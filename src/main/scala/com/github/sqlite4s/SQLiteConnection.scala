package com.github.sqlite4s

import java.io.{File, IOException}
import java.util
import java.util.Locale

import scala.scalanative.native._

import bindings.sqlite
import bindings.sqlite.SQLITE_CONSTANT._
import bindings.SQLITE_EXTENDED_RESULT_CODE._

import com.github.sqlite4s.c.util.CUtils
import Internal.mkLogMessage
import SQLITE_WRAPPER_ERROR_CODE._


/**
  * SQLiteConnection is a single connection to sqlite database. It wraps the <strong><code>sqlite3*</code></strong>
  * database handle from SQLite C Interface.
  * <p/>
  * Unless otherwise specified, methods are confined to the thread that was used to open the connection.
  * This means that an exception will be thrown if you try to access the method from a different thread.
  * <p/>
  * SQLiteConnection should be expicitly closed before the object is disposed. Failing to do so
  * may result in unpredictable behavior from SQLite.
  * <p/>
  * Once closed with {@link #dispose()}, the connection cannot be reused and the instance
  * should be forgotten.
  * <p/>
  * Several connections to the same database can be opened by creating several instances of SQLiteConnection.
  * <p/>
  * SQLiteConnection tracks all statements it had prepared. When connection is disposed,
  * it first tries to dispose all prepared statements. If there's an active transaction, it is rolled
  * back.
  *
  * @author Igor Sereda
  * @see SQLiteStatement
  * @see <a href="http://www.sqlite.org/c3ref/sqlite3.html">sqlite3*</a>
  */
object SQLiteConnection {

  sealed trait IHandle
  type Handle = Ptr[sqlite.sqlite3] with IHandle

  implicit class HandleWrapper(val handle: Handle) extends AnyVal {
    def asPtr(): Ptr[sqlite.sqlite3] = handle.asInstanceOf[Ptr[sqlite.sqlite3]]
  }

  val DEFAULT_DB_NAME = "main"
  val DEFAULT_DB_NAME_AS_CSTR = c"main"

  private val MAX_POOLED_DIRECT_BUFFER_SIZE = 1 << 20
  private val DEFAULT_STEPS_PER_CALLBACK = 1

  private def appendW(buf: java.lang.StringBuilder, what: String, width: Int, filler: Char): Unit = {
    buf.append(what)

    var i = what.length
    while (i < width)  {
      buf.append(filler)
      i += 1
    }
  }

  private var lastConnectionNumber = new java.util.concurrent.atomic.AtomicInteger(0)

  def nextConnectionNumber(): Int = lastConnectionNumber.incrementAndGet()

  // Added by DBO to enable/disable thread confinement checking
  private var confinementCheckingEnabled: Boolean = false

  def enableConfinementChecking(): Unit = this.synchronized { confinementCheckingEnabled = true }
  def disableConfinementChecking(): Unit = this.synchronized { confinementCheckingEnabled = false }
}

/**
  * Creates a connection to the database located in the specified file.
  * Database is not opened by the constructor, and the calling thread is insignificant.
  *
  * @param dbfile database file, or null to create an in-memory database
  */
final class SQLiteConnection(val myFile: File) extends Logging {
  logger.info(mkLogMessage(this.toString(), s"instantiated [$myFile]"))

  /**
    * An incremental number of the instance, used for debugging purposes.
    */
  private val myNumber = SQLiteConnection.nextConnectionNumber()
  /**
    * A lock for protecting statement registry & cache. Locking is needed
    * because dispose() may be called from another thread.
    */
  private val myLock = new AnyRef()
  /**
    * Confinement thread, set on open() call, cleared on dispose().
    */
  @volatile
  private var myConfinement: Thread = _
  /**
    * SQLite db handle.
    */
  private var myHandle: SQLiteConnection.Handle = _
  /**
    * When connection is disposed (closed), it cannot be used anymore.
    */
  private var myDisposed: Boolean = false
  /**
    * Statement registry. All statements that are not disposed are listed here.
    */
  final private val myStatements = new java.util.ArrayList[SQLiteStatement](100)
  final private val myBlobs = new java.util.ArrayList[SQLiteBlob](10)
  /**
    * Allocated buffers pool. Sorted by pool size.
    * todo pool size control
    */
  //final private val myBuffers = new util.ArrayList[DirectBuffer](10)

  /**
    * Sum of myBuffer sizes
    */
  private var myBuffersTotalSize = 0
  /**
    * Compiled statement cache. Maps SQL string into a valid SQLite handle.
    * <p/>
    * When cached handle is used, it is removed from the cache and placed into SQLiteStatement. When SQLiteStatement
    * is disposed, the handle is placed back into cache, unless there's another statement already created for the
    * same SQL.
    */
  //final private val myStatementCache = new collection.mutable.HashMap[SQLParts, SQLiteStatement.HandleBox]()
  final private val myStatementCache = new java.util.HashMap[SQLParts, SQLiteStatement.HandleBox]()
  /**
    * This controller provides service for cached statements.
    */
  final private val myCachedController = new CachedController(this)
  /**
    * This controller provides service for statements that aren't cached.
    */
  final private val myUncachedController = new UncachedController(this)
  /**
    * This object contains several variables that assist in calling native methods
    * and allow to avoid unnecessary memory allocation.
    */
  final private val mySQLiteWrapper = new SQLiteWrapper()
  /**
    * This object is initialized when INTARRAY module is added to the connection.
    */
  //--//private var myIntArrayModule = null
  /**
    * Native byte buffer to communicate between Java and SQLite to report progress and cancel execution.
    */
  private[sqlite4s] var myProgressHandler: ProgressHandler = _
  /**
    * May be set only before first exec() or step().
    */
  private var myStepsPerCallback: Int = SQLiteConnection.DEFAULT_STEPS_PER_CALLBACK
  /**
    * If initialized, all subsequent statements are analyzed for speed and stats are
    * collected in the profiler.
    */
  private var myProfiler: SQLiteProfiler = _
  /**
    * Contains inactive (initialized, but not in use) long arrays, mapped by the name.
    */
  //final private val myLongArrays = FastMap.newInstance
  /**
    * Incremental number used in generation of long array names.
    */
  private var myLongArrayCounter = 0
  /**
    * Flags used to open this connection
    * Protected by myLock
    */
  private var myOpenFlags = 0

  /**
    * Creates a connection to an in-memory temporary database.
    * Database is not opened by the constructor, and the calling thread is insignificant.
    *
    * @see #SQLiteConnection(java.io.File)
    */
  def this() {
    this(null)
  }

  def connectionHandle(): SQLiteConnection.Handle = myHandle

  @throws[SQLiteException]
  // TODO: rename _getHandleOrFail
  private[sqlite4s] def handle(): SQLiteConnection.Handle = myLock synchronized {

    if (myDisposed) throw new SQLiteException(WRAPPER_MISUSE, "connection is disposed")

    val handle = myHandle
    if (handle == null) throw new SQLiteException(WRAPPER_NOT_OPENED, null)

    handle
  }

  protected[sqlite4s] def getSQLiteWrapper(): SQLiteWrapper = mySQLiteWrapper

  /**
    * Returns the database file. This method is <strong>thread-safe</strong>.
    *
    * @return the file that hosts the database, or null if database is in memory
    */
  def getDatabaseFile(): File = myFile

  /**
    * Checks whether this connection is to an in-memory database. This method is <strong>thread-safe</strong>.
    *
    * @return true if the connection is to the memory database
    */
  def isMemoryDatabase(): Boolean = myFile == null

  /**
    * Sets the frequency of database callbacks during long-running SQL statements. Database callbacks
    * are currently used to check if the statement has been cancelled.
    * <p/>
    * This method is <strong>partially thread-safe</strong>: it may be called from a non-confining thread
    * before connection is opened. After connection is opened, is should be called from the confining thread and
    * before any statement has been executed.
    * <p/>
    *
    * @param stepsPerCallback the number of internal SQLite cycles in between calls to the progress callback (default 1)
    * @see <a href="http://www.sqlite.org/c3ref/progress_handler.html">sqlite3_progress_callback</a>
    */
  def setStepsPerCallback(stepsPerCallback: Int): Unit = {
    if (stepsPerCallback > 0) myStepsPerCallback = stepsPerCallback
  }

  /**
    * Allows the size of various constructs for the current connection to be limited.
    *
    * @param id     identifies the class of the constructs to be limited (use { @code SQLITE_LIMIT_*} constants from { @link SQLiteConstants}).
    * @param newVal the new limit
    * @return previous limit
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/limit.html">sqlite3_limit</a>
    */
  @throws[SQLiteException]
  def setLimit(id: Int, newVal: Int): Int = {
    checkThread()
    sqlite.sqlite3_limit(handle, id, newVal)
  }

  /**
    * Returns the current limit for the size of a various constructs for the current connection.
    *
    * @param id identifies the class of the constructs to be limited (use { @code SQLITE_LIMIT_*} constants from { @link SQLiteConstants}).
    * @return current limit
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/limit.html">sqlite3_limit</a>
    */
  @throws[SQLiteException]
  def getLimit(id: Int): Int = {
    checkThread()
    sqlite.sqlite3_limit(handle, id, -1)
  }

  /**
    * Opens the connection, optionally creating the database.
    * <p/>
    * If connection is already open, fails gracefully, allowing connection can be used further.
    * <p/>
    * This method "confines" the connection to the thread in which it has been called. Most of the following
    * method calls to this connection and to its statements should be made only from that thread, otherwise
    * an exception is thrown.
    * <p/>
    * If allowCreate parameter is false, and database file does not exist, method fails with an exception.
    * <p/>
    *
    * @param allowCreate if true, database file may be created. For an in-memory database, this parameter must
    *                    be true.
    * @return this connection
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/open.html">sqlite3_open_v2</a>
    */
  @throws[SQLiteException]
  def open(allowCreate: Boolean): SQLiteConnection = {
    var flags = SQLITE_OPEN_READWRITE

    if (allowCreate) flags |= SQLITE_OPEN_CREATE
    else if (isMemoryDatabase()) throw new SQLiteException(WRAPPER_WEIRD, "cannot open memory database without creation")

    open0(flags)

    this
  }

  /**
    * Opens the connection, creating database if needed. See {@link #open(boolean)} for a full description.
    *
    * @return this connection
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def open(): SQLiteConnection = open(true)

  /**
    * Opens the connection is read-only mode. Not applicable for an in-memory database.
    * See {@link #open(boolean)} for a full description.
    *
    * @return this connection
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def openReadonly(): SQLiteConnection = {
    if (isMemoryDatabase) throw new SQLiteException(WRAPPER_WEIRD, "cannot open memory database in read-only mode")
    open0(SQLITE_OPEN_READONLY)
    this
  }

  /**
    * Opens the connection with the specified flags for the sqlite3_open_v2 method. The flags SQLITE_OPEN_xxx are defined
    * in {@link SQLiteConstants} and can be ORed together.
    * <p/>
    * This method is provided for future versions compatibility and for open options not otherwise supported by
    * sqlite4java. Use this method only if other open() methods are not sufficient.
    * <p/>
    * In all other respects, this method works exactly like {@link #open(boolean)}, consult documentation to that method
    * for details.
    *
    * @param flags integer flags as defined by sqlite3_open_v2 function
    * @return this connection
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/open.html">sqlite3_open_v2</a>
    */
  @throws[SQLiteException]
  def openV2(flags: Int): SQLiteConnection = {
    open0(flags)
    this
  }

  /**
    * Tells whether connection is open. This method is <strong>thread-safe</strong>.
    *
    * @return true if this connection was successfully opened and has not been disposed
    */
  def isOpen(): Boolean = {
    myLock synchronized
      myHandle != null && !myDisposed

  }

  /**
    * Checks if the connection has been disposed. This method is <strong>thread-safe</strong>.
    *
    * @return true if this connection has been disposed. Disposed connections can't be used for anything.
    */
  def isDisposed: Boolean = {
    myLock synchronized
      myDisposed

  }

  /**
    * <p>Checks if a database accessed through this connection is read-only.</p>
    *
    * <p>A database can be read-only if:</p>
    * <ul>
    * <li>it was opened with read-only flag (using {@link #openReadonly()} or
    * {@link #openV2(int)} with {@link SQLiteConstants#SQLITE_OPEN_READONLY}), or</li>
    * <li>if the file or file system is read-only.</li>
    * </ul>
    *
    * @param dbName database name, or null for the main database
    * @return { @code true} if the specified database is read-only
    * @throws SQLiteException if the requested database name cannot be found
    * @see <a href="http://www.sqlite.org/c3ref/db_readonly.html">sqlite3_db_readonly</a>
    */
  @throws[SQLiteException]
  def isReadOnly(dbName: String): Boolean = {
    checkThread()
    logger.trace(mkLogMessage(s"calling sqlite3_db_readonly [$dbName]"))

    val realDbName = dbName //if (dbName == null) "main" else dbName
    val result = Zone { implicit z =>
      sqlite.sqlite3_db_readonly(handle(), CUtils.toCString(realDbName))
    }

    if (result == -1) throw new SQLiteException(result, s"$dbName is not a valid database name")
    else {
      assert(result == 0 || result == 1, s"unexpected RC=$result")
      result == 1
    }
  }

  /**
    * <p>Attempts to flush dirty pages in the pager-cache. Dirty pages may exist
    * during a write-transaction. This method may need to acquire extra database
    * locks before it can flush the dirty pages.</p>
    *
    * @throws SQLiteException If method cannot acquire extra database locks, or if the call
    *                         violates the contract of this class.
    * @see <a href="https://www.sqlite.org/c3ref/db_cacheflush.html">sqlite3_db_cacheflush</a>
    */
  @throws[SQLiteException]
  def flush(): Unit = {
    checkThread()
    logger.trace(mkLogMessage("calling sqlite3_db_cacheflush() via flush()"))
    val result = sqlite.sqlite3_db_cacheflush(handle)
    throwResult(result, "flush()")
  }

  /**
    * <p>Attempts to flush dirty pages in the pager-cache. Dirty pages may exist
    * during a write-transaction. This method may attempt to acquire extra database
    * locks before it can flush the dirty pages. On failure, a warning message is logged.</p>
    *
    * @see <a href="https://www.sqlite.org/c3ref/db_cacheflush.html">sqlite3_db_cacheflush</a>
    */
  def safeFlush(): Unit = {
    try
      flush()
    catch {
      case e: SQLiteException =>
        logger.warn(mkLogMessage(s"error during flush() - ${e.getMessage}"))
    }
  }

  /**
    * <p>Checks if this connection is read-only. This is a convenience method for calling </p>
    * <p>A database can be read-only if:</p>
    * <ul>
    * <li>it was opened with read-only flag (using {@link #openReadonly()} or
    * {@link #openV2(int)} with {@link SQLiteConstants#SQLITE_OPEN_READONLY}), or</li>
    * <li>if the file or file system is read-only.</li>
    * </ul>
    *
    * <p>This is a convenience method that calls {@link #isReadOnly(String)} with {@code null} parameter,
    * checking the status of the main database of this connection.</p>
    *
    * @return { @code true} if the specified database is read-only
    * @throws SQLiteException if the requested database name cannot be found
    * @see #isReadOnly(String)
    * @see <a href="http://www.sqlite.org/c3ref/db_readonly.html">sqlite3_db_readonly</a>
    */
  @throws[SQLiteException]
  def isReadOnly(): Boolean = isReadOnly(null)

  /**
    * Returns the flags that were used to open this connection.
    *
    * @return Flags that were used to open the connection.
    */
  def getOpenFlags(): Int = {
    myLock synchronized {
      myOpenFlags
    }
  }

  /**
    * Closes this connection and disposes all related resources. After dispose() is called, the connection
    * cannot be used and the instance should be forgotten.
    * <p/>
    * Calling this method on an already disposed connection does nothing.
    * <p/>
    * If called from a different thread rather from the thread where the connection was opened, this method
    * does nothing. (It used to attempt connection disposal anyway, but that could lead to JVM crash.)
    * <p/>
    * It is better to call dispose() from a different thread, than not to call it at all.
    * <p/>
    * This method does not throw exception even if SQLite returns an error.
    * <p/>
    *
    * @see <a href="http://www.sqlite.org/c3ref/close.html">sqlite3_close</a>
    */
  def dispose(): Unit = {
    var handle: SQLiteConnection.Handle = null
    myLock synchronized {
      if (myDisposed) return

      val confinement = myConfinement
      if (confinement != null && (confinement != Thread.currentThread)) {
        SQLiteException.logWarnOrThrowError(
          msg => logger.warn(msg),
          "will not dispose from a non-confining thread",
          true
        )
        return
      }

      myDisposed = true
      handle = myHandle
      myHandle = null
      myOpenFlags = 0
    }

    if (handle == null) return

    logger.trace(mkLogMessage("disposing"))
    finalizeStatements()
    finalizeBlobs()

    //---//finalizeBuffers()
    //---//finalizeArrays()
    finalizeProgressHandler(handle)

    val rc = sqlite.sqlite3_close(handle)
    // rc may be SQLiteConstants.Result.SQLITE_BUSY if statements are open
    if (rc != SQLITE_OK) {
      val errmsg = try
        fromCString(sqlite.sqlite3_errmsg(handle))
      catch {
        case e: Exception =>
          logger.warn(mkLogMessage("cannot get sqlite3_errmsg"), e)
          null
      }

      logger.warn(mkLogMessage(s"close error RC=${rc + (if (errmsg == null) "" else s" is: $errmsg")}"))
    }
    logger.info(mkLogMessage("connection closed"))

    myConfinement = null
  }

  /**
    * Executes SQL. This method is normally used for DDL, transaction control and similar SQL statements.
    * For querying database and for DML statements with parameters, use {@link #prepare}.
    * <p/>
    * Several statements, delimited by a semicolon, can be executed with a single call.
    * <p/>
    * Do not use this method if your SQL contains non-ASCII characters!
    * <p/>
    *
    * @param sql an SQL statement
    * @return this connection
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/exec.html">sqlite3_exec</a>
    */
  @throws[SQLiteException]
  def exec(sql: String): SQLiteConnection = {
    checkThread()
    val profiler = myProfiler
    logger.trace(mkLogMessage(s"exec [$sql]"))
    val handle = this.handle()

    // FIXME: DBO => do we really need this?what is the associated overhead?
    val ph = _getOrInstallProgressHandler()
    ph.reset()

    try {
      val from = if (profiler == null) 0 else System.nanoTime

      val (rc, errorOpt) = SQLiteWrapper.sqlite3Exec(handle, sql)

      if (profiler != null) profiler.reportExec(sql, from, System.nanoTime, rc)

      throwResult(rc, "exec()", errorOpt.orNull)
    } finally {
      logger.trace(mkLogMessage(s"exec [$sql]"))
      //---//if (SQLiteException.isFineLogging) SQLiteException.logFine(this, "exec [" + sql + "]: " + ph.getSteps + " steps")
      ph.reset()
    }

    this
  }

  @throws[SQLiteException]
  private def _getOrInstallProgressHandler(): ProgressHandler = {
    var handler = myProgressHandler

    if (handler == null) {
      handler = mySQLiteWrapper.install_progress_handler(handle(), myStepsPerCallback)

      if (handler == null) {
        logger.warn(Internal.mkLogMessage(this.toString(), s"cannot install progress handler [${mySQLiteWrapper.getLastReturnCode()}]"))
        handler = ProgressHandler.DISPOSED
      }

      myProgressHandler = handler
    }

    assert(handler != null, "handler must not be null")

    handler
  }

  /**
    * Returns meta information about a specific column of a database table.
    *
    * @param dbName     database name or { @code null}
    * @param tableName  table name
    * @param columnName column name
    * @return SQLiteColumnMetadata column metadata
    * @throws SQLiteException if specified table is actually a view, or if error occurs during this process, or if the requested table or column cannot be found, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/table_column_metadata.html">sqlite3_table_column_metadata</a>
    */
  @throws[SQLiteException]
  def getTableColumnMetadata(dbName: String, tableName: String, columnName: String): SQLiteColumnMetadata = {
    checkThread()
    logger.trace(mkLogMessage(s"calling sqlite3_table_column_metadata [$dbName,$tableName,$columnName]"))

    SQLiteWrapper.sqlite3TableColumnMetadata(this.handle(), dbName, tableName, columnName)
  }

  /**
    * Prepares an SQL statement. Prepared SQL statement can be used further for putting data into
    * the database and for querying data.
    * <p/>
    * Prepared statements are normally cached by the connection, unless you set <code>cached</code> parameter
    * to false. Because parsing SQL is costly, caching should be used in cases where a single SQL can be
    * executed more than once.
    * <p/>
    * Cached statements are cleared of state before or after they are used.
    * <p/>
    * SQLParts is used to contains the SQL query, yet there are convenience methods that accept String.
    * <p/>
    * Returned statement must be disposed when the calling code is done with it, whether it was cached or not.
    * <p/>
    *
    * @param sql    the SQL statement, not null
    * @param cached if true, the statement handle will be cached by the connection
    * @param flags  A bit array which consists of 0 or more
    *               <a href="https://www.sqlite.org/c3ref/c_prepare_persistent.html">SQLITE_PREPARE_*</a> flags. Note: if statement
    *               is already cached, flags will be ignored.
    * @return an instance of { @link SQLiteStatement}
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/prepare.html">sqlite3_prepare_v3</a>
    */
  @throws[SQLiteException]
  def prepare(sql: SQLParts, cached: Boolean, flags: Int): SQLiteStatement = {
    require(sql != null, "sql is null")
//println("begin")
    checkThread()
//var z = 0
    logger.trace(mkLogMessage(s"prepare [$sql]"))
//println(z); z += 1
    val profiler = myProfiler
    var handle: SQLiteConnection.Handle = null
    var stmt: SQLiteStatement.Handle = null
    var fixedKey: SQLParts = null
//    println(z); z += 1
    myLock synchronized {
//      println("zzzzz " + cached)
      if (cached) { // while the statement is in work, it is removed from cache. it is put back in cache by SQLiteStatement.dispose().

        //val stmtBox = if (myStatementCache.containsKey(sql)) myStatementCache.get(sql) else null

        // FIXME: this is a workaround because containsKey crashes the program
        /*var hasKey = false
        var stmtBox: SQLiteStatement.HandleBox = null
        try {
          val iter = myStatementCache.entrySet().iterator()
          while (iter.hasNext && !hasKey) {
            val elem = iter.next()
            val str = elem.getKey.toString()
            hasKey = str == sql.toString()
            if (hasKey) {
              fixedKey = elem.getKey
              stmtBox = elem.getValue
            }
          }
        } catch {
          case e: NoSuchElementException => hasKey = false
        }
        println("hasKey: "+ hasKey)
*/
        val stmtBox = myStatementCache.get(sql)
        if (stmtBox != null) {
//          println("a")
          fixedKey = sql.getFixedParts()
          assert(fixedKey != null)
          assert(fixedKey.isFixed(), sql)
//          println("b")
          stmt = stmtBox.handle
//          println("c")
          //---//if (stmt != null) e.setValue(null)
          myStatementCache.synchronized {
            if (stmt != null) myStatementCache.remove(sql)
          }
//          println("d")
        }
      }
//      println("yyyyyyyy")
      handle = this.handle()
    }
//    println(z); z += 1
    if (stmt == null) {
      logger.trace(mkLogMessage(s"calling sqlite3_prepare_v2 for [$sql]"))
      val from = if (profiler == null) 0
      else System.nanoTime
      val sqlString = sql.toString()
      if (sqlString.trim.length == 0) throw new SQLiteException(WRAPPER_USER_ERROR, "empty SQL")
      stmt = mySQLiteWrapper.sqlite3PrepareV3(handle, sqlString, flags)
      val rc = mySQLiteWrapper.getLastReturnCode()
      if (profiler != null) profiler.reportPrepare(sqlString, from, System.nanoTime, rc)
      throwResult(rc, "prepare()", sql)
      if (stmt == null) throw new SQLiteException(WRAPPER_WEIRD, "sqlite did not return stmt")
    }
    else logger.trace(mkLogMessage(s"using cached stmt for [$sql]"))
//    println(z); z += 1
    var statement: SQLiteStatement = null
    myLock synchronized { // the connection may close while prepare in progress
      // most probably that would throw SQLiteException earlier, but we'll check anyway
      if (myHandle != null) {
        val controller = if (cached) myCachedController
        else myUncachedController
        if (fixedKey == null) fixedKey = sql.getFixedParts()
        statement = new SQLiteStatement(controller, stmt, fixedKey, myProfiler)
        myStatements.add(statement)
      }
      else logger.warn(mkLogMessage(s"connection disposed while preparing statement for [$sql]"))
    }
//    println(z); z += 1
    if (statement == null) { // connection closed
      try
        throwResult(sqlite.sqlite3_finalize(stmt), "finalize() in prepare()")
      catch {
        case e: Exception => // ignore
      }
      throw new SQLiteException(WRAPPER_NOT_OPENED, "connection disposed")
    }
//    println("end")
    statement
  }

  /**
    * Convenience method that prepares a cached statement for the given SQL. See {@link #prepare(SQLParts, boolean, int)}
    * for details.
    *
    * @param sql an SQL statement, not null
    * @return an instance of { @link SQLiteStatement}
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def prepare(sql: String): SQLiteStatement = prepare(sql, true)

  /**
    * Convenience method that prepares a cached statement for the given SQL. See {@link #prepare(SQLParts, boolean, int)}
    * for details. This variant allows flags to be passed as a parameter.
    *
    * @param sql an SQL statement, not null
    * @return an instance of { @link SQLiteStatement}
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def prepare(sql: String, flags: Int): SQLiteStatement = prepare(sql, true, flags)

  /**
    * Convenience method that prepares a statement for the given String-based SQL. See {@link #prepare(SQLParts, boolean, int)}
    * for details.
    *
    * @param sql    the SQL statement, not null
    * @param cached if true, the statement handle will be cached by the connection
    * @return an instance of { @link SQLiteStatement}
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def prepare(sql: String, cached: Boolean): SQLiteStatement = prepare(new SQLParts(sql), cached, 0)

  /**
    * Convenience method that prepares a statement for the given String-based SQL. See {@link #prepare(SQLParts, boolean, int)}
    * for details. This variant allows flags to be passed as a parameter.
    *
    * @param sql    the SQL statement, not null
    * @param cached if true, the statement handle will be cached by the connection
    * @return an instance of { @link SQLiteStatement}
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def prepare(sql: String, cached: Boolean, flags: Int): SQLiteStatement = prepare(new SQLParts(sql), cached, flags)

  /**
    * Convenience method that prepares a cached statement for the given SQL. See {@link #prepare(SQLParts, boolean, int)}
    * for details.
    *
    * @param sql the SQL statement, not null
    * @return an instance of { @link SQLiteStatement}
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def prepare(sql: SQLParts): SQLiteStatement = prepare(sql, true, 0)

  /**
    * Convenience method that prepares a statement for the given SQL. See {@link #prepare(SQLParts, boolean, int)}
    * for details.
    *
    * @param sql    the SQL statement, not null
    * @param cached if true, the statement handle will be cached by the connection
    * @return an instance of { @link SQLiteStatement}
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def prepare(sql: SQLParts, cached: Boolean): SQLiteStatement = prepare(sql, cached, 0)

  /**
    * Convenience method that prepares a cached statement for the given SQL. See {@link #prepare(SQLParts, boolean, int)}
    * for details. This variant allows flags to be passed as a parameter.
    *
    * @param sql   the SQL statement, not null
    * @param flags The flags parameter use in sqlite3_prepare_v3()
    * @return an instance of { @link SQLiteStatement}
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def prepare(sql: SQLParts, flags: Int): SQLiteStatement = prepare(sql, true, flags)

  /**
    * Opens a BLOB for reading or writing. This method returns an instance of {@link SQLiteBlob}, which can
    * be used to read or write a single table cell with a BLOB value. After operations are done, the blob should
    * be disposed.
    * <p/>
    * See SQLite documentation about transactional behavior of the corresponding methods.
    * <p/>
    *
    * @param dbname      database name, or null for the current database
    * @param table       table name, not null
    * @param column      column name, not null
    * @param rowid       row id
    * @param writeAccess if true, write access is requested
    * @return an instance of SQLiteBlob for incremental reading or writing
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/blob_open.html">sqlite3_blob_open</a>
    */
  @throws[SQLiteException]
  def blob(dbname: String, table: String, column: String, rowid: Long, writeAccess: Boolean): SQLiteBlob = {
    checkThread()

    logger.trace(mkLogMessage(s"openBlob [$dbname,$table,$column,$rowid,$writeAccess]"))
    val handle = this.handle()
    val blob = mySQLiteWrapper.sqlite3BlobOpen(handle, dbname, table, column, rowid, writeAccess)
    throwResult(mySQLiteWrapper.getLastReturnCode(), "openBlob()", null)

    if (blob == null) throw new SQLiteException(WRAPPER_WEIRD, "sqlite did not return blob")

    var result: SQLiteBlob = null
    myLock synchronized {
      // the connection may close while openBlob in progress
      if (myHandle != null) {
        result = new SQLiteBlob(myUncachedController, blob, dbname, table, column, rowid, writeAccess)
        myBlobs.add(result)
      }
      else logger.warn(mkLogMessage("connection disposed while opening blob"))
    }

    if (result == null) {
      try
        throwResult(sqlite.sqlite3_blob_close(blob), "blob_close() in prepare()")
      catch {
        case e: Exception => // ignor
      }
      throw new SQLiteException(WRAPPER_NOT_OPENED, "connection disposed")
    }

    result
  }

  /**
    * Convenience method for calling blob() on the currently selected database.
    * See {@link #blob(String, String, String, long, boolean)} for detailed description.
    *
    * @param table       table name, not null
    * @param column      column name, not null
    * @param rowid       row id
    * @param writeAccess if true, write access is requested
    * @return an instance of SQLiteBlob for incremental reading or writing
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def blob(table: String, column: String, rowid: Long, writeAccess: Boolean): SQLiteBlob = {
    blob(null, table, column, rowid, writeAccess)
  }

  /**
    * Sets "busy timeout" for this connection. If timeout is defined, then SQLite will not wait to lock
    * the database for more than the specified number of milliseconds.
    * <p/>
    * By default, the timeout is not set.
    *
    * @param millis number of milliseconds for the busy timeout, or 0 to disable the timeout
    * @return this connection
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/busy_timeout.html">sqlite3_busy_timeout</a>
    */
  @throws[SQLiteException]
  def setBusyTimeout(millis: Long): SQLiteConnection = {
    checkThread()
    val rc = sqlite.sqlite3_busy_timeout(handle, millis.toInt)
    throwResult(rc, "setBusyTimeout")
    this
  }

  /**
    * Checks if the database is in the auto-commit mode. In auto-commit mode, transaction is ended after execution of
    * every statement.
    * <p/>
    *
    * @return true if the connection is in auto-commit mode
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/get_autocommit.html">sqlite3_get_autocommit</a>
    */
  @throws[SQLiteException]
  def getAutoCommit(): Boolean = {
    checkThread()
    val r = sqlite.sqlite3_get_autocommit(handle)
    r != 0
  }

  /**
    * Returns the ROWID of the row, last inserted in this connection (regardless of which statement was used).
    * If the table has a column of type INTEGER PRIMARY KEY, then that column contains the ROWID. See SQLite docs.
    * <p/>
    * You can also use "last_insert_rowid()" function in SQL statements following the insert.
    *
    * @return the rowid of the last successful insert, or 0 if nothing has been inserted in this connection
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/last_insert_rowid.html">sqlite3_last_insert_rowid</a>
    */
  @throws[SQLiteException]
  def getLastInsertId(): Long = {
    checkThread()
    val id = sqlite.sqlite3_last_insert_rowid(handle)
    id
  }

  /**
    * This method returns the number of database rows that were changed or inserted or deleted by the most
    * recently completed SQL statement in this connection. See SQLite documentation for details.
    *
    * @return the number of affected rows, or 0
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/changes.html">sqlite3_changes</a>
    */
  @throws[SQLiteException]
  def getChanges(): Int = {
    checkThread()
    sqlite.sqlite3_changes(handle)
  }

  /**
    * This method returns the total number of database rows that were changed or inserted or deleted since
    * this connection was opened. See SQLite documentation for details.
    *
    * @return the total number of affected rows, or 0
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/total_changes.html">sqlite3_total_changes</a>
    */
  @throws[SQLiteException]
  def getTotalChanges(): Int = {
    checkThread()
    sqlite.sqlite3_total_changes(handle)
  }

  /**
    * This method can be called to interrupt a currently long-running SQL statement, causing it to fail
    * with an exception.
    * <p/>
    * This method is <strong>thread-safe</strong>.
    * <p/>
    * There are some important implications from using this method, see SQLite docs.
    *
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/interrupt.html">sqlite3_interrupt</a>
    */
  @throws[SQLiteException]
  def interrupt(): Unit = {
    sqlite.sqlite3_interrupt(handle)
  }

  /**
    * This method returns the error code of the most recently failed operation. However, this method is
    * rarely needed, as the error code can usually be received from {@link SQLiteException#getErrorCode}.
    *
    * @return error code, or 0
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/errcode.html">sqlite3_errcode</a>
    * @see <a href="http://www.sqlite.org/c3ref/errcode.html">sqlite3_extended_errcode</a>
    */
  @throws[SQLiteException]
  def getErrorCode(): Int = {
    checkThread()
    sqlite.sqlite3_errcode(handle)
  }

  /**
    * This method returns the English error message that describes the error returned by {@link #getErrorCode}.
    *
    * @return error message, or null
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/errcode.html">sqlite3_errmsg</a>
    */
  @throws[SQLiteException]
  def getErrorMessage(): String = {
    checkThread()
    fromCString(sqlite.sqlite3_errmsg(this.handle()))
  }

  /**
    * Starts SQL profiling and returns the profiler class. If profiling is already started, returns the
    * profiler.
    * <p/>
    * This method is thread-safe, in a sense that it can be called from non-session threads. It's not
    * strongly synchronized, so calling it concurrently may result in duplicate profilers.
    * <p/>
    * Only instances of SQLiteStatement created after this method is called will be profiled (whether
    * SQLite statement is cached or not).
    *
    * @return the profiler, which will collect stats for all subsequent operations until { @link #stopProfiling}
    *         is called.
    */
  def profile(): SQLiteProfiler = {
    if (myProfiler == null) {
      myProfiler =  new SQLiteProfiler()
    }
    myProfiler
  }

  /**
    * Stops the profiling and returns the profiler instance with data. If the profiling was not running,
    * returns null.
    * <p/>
    * This method is thread-safe, in a sense that it can be called from non-session threads. It's not
    * strongly synchronized, so calling it concurrently may result in race conditions.
    *
    * @return the profiler with collected data, or null
    */
  def stopProfiling(): SQLiteProfiler = {
    val profiler = myProfiler
    myProfiler = null
    profiler
  }

  /**
    * Creates a virtual table within the current session, to represent an array of long values (functionality provided
    * by test_intarray module from SQLite sources). After SQLiteLongArray
    * is created, it can be bound consequently several times to a long[], and the virtual table can be used in any SQL.
    * This provides means to make queries with array parameters. For example:
    * <pre>
    * long[] itemIds = ...;
    * SQLiteLongArray array = connection.createArray();
    * SQLiteStatement st = connection.prepare("SELECT * FROM items WHERE itemId IN " + array.getName());
    * array.bind(itemIds);
    * while (st.step()) {
    * // read values
    * }
    * st.dispose();
    * array.dispose();
    * <p/>
    * The array contents is bound using {@link SQLiteLongArray#bind} methods. Binding an array is not a transactional
    * operation; it does not start or stop a transaction, and contents of the array is not affected by ROLLBACK.
    * <p/>
    * You can execute any SQL using array's name ({@link SQLiteLongArray#getName}) as the table name. The actual table
    * is a VIRTUAL TABLE, residing in TEMP database. (Because of that, temp database may be created on disk - you can
    * change that using PRAGMA TEMP_STORE.)
    * <p/>
    * It is possible to execute an SQL that contains several virtual array tables.
    * <p/>
    * Note that the virtual array table does not have a primary key (bound values may have duplicates and come
    * in random order), so be careful about performance.
    * <p/>
    * SQLiteLongArray are cached by the SQLiteConnection, unless <code>cached</code> parameter is set to <code>false</code>.
    * When cached SQLiteLongArray is disposed, it is kept by the connection for further reuse. When a non-cached
    * SQLiteLongArray is disposed, its table is deleted from the TEMP database.
    * <p/>
    * <strong>Caution:</strong> It's possible to use DROP TABLE on the array virtual table; doing that will make
    * SQL statements that use the table invalid.
    *
    * @param name   the name of the table, must be a correct SQL table name, and contains only ASCII characters. If null,
    *               a temporary name will be provided automatically (can be later retrieved via { @link SQLiteLongArray#getName}.
    * @param cached if true, then a cached array will be used, thus reducing the number of virtual tables and schema
    *               changes. If cached is true and a name is given and there's no free array with that name, a new array will be created.
    *               If cached is true and name is null, then any free array will be allocated.
    * @return an instance of SQLiteLongArray, wrapping an empty (unbound) virtual array table
    * @throws SQLiteException if name is already in use, or if other problem happens
    * @see <a href="http://www.sqlite.org/src/artifact/489edb9068bb926583445cb02589344961054207">test_intarray.h</a>
    */
  /*@throws[SQLiteException]
  def createArray(name: String, cached: Boolean): Nothing = {
    checkThread()
    if (Internal.isFineLogging) Internal.logFine(this, "createArray [" + name + "," + cached + "]")
    if (!cached && name != null && myLongArrays.containsKey(name)) {
      Internal.logWarn(this, "using cached array in lieu of passed parameter, because name already in use")
      cached = true
    }
    if (!cached) return createArray0(name, myUncachedController)
    if (name == null && !myLongArrays.isEmpty) name = myLongArrays.head.getNext.getKey
    val array = if (name == null) null
    // TODO: check if removing element can lead to perf. issues
    else myLongArrays.remove(name)
    if (array != null) return new Nothing(myCachedController, array, name)
    createArray0(name, myCachedController)
  }

  /**
    * Creates a virtual table within the current session, to represent an array of long values (functionality provided
    * by test_intarray module from SQLite sources). After SQLiteLongArray
    * is created, it can be bound consequently several times to a long[], and the virtual table can be used in any SQL.
    * <p/>
    * This is a convenience method that creates an array with an arbitrary name and cached by the connection,
    * equal to call to <code>createArray(null, true)</code>. See {@link #createArray(String, boolean)} for details.
    *
    * @return an instance of SQLiteLongArray, wrapping an empty (unbound) virtual array table
    * @throws SQLiteException in case any problem is reported by SQLite, or general contract is broken
    */
  @throws[SQLiteException]
  def createArray: Nothing = createArray(null, true)
*/

  /**
    * <p>
    * Initializes backup of the database with the given name from the current connection to the specified file.
    * </p><p>
    * This method creates a new SQLite connection to the destination file, opens it with the specified flags and
    * initializes an instance of {@link SQLiteBackup} for the source and destination connections.
    * </p><p>Each successful call
    * to <code>initializeBackup</code> must be followed by a call to {@link com.almworks.sqlite4java.SQLiteBackup#dispose}.
    * </p>
    * <p>The name of the source database is usually <code>"main"</code> for the main database, or <code>"temp"</code>
    * for the temporary database. It can also be the name used in the ATTACH clause for an attached database.
    * </p>
    * <p>
    * The database that will hold the backup in the destination file is always named <code>"main"</code>.
    * </p>
    *
    * @param sourceDbName      name of the source database in this connection (usually "main")
    * @param destinationDbFile file to store the backup or <strong>null</strong> if you want to back up into a in-memory database
    * @param flags             flags for opening the connection to the destination database - see { @link #openV2(int)} for details
    * @return a new instance of { @link SQLiteBackup}
    * @throws SQLiteException if SQLite return an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/backup_finish.html#sqlite3backupinit">sqlite3_backup_init</a>
    */
  @throws[SQLiteException]
  def initializeBackup(sourceDbName: String, destinationDbFile: File, flags: Int): SQLiteBackup = {
    checkThread()

    val destination = new SQLiteConnection(destinationDbFile).openV2(flags)
    logger.trace(Internal.mkLogMessage(this.toString(), "initializeBackup to " + destination))

    val sourceDb = handle()
    val destinationDb = destination.handle()

    Zone { z =>
      val sourceDbNameAsCStr = CUtils.toCString(sourceDbName)(z)
      val backup = sqlite.sqlite3_backup_init(
        destinationDb,
        SQLiteConnection.DEFAULT_DB_NAME_AS_CSTR,
        sourceDb,
        sourceDbNameAsCStr
      )

      if (backup == null) {
        try {
          val errorCode = destination.getErrorCode()
          destination.throwResult(errorCode, "backup initialization")
          throw new SQLiteException(SQLITE_WRAPPER_ERROR_CODE.WRAPPER_WEIRD, "backup failed to start but error code is 0")
        } finally destination.dispose()
      }

      val destinationController = destination.myUncachedController
      new SQLiteBackup(myUncachedController, destinationController, backup.asInstanceOf[SQLiteBackup.Handle], this, destination)
    }
  }

  /**
    * <p>
    * Initializes backup of the database from this connection to the specified file.
    * </p>
    * <p>
    * This is a convenience method, equivalent to
    * <code>initializeBackup("main", destinationDbFile, SQLITE_OPEN_CREATE | SQLITE_OPEN_READWRITE)</code>
    * </p>
    *
    * @param destinationDbFile file to store the backup or <strong>null</strong> if you want to back up into a in-memory database
    * @return an instance of { @link SQLiteBackup}
    * @throws SQLiteException if SQLite return an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def initializeBackup(destinationDbFile: File): SQLiteBackup = {
    initializeBackup(SQLiteConnection.DEFAULT_DB_NAME, destinationDbFile, SQLITE_OPEN_CREATE | SQLITE_OPEN_READWRITE)
  }

  /*
  /**
    * Enables or disables SQLite extension loading for this connection. By default, extension loading is disabled.
    *
    * @param enabled if true, extensions can be loaded via { @link #loadExtension} function
    * @throws SQLiteException if extension loading flag cannot be changed
    * @see <a href="http://www.sqlite.org/c3ref/enable_load_extension.html">enable_load_extension</a>
    */
  @throws[SQLiteException]
  def setExtensionLoadingEnabled(enabled: Boolean): Unit = {
    checkThread()
    val rc = sqlite.sqlite3_enable_load_extension(handle, if (enabled) 1
    else 0)
    throwResult(rc, "enableLoadExtension()")
    if (Internal.isFineLogging) Internal.logFine(this, if (enabled) "Extension load enabled"
    else "Extension load disabled")
  }

  /**
    * Loads an SQLite extension library. Extension loading must be enabled with {@link #setExtensionLoadingEnabled(boolean)}
    * prior to calling this method.
    *
    * @param extensionFile extension library, not null
    * @param entryPoint    entry point function; if null, defaults to "sqlite3_extension_init"
    * @throws SQLiteException if extension can't be loaded
    * @see <a href="http://www.sqlite.org/c3ref/load_extension.html">load_extension</a>
    */
  @throws[SQLiteException]
  def loadExtension(extensionFile: Nothing, entryPoint: String): Unit = {
    checkThread()
    val handle = this.handle()
    val path = extensionFile.getAbsolutePath
    if (Internal.isFineLogging) Internal.logFine(this, "loading extension from (" + path + "," + entryPoint + ")")
    val error = mySQLiteWrapper.sqlite3_load_extension(handle, path, entryPoint)
    val rc = mySQLiteWrapper.getLastReturnCode
    throwResult(rc, "loadExtension()", error)
    if (Internal.isFineLogging) Internal.logFine(this, "extension (" + path + "," + entryPoint + ") loaded")
  }

  /**
    * Loads an SQLite extension library using default extension entry point.
    * Extension loading must be enabled with {@link #setExtensionLoadingEnabled(boolean)}
    * prior to calling this method.
    *
    * @param extensionFile extension library, not null
    * @throws SQLiteException if extension can't be loaded
    * @see <a href="http://www.sqlite.org/c3ref/load_extension.html">load_extension</a>
    */
  @throws[SQLiteException]
  def loadExtension(extensionFile: Nothing): Unit = {
    loadExtension(extensionFile, null)
  }

  @throws[SQLiteException]
  private def createArray0(name: String, controller: Nothing) = {
    val handle = this.handle()
    if (name == null) name = nextArrayName
    val module = getIntArrayModule(handle)
    if (Internal.isFineLogging) Internal.logFine(this, "creating intarray [" + name + "]")
    val r = mySQLiteWrapper.sqlite3_intarray_create(module, name)
    val rc = mySQLiteWrapper.getLastReturnCode
    if (rc != 0) throwResult(rc, "createArray()", name + " (cannot allocate virtual table)")
    if (r == null) throwResult(SQLiteConstants.WRAPPER_WEIRD, "createArray()", name)
    if (Internal.isFineLogging) Internal.logFine(this, "created intarray [" + name + "]")
    new Nothing(controller, r, name)
  }

  @throws[SQLiteException]
  private def getIntArrayModule(handle: Nothing) = {
    var r = myIntArrayModule
    // single-thread: we may be sure of singularity
    if (r == null) {
      if (Internal.isFineLogging) Internal.logFine(this, "registering INTARRAY module")
      myIntArrayModule = r = mySQLiteWrapper.sqlite3_intarray_register(handle)
      throwResult(mySQLiteWrapper.getLastReturnCode, "getIntArrayModule()")
      if (r == null) throwResult(SQLiteConstants.WRAPPER_WEIRD, "getIntArrayModule()")
    }
    r
  }

  private def nextArrayName = String.format("__IA%02X", {
    myLongArrayCounter += 1; myLongArrayCounter
  })*/

  private def finalizeProgressHandler(handle: SQLiteConnection.Handle): Unit = {
    if (Thread.currentThread == myConfinement) {
      val handler = myProgressHandler
      if (handler != null) mySQLiteWrapper.uninstall_progress_handler(handle, handler)
    }
  }

  /*private def finalizeBuffers(): Unit = {
    var buffers = null
    myLock synchronized
    if (myBuffers.isEmpty) return
    buffers = myBuffers.toArray(new Array[DirectBuffer](myBuffers.size))
    myBuffers.clear()
    myBuffersTotalSize = 0

    if (Thread.currentThread == myConfinement) for (buffer <- buffers) {
      SQLiteWrapper.wrapper_free(buffer)
    }
    else SQLiteException.logWarn(this, "cannot free " + buffers.length + " buffers from alien thread (" + Thread.currentThread + ")")
  }*/

  private def finalizeStatements(): Unit = {
    val alienThread = myConfinement != Thread.currentThread

    if (!alienThread) {
      logger.trace(mkLogMessage("finalizing statements"))

      var isStmtListNonEmpty = true
      while (isStmtListNonEmpty) {
        var statements: Array[SQLiteStatement] = null

        isStmtListNonEmpty = myLock synchronized {
          if (myStatements.isEmpty) false
          else {
            statements = myStatements.toArray(new Array[SQLiteStatement](myStatements.size))
            true
          }
        }

        if (isStmtListNonEmpty) {
          for (statement <- statements) {
            finalizeStatement(statement)
          }
        }
      }

      logger.trace(mkLogMessage("finalizing cached statements"))

      var isStmtCacheNonEmpty = true
      while (isStmtCacheNonEmpty) {
        var stmt: SQLiteStatement.Handle = null
        var sql: SQLParts = null

        isStmtCacheNonEmpty = myLock synchronized {
          val entryIter = myStatementCache.entrySet().iterator()
          if (!entryIter.hasNext) false
          else {
            val entry = entryIter.next()
            sql = entry.getKey
            stmt = entry.getValue.handle
            true
          }
        }

        if (isStmtCacheNonEmpty) {
          assert(stmt != null, "stmt is null")
          finalizeStatement(stmt, sql)
        }
      }
    }

    myLock synchronized {
      if (!myStatements.isEmpty || !myStatementCache.isEmpty) {
        val count = myStatements.size + myStatementCache.size
        if (alienThread) logger.warn(mkLogMessage(s"cannot finalize $count statements from alien thread"))
        else SQLiteException.logWarnOrThrowError(
          msg => logger.warn(msg), s"$count statements are not finalized", false
        )
      }

      myStatements.clear()
      myStatementCache.clear()
    }

  }

  /*
      private def finalizeArrays(): Unit

      =
      {
        val alienThread = myConfinement != Thread.currentThread
        if (!alienThread) {
          Internal.logFine(this, "finalizing arrays")
          var fastMap = null
          while ( {
            true
          }) {
            myLock synchronized
            if (myLongArrays.isEmpty) {
              break //todo: break is not supported}
              fastMap = new Nothing(myLongArrays)
              myLongArrays.clear

              import scala.collection.JavaConversions._
              for (entry <- fastMap.entrySet) {
                finalizeArrayHandle(entry.getValue, entry.getKey)
              }
            }
          }
          myLock synchronized
          if (!myLongArrays.isEmpty) {
            val count = myLongArrays.size
            if (alienThread) Internal.logWarn(this, "cannot finalize " + count + " arrays from alien thread")
            else Internal.recoverableError(this, count + " arrays are not finalized", false)
            myLongArrays.clear
          }

        }
*/

  private def finalizeBlobs(): Unit = {
    val alienThread = myConfinement != Thread.currentThread
    if (!alienThread) {
      logger.trace(mkLogMessage("finalizing blobs"))

      var isBlobsNonEmpty = true
      while (isBlobsNonEmpty) {
        var blobs: Array[SQLiteBlob] = null

        isBlobsNonEmpty = myLock synchronized {
          if (myBlobs.isEmpty) false
          else {
            blobs = myBlobs.toArray(new Array[SQLiteBlob](myBlobs.size))
            true
          }
        }

        if (isBlobsNonEmpty) {
          for (blob <- blobs) {
            finalizeBlob(blob)
          }
        }
      }

      myLock synchronized {
        if (!myBlobs.isEmpty) {
          val count = myBlobs.size
          if (alienThread) logger.warn(mkLogMessage(s"cannot finalize $count blobs from alien thread"))
          else SQLiteException.logWarnOrThrowError(msg => logger.warn(msg), s"$count blobs are not finalized", false)
        }
        myBlobs.clear()
      }
    }
  }

  private[sqlite4s] def finalizeBlob(blob: SQLiteBlob): Unit = {
    logger.trace(mkLogMessage("finalizing blob"))

    val handle = blob.blobHandle
    blob.clear()
    softClose(handle, blob)
    myLock synchronized forgetBlob(blob)
  }

  private def finalizeStatement(handle: SQLiteStatement.Handle, sql: SQLParts): Unit = {
    logger.trace(mkLogMessage(s"finalizing cached stmt for $sql"))

    softFinalize(handle, sql)
    myLock synchronized { forgetCachedHandle(handle, sql) }
  }

  private[sqlite4s] def finalizeStatement(statement: SQLiteStatement): Unit = {
    logger.trace(mkLogMessage("finalizing statement"))
    val handle = statement.statementHandle()
    val sql = statement.getSqlParts()
    statement.clear()
    softFinalize(handle, statement)
    myLock synchronized {
      forgetStatement(statement)
      forgetCachedHandle(handle, sql)
    }

  }

  /*
  private def finalizeArray(array: Nothing): Unit

  =
  {
    Internal.logFine(array, "finalizing")
    val handle = array.arrayHandle
    val tableName = array.getName
    finalizeArrayHandle(handle, tableName)
  }

  private def finalizeArrayHandle(handle: Nothing, name: String): Unit

  =
  {
    val rc = SQLiteWrapper.sqlite3_intarray_destroy(handle)
    if (rc != SQLITE_OK) Internal.logWarn(this, "error [" + rc + "] finalizing array " + name)
  }*/

  private def softFinalize(handle: SQLiteStatement.Handle, source: Any): Unit = {
    val rc = sqlite.sqlite3_finalize(handle)
    if (rc != SQLITE_OK) logger.warn(mkLogMessage(s"error [$rc] finishing $source"))
  }

  private def softClose(handle: SQLiteBlob.Handle, source: Any): Unit = {
    val rc = sqlite.sqlite3_blob_close(handle)
    if (rc != SQLITE_OK) logger.warn(mkLogMessage(s"error [$rc] finishing $source"))
  }

  /*
  private def cacheArrayHandle(array: Nothing): Unit

  =
  {
    if (Internal.isFineLogging) Internal.logFine(array, "returning handle to cache")
    var finalize = false
    val handle = array.arrayHandle
    if (handle == null) {
      Internal.logWarn(array, "no handle")
      return
    }
    try {
      val rc = SQLiteWrapper.sqlite3_intarray_unbind(handle)
      throwResult(rc, "intarray_unbind")
    } catch {
      case e: SQLiteException =>
        Internal.log(Level.WARNING, array, "exception when clearing", e)
        finalize = true
    }
    if (finalize) finalizeArray(array)
    else {
      val expunged = myLongArrays.put(array.getName, handle)
      if (expunged != null) Internal.logWarn(array, handle + " expunged " + expunged)
    }
  }*/

  /**
    * Called from {@link SQLiteStatement#dispose()}
    */
  private[sqlite4s] def cacheStatementHandle(statement: SQLiteStatement): Unit = {
    logger.trace(mkLogMessage("returning handle to cache"))

    var finalize = false
    val handle: SQLiteStatement.Handle = statement.statementHandle()
    val sql: SQLParts = statement.getSqlParts()

    try {
      if (statement.hasStepped) {
        val rc = sqlite.sqlite3_reset(handle)
        throwResult(rc, "reset")
      }
      if (statement.hasBindings) {
        val rc = sqlite.sqlite3_clear_bindings(handle)
        throwResult(rc, "clearBindings")
      }
    } catch {
      case e: SQLiteException =>
        logger.warn(mkLogMessage("exception when clearing"), e)
        finalize = true
    }

    myLock synchronized {
      if (!finalize) {
        val expunged = myStatementCache.put(sql, SQLiteStatement.HandleBox(handle))
        if (expunged != null) {
          val expungedHandle = expunged.handle
          if (expungedHandle == handle) {
            SQLiteException.logWarnOrThrowError(
              msg => logger.warn(msg),
              "statement handle appeared in cache when inserted",
              true
            )
          } else { // put it back
            logger.trace(mkLogMessage(s"second statement cached copy for [$sql] prevails"))
            // FIXME: DBO => do we need to do this and is this correct?
            myStatementCache.put(sql, expunged)
            finalize = true
          }
        }
      }
      forgetStatement(statement)
    }

    if (finalize) {
      logger.trace(mkLogMessage("cache doesn't need me, finalizing statement"))
      finalizeStatement(handle, sql)
    }
  }

  private def forgetCachedHandle(handle: SQLiteStatement.Handle, sql: SQLParts): Unit = {
    //---//assert(Thread.holdsLock(myLock))
    val removedHandleBox = myStatementCache.get(sql)

    if (removedHandleBox != null && removedHandleBox.handle == handle) {
      myStatementCache.remove(sql)
    }
  }

  private def forgetStatement(statement: SQLiteStatement): Unit = {
    //---//assert(Thread.holdsLock(myLock))
    // TODO: check if removing element can lead to perf. issues
    val removed = myStatements.remove(statement)
    if (!removed) SQLiteException.logWarnOrThrowError(msg => logger.warn(msg), "alien statement", true)
  }

  private def forgetBlob(blob: SQLiteBlob): Unit = {
    //---//assert(Thread.holdsLock(myLock))
    // TODO: check if removing element can lead to perf. issues
    val removed = myBlobs.remove(blob)
    if (!removed) SQLiteException.logWarnOrThrowError(msg => logger.warn(msg), "alien blob", true)
  }

  @throws[SQLiteException]
  def throwResult(resultCode: Int, operation: String): Unit = {
    throwResult(resultCode, operation, null)
  }

  @throws[SQLiteException]
  // TODO: rename throwErrorIfNotOK() and change additional to String
  def throwResult(resultCode: Int, operation: String, additional: Any): Unit = {
    if (resultCode == SQLITE_OK) return

    // ignore sync
    val handle = myHandle
    val msgBuilder = new StringBuilder(s"$this $operation")
    val additionalMessage = if (additional == null) null else String.valueOf(additional)
    if (additionalMessage != null) msgBuilder ++= " " + additionalMessage

    if (handle != null) try {
      val errmsg = fromCString(sqlite.sqlite3_errmsg(handle))
      if (additionalMessage == null || !(additionalMessage == errmsg)) msgBuilder ++= s" [$errmsg]"
    } catch {
      case e: Exception =>
        logger.warn(mkLogMessage("cannot get sqlite3_errmsg"), e)
    }

    val message = msgBuilder.toString()

    if (resultCode == SQLITE_BUSY || resultCode == SQLITE_IOERR_BLOCKED) throw new SQLiteBusyException(resultCode, message)
    else if (resultCode == SQLITE_INTERRUPT) throw new SQLiteInterruptedException(resultCode, message)
    else throw new SQLiteException(resultCode, message)
  }

  @throws[SQLiteException]
  private def open0(flags: Int): Unit = {
    logger.trace(mkLogMessage(s"opening (0x${Integer.toHexString(flags).toUpperCase(Locale.US)})"))

    var handle: SQLiteConnection.Handle = null
    myLock synchronized {
      if (myDisposed) throw new SQLiteException(WRAPPER_MISUSE, "cannot reopen closed connection")
      if (myConfinement == null) {
        myConfinement = Thread.currentThread
        logger.trace(mkLogMessage(s"confined to $myConfinement"))
      }
      else checkThread()

      handle = myHandle
    }

    if (handle != null) {
      throw new IllegalStateException("connection already opened")
    }

    val dbname = getSqliteDbName()
    logger.trace(mkLogMessage(s"dbname [$dbname]"))

    handle = mySQLiteWrapper.sqlite3OpenV2(dbname, flags)
    val rc = mySQLiteWrapper.getLastReturnCode()
    if (rc != SQLITE_OK) {
      if (handle != null) {
        logger.trace(mkLogMessage(s"error on open ($rc), closing handle"))

        try
          sqlite.sqlite3_close(handle)
        catch {
          case e: Exception =>
            logger.trace(mkLogMessage(s"error on closing after failed open"),e)
        }
      }
      var errorMessage = mySQLiteWrapper.drainLastOpenError()
      if (errorMessage == null) errorMessage = "open database error code " + rc
      throw new SQLiteException(rc, errorMessage)
    }

    if (handle == null) throw new SQLiteException(WRAPPER_WEIRD, "sqlite didn't return db handle")
    configureConnection(handle)
    myLock synchronized {
      myHandle = handle
      myOpenFlags = flags
    }

    logger.info(mkLogMessage("database opened"))
  }

  private def configureConnection(handle: SQLiteConnection.Handle): Unit = {
    val rc = sqlite.sqlite3_extended_result_codes(handle, 1)
    if (rc != SQLITE_OK) logger.warn(mkLogMessage(s"cannot enable extended result codes [$rc]"))
  }

  private def getSqliteDbName(): String = {
    if (myFile == null) ":memory:" else myFile.getAbsolutePath
  }

  def getStatementCount(): Int = myLock synchronized { myStatements.size }

  @throws[SQLiteException]
  def checkThread(): Unit = {
    if (this.isDisposed)
      throw new SQLiteException(WRAPPER_MISUSE, s"$this is already disposed")

    if (!SQLiteConnection.confinementCheckingEnabled) return

    val confinement = myConfinement
    if (confinement == null)
      // TODO: DBO => error code should also be WRAPPER_CONFINEMENT_VIOLATED
      throw new SQLiteException(WRAPPER_MISUSE,  s"$this is not confined or already disposed")

    val thread = Thread.currentThread
    if (thread != confinement) {
      val message = s"$this confined ($confinement) used ($thread)"
      throw new SQLiteException(WRAPPER_CONFINEMENT_VIOLATED, message)
    }
  }

  override def toString(): String = s"DB[$myNumber]"

  /**
    * The finalize() method is used to warn about a non-closed connection being forgotten.
    */
  @throws[Throwable]
  override protected def finalize(): Unit = {
    super.finalize()

    val handle = myHandle
    val disposed = myDisposed

    if (handle != null || !disposed)
      SQLiteException.logWarnOrThrowError(
        msg => logger.warn(msg), "wasn't disposed before finalizing", true
      )
  }

  /*@throws[SQLiteException]
  private def freeBuffer(buffer: DirectBuffer): Unit

  =
  {
    checkThread()
    var cached = false
    myLock synchronized cached = myBuffers.indexOf(buffer) >= 0

    buffer.decUsed
    if (!cached) {
      val rc = SQLiteWrapper.wrapper_free(buffer)
      if (rc != 0) SQLiteException.recoverableError(this, "error deallocating buffer", true)
    }
  }

  @throws[SQLiteException]
  @throws[IOException]
  private def allocateBuffer(minimumSize: Int)

  =
  {
    checkThread()
    handle
    var size = 1024
    while ( {
      size < minimumSize + DirectBuffer.CONTROL_BYTES
    }) size <<= 1
    val payloadSize = size - DirectBuffer.CONTROL_BYTES
    var allocated = 0
    var buffer = null
    myLock synchronized
    var i = myBuffers.size - 1
    while ( {
      i >= 0
    }) {
      val b = myBuffers.get(i)
      if (!b.isValid) {
        // TODO: check if removing element can lead to perf. issues
        myBuffers.remove(i)
        myBuffersTotalSize -= b.getCapacity
        continue //todo: continue is not supported
      }
      if (b.getCapacity < payloadSize) break //todo: break is not supported
      if (!b.isUsed) buffer = b

      {
        i -= 1
        i + 1
      }
    }
    if (buffer != null) {
      buffer.incUsed
      buffer.data.clear
      return buffer
    }
    allocated = myBuffersTotalSize

    assert(buffer == null)
    buffer = mySQLiteWrapper.wrapper_alloc(size)
    throwResult(mySQLiteWrapper.getLastReturnCode, "allocateBuffer", minimumSize)
    if (buffer == null) throw new SQLiteException(WRAPPER_WEIRD, "cannot allocate buffer [" + minimumSize + "]")
    buffer.incUsed
    buffer.data.clear
    if (allocated + size < SQLiteConnection.MAX_POOLED_DIRECT_BUFFER_SIZE) {
      myLock synchronized {
      var i = 0
      i = 0
      while (i < myBuffers.size) {
        val b = myBuffers.get(i)
        if (b.getCapacity > payloadSize) {
          break //todo: break is not supported}

            i += 1
          }
        }
        myBuffers.add(i, buffer)
        myBuffersTotalSize += buffer.getCapacity
      }
    }

    return buffer
  }*/

  /**
    * Runs SQL and returns formatted result. This method is added for running an SQL from debugger.
    *
    * @param sql SQL to execute
    * @return a string containing multiline formatted table with the result
    */
  def debug(sql: String): String = {
    var st: SQLiteStatement = null

    try {
      st = prepare(sql)
      val r = st.step()
      if (!r) return ""

      val columnsCount = st.columnCount()
      if (columnsCount == 0) return ""
      val columnIndices = 0 to columnsCount

      val widths = new Array[Int](columnsCount)
      val columnNames = new Array[String](columnsCount)

      for (i <- columnIndices) {
        columnNames(i) = String.valueOf(st.getColumnName(i))
        widths(i) = columnNames(i).length
      }

      val cells = new util.ArrayList[String]
      do {
        for (i <- columnIndices) {
          val v = if (st.columnNull(i)) "<null>"
          else String.valueOf(st.columnValue(i))
          cells.add(v)
          widths(i) = Math.max(widths(i), v.length)
        }
      } while (st.step())

      val buf = new java.lang.StringBuilder()
      buf.append('|')

      for (i <- columnIndices) {
        SQLiteConnection.appendW(buf, columnNames(i), widths(i), ' ')
        buf.append('|')
      }
      buf.append("\n|")

      for (i <- columnIndices) {
        SQLiteConnection.appendW(buf, "", widths(i), '-')
        buf.append('|')
      }

      var i = 0
      while (i < cells.size) {
        if (i % columnsCount == 0) buf.append("\n|")
        SQLiteConnection.appendW(buf, cells.get(i), widths(i % columnsCount), ' ')
        buf.append('|')

        i += 1
      }

      buf.toString

    } catch {
      case e: SQLiteException => e.getMessage
    } finally if (st != null) st.dispose()

  }
}