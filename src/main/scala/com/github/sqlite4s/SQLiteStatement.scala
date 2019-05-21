package com.github.sqlite4s

import java.io.IOException
import java.nio.BufferUnderflowException
import java.util

import scala.scalanative.native._
import scala.scalanative.runtime.ByteArray
import bindings.sqlite.SQLITE_CONSTANT._
import com.github.sqlite4s.bindings.sqlite_addons.sqlite3_destructor_type
import com.github.sqlite4s.bindings.sqlite_addons.DESTRUCTOR_TYPE._
import com.github.sqlite4s.c.util.CUtils
import Internal._
import SQLITE_WRAPPER_ERROR_CODE._
import com.github.sqlite4s.bindings.sqlite


/**
  * SQLiteStatement wraps an instance of compiled SQL statement, represented as <strong><code>sqlite3_stmt*</code></strong>
  * handle in SQLite C Interface.
  * <p/>
  * You get instances of SQLiteStatement via {@link SQLiteConnection#prepare} methods. After you've done using
  * the statement, you have to free it using {@link #dispose} method. Statements are usually cached, so until
  * you release the statement, <code>prepare</code> calls for the same SQL will result in needless compilation.
  * <p/>
  * Typical use includes binding parameters, then executing steps and reading columns. Most methods directly
  * correspond to the sqlite3 C interface methods.
  * <pre>
  * SQLiteStatement statement = connection.prepare(".....");
  * try {
  *   statement.bind(....).bind(....);
  * while (statement.step()) {
  *      statement.columnXXX(...);
  * }
  * } finally {
  *   statement.dispose();
  * }
  * </pre>
  * <p/>
  * Unless a method is marked as thread-safe, it is confined to the thread that has opened the connection. Calling
  * a confined method from a different thread will result in exception.
  *
  * @author Igor Sereda
  * @see <a href="http://sqlite.org/c3ref/stmt.html">sqlite3_stmt*</a>
  */
object SQLiteStatement {
  /**
    * Public instance of initially disposed, dummy statement. To be used as a guardian object.
    */
  val DISPOSED = new SQLiteStatement()

  sealed trait IHandle
  type Handle = Ptr[sqlite.sqlite3_stmt] with IHandle
  private[sqlite4s] case class HandleBox(handle: Handle)

  implicit class HandleWrapper(val handle: Handle) extends AnyVal {
    def asPtr(): Ptr[sqlite.sqlite3_stmt] = handle.asInstanceOf[Ptr[sqlite.sqlite3_stmt]]
  }
}

final class SQLiteStatement private() extends Logging {

  /**
    * The SQL of this statement.
    */
  private var mySqlParts: SQLParts = _
  /**
    * The profiler for this statement, may be null.
    */
  private var myProfiler: SQLiteProfiler = _
  /**
    * The controller that handles connection-level operations. Initially it is set
    */
  private var myController: SQLiteController = SQLiteController.getDisposed(null)

  /**
    * Statement handle wrapper. Becomes null when disposed.
    */
  private var myHandle: SQLiteStatement.Handle = _
  /**
    * When true, the last step() returned SQLITE_ROW, which means data can be read.
    */
  private var myHasRow: Boolean = false
  /**
    * When true, values have been bound to the statement. (and they take up memory)
    */
  private var myHasBindings: Boolean = false
  /**
    * When true, the statement has performed step() and needs to be reset to be reused.
    */
  private var myStepped: Boolean = false
  /**
    * The number of columns in current result set. If negative, the number is unknown and should
    * be requested at first need.
    */
  private var myColumnCount: Int = -1
  /**
    * All currently active bind streams.
    */
  //private var myBindStreams: List<BindStream> = null
  //private var myColumnStreams: List<ColumnStream> = null
  /**
    * Contains progress handler instance - only when step() is in progress. Used to cancel the execution.
    * Protected for MT access with this.
    */
  private var myProgressHandler: ProgressHandler = _
  /**
    * True if statement has been cancelled. Cleared at statement reset.
    */
  private var myCancelled: Boolean = false

  /**
    * Instances are constructed only by SQLiteConnection.
    *
    * @param controller controller, provided by the connection
    * @param handle     native handle wrapper
    * @param sqlParts   SQL
    * @param profiler   an instance of profiler for the statement, or null
    * @see SQLiteConnection#prepare(String, boolean)
    */
  def this(controller: SQLiteController, handle: SQLiteStatement.Handle, sqlParts: SQLParts, profiler: SQLiteProfiler) {
    this()

    assert(handle != null, "handle is null")
    assert(sqlParts.isFixed(), sqlParts)
    myController = controller
    myHandle = handle
    mySqlParts = sqlParts
    myProfiler = profiler
    logger.trace(mkLogMessage("instantiated"))
  }

  def statementHandle(): SQLiteStatement.Handle = myHandle

  @throws[SQLiteException]
  private def _getHandleOrFail(): SQLiteStatement.Handle = {
    val handle = myHandle
    if (handle == null)
      throw new SQLiteException(WRAPPER_STATEMENT_DISPOSED, null)

    handle
  }

  /**
    * @return true if the statement is disposed and cannot be used
    */
  def isDisposed(): Boolean = myHandle == null

  /**
    * Returns the immutable SQLParts object that was used to create this instance.
    * <p/>
    * This method is <strong>thread-safe</strong>.
    *
    * @return SQL used for this statement
    */
  def getSqlParts(): SQLParts = mySqlParts

  /**
    * Disposes this statement and frees allocated resources. If the statement's handle is cached,
    * it is returned to the connection's cache and can be reused by later calls to <code>prepare</code>
    * <p/>
    * Calling this method on an already disposed instance has no effect.
    * <p/>
    * After SQLiteStatement instance is disposed, it is no longer usable and holds no references to its originating
    * connection or SQLite database.
    */
  def dispose(): Unit = {
    if (myHandle == null) return

    try
      myController.validate()
    catch {
      case e: SQLiteException =>
        SQLiteException.logWarnOrThrowError(msg => logger.warn(msg), "invalid dispose: " + e, true)
        return
    }

    logger.trace(mkLogMessage("disposing"))

    myController.dispose(this)

    // clear may be called from dispose() too
    clear()
  }

  /**
    * Resets the statement if it has been stepped, allowing SQL to be run again. Optionally, clears bindings all binding.
    * <p/>
    * If <code>clearBinding</code> parameter is false, then all preceding bindings remain in place. You can change
    * some or none of them and run statement again.
    *
    * @param clearBindings if true, all parameters will be set to NULL
    * @return this statement
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/reset.html">sqlite3_reset</a>
    * @see <a href="http://www.sqlite.org/c3ref/clear_bindings.html">sqlite3_clear_bindings</a>
    */
  @throws[SQLiteException]
  def reset(clearBindings: Boolean): SQLiteStatement = {
    myController.validate()

    logger.trace(mkLogMessage(s"reset($clearBindings)"))

    val handle = _getHandleOrFail()
    //---//clearColumnStreams()
    if (myStepped) {
      logger.trace(mkLogMessage("resetting"))
      sqlite.sqlite3_reset(handle)
    }

    myHasRow = false
    myStepped = false
    myColumnCount = -1

    if (clearBindings && myHasBindings) {
      logger.trace(mkLogMessage("clearing bindings"))

      val rc = sqlite.sqlite3_clear_bindings(handle)
      myController.throwResult(rc, "reset.clearBindings()", this)

      //---//clearBindStreams(false)
      myHasBindings = false
    }

    // TODO: use a specific guard?
    this.synchronized {
      myCancelled = false
    }

    this
  }

  /**
    * Convenience method that resets the statement and clears bindings. See {@link #reset(boolean)} for a detailed
    * description.
    *
    * @return this statement
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def reset(): SQLiteStatement = reset(true)

  /**
    * Clears parameter bindings, if there are any. All parameters are set to NULL.
    *
    * @return this statement
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/clear_bindings.html">sqlite3_clear_bindings</a>
    */
  @throws[SQLiteException]
  def clearBindings(): SQLiteStatement = {
    myController.validate()
    logger.trace(mkLogMessage("clearBindings()"))

    if (myHasBindings) {
      logger.trace(mkLogMessage("clearing bindings"))
      val rc = sqlite.sqlite3_clear_bindings(_getHandleOrFail())
      myController.throwResult(rc, "clearBindings()", this)
      //---//clearBindStreams(false)
    }

    myHasBindings = false

    this
  }

  /**
    * Evaluates SQL statement until either there's data to be read, an error occurs, or the statement completes.
    * <p/>
    * An SQL statement is represented as a VM program in SQLite, and a call to <code>step</code> runs that program
    * until there's a "break point".
    * <p/>
    * Note that since SQlite 3.7, {@link #reset} method is called by step() automatically if anything other than
    * SQLITE_ROW is returned.
    * <p/>
    * This method can produce one of the three results:
    * <ul>
    * <li>If the return value is <strong>true</strong>, there's data to be read using <code>columnXYZ</code> methods;
    * <li>If the return value is <strong>false</strong>, the SQL statement is completed and no longer executable until
    * {@link #reset(boolean)} is called;
    * <li>Exception is thrown if any error occurs.
    * </ul>
    *
    * @return true if there is data (SQLITE_ROW) was returned, false if statement has been completed (SQLITE_DONE)
    * @throws SQLiteException if result code from sqlite3_step was neither SQLITE_ROW nor SQLITE_DONE, or if any other problem occurs
    * @see <a href="http://www.sqlite.org/c3ref/step.html">sqlite3_step</a>
    */
  @throws[SQLiteException]
  def step(): Boolean = {
    myController.validate()
    logger.trace(mkLogMessage("step()"))

    val handle = _getHandleOrFail()
    var rc = 0
    val ph = prepareStep()

    try {
      /*val profiler = myProfiler
      val from = if (profiler == null) 0
      else System.nanoTime*/
      rc = sqlite.sqlite3_step(handle)
      //---//if (profiler != null) profiler.reportStep(myStepped, mySqlParts.toString, from, System.nanoTime, rc)
    } finally {
      finalizeStep(ph, "step")
    }

    stepResult(rc, "step")

    myHasRow
  }

  /**
    * Convenience method that ignores the available data and steps through the SQL statement until evaluation is
    * completed. See {@link #step} for details.
    * <p/>
    * Most often it's used to chain calls.
    *
    * @return this statement
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def stepThrough(): SQLiteStatement = {
    while (step()) {
      /* do nothing */
    }

    this
  }

  /**
    * Cancels the currently running statement. This method has effect only during execution of the step() method,
    * and so it is run from a different thread.
    * <p/>
    * This method works by setting a cancel flag, which is checked by the progress callback. Hence, if the progress
    * callback is disabled, this method will not have effect. Likewise, if <code>stepsPerCallback</code> parameter
    * is set to large values, the reaction to this call may be far from immediate.
    * <p/>
    * If execution is cancelled, the step() method will throw {@link SQLiteInterruptedException}, and transaction
    * will be rolled back.
    * <p/>
    * This method is <strong>thread-safe</strong>.
    * <p/>
    *
    * @see SQLiteConnection#setStepsPerCallback
    * @see <a href="http://www.sqlite.org/c3ref/progress_handler.html">sqlite3_progress_callback</a>
    */
  def cancel(): Unit = {
    val handler = this.synchronized {
      myCancelled = true
      myProgressHandler
    }

    if (handler != null) handler.cancel()
  }

  /**
    * Checks whether there's data to be read with <code>columnXYZ</code> methods.
    *
    * @return true if last call to { @link #step} has returned true
    */
  def hasRow: Boolean = myHasRow

  /**
    * Checks if some parameters were bound
    *
    * @return true if at least one of the statement parameters has been bound to a value
    */
  def hasBindings: Boolean = myHasBindings

  /**
    * Checks if the statement has been evaluated
    *
    * @return true if the statement has been stepped at least once, and not reset
    */
  def hasStepped: Boolean = myStepped

  /**
    * Loads int values returned from a query into a buffer.
    * <p/>
    * The purpose of this method is to run a query and load a single-column result in bulk. This could save a lot of time
    * by making a single JNI call instead of 2*N calls to <code>step()</code> and <code>columnInt()</code>.
    * <p/>
    * If result set contains NULL value, it's replaced with 0.
    * <p/>
    * This method may be called iteratively with a fixed-size buffer. For example:
    * <pre>
    * SQLiteStatement st = connection.prepare("SELECT id FROM articles WHERE text LIKE '%whatever%'");
    * try {
    * int[] buffer = new int[1000];
    * while (!st.hasStepped() || st.hasRow()) {
    * int loaded = st.loadInts(0, buffer, 0, buffer.length);
    * processResult(buffer, 0, loaded);
    * }
    * } finally {
    *     st.dispose();
    * }
    * </pre>
    * <p/>
    * After method finishes, the number of rows loaded is returned and statement's {@link #hasRow} method indicates
    * whether more rows are available.
    *
    * @param column column index, as used in { @link #columnInt}
    * @param buffer buffer for accepting loaded integers
    * @param offset offset in the buffer to start writing
    * @param length maximum number of integers to load from the database
    * @return actual number of integers loaded
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def loadInts(column: Int, buffer: Array[Int], offset: Int, length: Int): Int = {
    require(buffer != null, "buffer is null")

    logger.trace(Internal.mkLogMessage(this.toString(), s"loadInts($column,$offset,$length)"))

    this._loadIntsOrLongs(
      "loadInts",
      wrapper => wrapper.wrapper_load_ints(_getHandleOrFail(), column, buffer, offset, length),
      (profiler, from, rc, count) => profiler.reportLoadInts(myStepped, mySqlParts.toString(), from, System.nanoTime, rc, count)
    )
  }

  /**
    * Loads long values returned from a query into a buffer.
    * <p/>
    * The purpose of this method is to run a query and load a single-column result in bulk. This could save a lot of time
    * by making a single JNI call instead of 2*N calls to <code>step()</code> and <code>columnLong()</code>.
    * <p/>
    * If result set contains NULL value, it's replaced with 0.
    * <p/>
    * This method may be called iteratively with a fixed-size buffer. For example:
    * <pre>
    * SQLiteStatement st = connection.prepare("SELECT id FROM articles WHERE text LIKE '%whatever%'");
    * try {
    * long[] buffer = new long[1000];
    * while (!st.hasStepped() || st.hasRow()) {
    * int loaded = st.loadInts(0, buffer, 0, buffer.length);
    * processResult(buffer, 0, loaded);
    * }
    * } finally {
    *     st.dispose();
    * }
    * </pre>
    * <p/>
    * After method finishes, the number of rows loaded is returned and statement's {@link #hasRow} method indicates
    * whether more rows are available.
    *
    * @param column column index, as used in { @link #columnInt}
    * @param buffer buffer for accepting loaded longs
    * @param offset offset in the buffer to start writing
    * @param length maximum number of integers to load from the database
    * @return actual number of integers loaded
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def loadLongs(column: Int, buffer: Array[Long], offset: Int, length: Int): Int = {
    require(buffer != null, "buffer is null")

    logger.trace(Internal.mkLogMessage(this.toString(), s"loadLongs($column,$offset,$length)"))

    this._loadIntsOrLongs(
      "loadLongs",
      wrapper => wrapper.wrapper_load_longs(_getHandleOrFail(), column, buffer, offset, length),
      (profiler, from, rc, count) => profiler.reportLoadLongs(myStepped, mySqlParts.toString(), from, System.nanoTime, rc, count)
    )
  }

  private def _loadIntsOrLongs(
    methodName: String,
    bufferLoader: SQLiteWrapper => Int,
    profilerReporter: (SQLiteProfiler, Long, Int, Int) => Unit
  ): Int = {
    myController.validate()

    if (myStepped && !myHasRow) return 0

    var loadedValues = 0
    var rc = 0
    val ph = prepareStep()

    try {
      val wrapper = myController.getSQLiteWrapper()
      val profiler = myProfiler
      val from = if (profiler == null) 0 else System.nanoTime

      loadedValues = bufferLoader(wrapper)

      rc = wrapper.getLastReturnCode()

      if (profiler != null) profilerReporter(profiler, from, rc, loadedValues)

    } finally finalizeStep(ph, methodName)

    stepResult(rc, methodName)

    loadedValues
  }


  /**
    * Returns the number of parameters that can be bound.
    *
    * @return the number of SQL parameters
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_parameter_count.html">sqlite3_bind_parameter_count</a>
    */
  @throws[SQLiteException]
  def getBindParameterCount(): Int = {
    myController.validate()
    sqlite.sqlite3_bind_parameter_count(_getHandleOrFail())
  }

  /**
    * Returns the name of a given bind parameter, as defined in the SQL.
    *
    * @param index the index of a bindable parameter, starting with 1
    * @return the name of the parameter, e.g. "?PARAM1", or null if parameter is anonymous (just "?")
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_parameter_name.html">sqlite3_bind_parameter_name</a>
    */
  @throws[SQLiteException]
  def getBindParameterName(index: Int): String = {
    myController.validate()
    fromCString(sqlite.sqlite3_bind_parameter_name(_getHandleOrFail(), index))
  }

  /**
    * Returns the index of a bind parameter with a given name, as defined in the SQL.
    *
    * @param name parameter name
    * @return the index of the parameter in the SQL, or 0 if no such parameter found
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_parameter_index.html">sqlite3_bind_parameter_index</a>
    */
  @throws[SQLiteException]
  def getBindParameterIndex(name: String): Int = {
    myController.validate()

    Zone { implicit z =>
      sqlite.sqlite3_bind_parameter_index(_getHandleOrFail(), CUtils.toCString(name))
    }
  }

  /**
    * Wraps <code>getBindParameterIndex</code> method
    *
    * @throws SQLiteException if parameter with specified name was not found
    */
  @throws[SQLiteException]
  private def getValidBindParameterIndex(name: String): Int = {
    val index = getBindParameterIndex(name)
    if (index == 0)
      throw new SQLiteException(
        WRAPPER_INVALID_ARG_1,
        s"failed to find parameter with specified name ($name)"
      )

    index
  }

  // This method  should be inlined for better performance
  @throws[SQLiteException]
  @inline
  private def _bind[T](fnSignature: String, traceMsg: => String, bindingFn: => CInt): SQLiteStatement = {
    myController.validate()
    logger.trace(mkLogMessage(traceMsg))

    // Execute binding function
    val rc = bindingFn
    myController.throwResult(rc, fnSignature, this)

    myHasBindings = true

    this
  }

  /**
    * Binds SQL parameter to a value of type double.
    *
    * @param index the index of the bindable parameter, starting with 1
    * @param value non-null double value
    * @return this object
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_double</a>
    */
  @throws[SQLiteException]
  def bind(index: Int, value: Double): SQLiteStatement = {
    _bind("bind(double)", s"bind($index, $value)", sqlite.sqlite3_bind_double(_getHandleOrFail(), index, value))
  }

  /**
    * Binds SQL parameter to a value of type double.
    *
    * @param name  parameter name
    * @param value non-null double value
    * @return this object
    * @throws SQLiteException if SQLite returns an error,
    *                         or if parameter with specified name was not found,
    *                         or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_double</a>
    */
  @throws[SQLiteException]
  def bind(name: String, value: Double): SQLiteStatement = bind(getValidBindParameterIndex(name), value)

  /**
    * Binds SQL parameter to a value of type int.
    *
    * @param index the index of the bindable parameter, starting with 1
    * @param value non-null int value
    * @return this object
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_int</a>
    */
  @throws[SQLiteException]
  def bind(index: Int, value: Int): SQLiteStatement = {
    _bind("bind(int)", s"bind($index, $value)", sqlite.sqlite3_bind_int(_getHandleOrFail(), index, value))
  }

  /**
    * Binds SQL parameter to a value of type int.
    *
    * @param name  parameter name
    * @param value non-null int value
    * @return this object
    * @throws SQLiteException if SQLite returns an error,
    *                         or if parameter with specified name was not found,
    *                         or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_int</a>
    */
  @throws[SQLiteException]
  def bind(name: String, value: Int): SQLiteStatement = bind(getValidBindParameterIndex(name), value)

  /**
    * Binds SQL parameter to a value of type long.
    *
    * @param index the index of the bindable parameter, starting with 1
    * @param value non-null long value
    * @return this object
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_int64</a>
    */
  @throws[SQLiteException]
  def bind(index: Int, value: Long): SQLiteStatement = {
    _bind("bind(long)", s"bind($index, $value)",sqlite.sqlite3_bind_int64(_getHandleOrFail(), index, value))
  }

  /**
    * Binds SQL parameter to a value of type long.
    *
    * @param name  parameter name
    * @param value non-null long value
    * @return this object
    * @throws SQLiteException if SQLite returns an error,
    *                         or if parameter with specified name was not found,
    *                         or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_int64</a>
    */
  @throws[SQLiteException]
  def bind(name: String, value: Long): SQLiteStatement = bind(getValidBindParameterIndex(name), value)

  /**
    * Binds SQL parameter to a value of type String.
    *
    * @param index the index of the bindable parameter, starting with 1
    * @param value String value, if null then { @link #bindNull} will be called
    * @return this object
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_text</a>
    */
  @throws[SQLiteException]
  def bind(index: Int, value: String): SQLiteStatement = {
    if (value == null) {
      logger.trace(mkLogMessage("bind(null string)"))
      return bindNull(index)
    }

    // FIXME: check if it is better to use toCString or ByteArray raw conversion
    /*val valueLen = value.length
      val valueAsCStr = Zone { implicit z =>
      toCString(value)
    }
    val valueAsBytes = value.getBytes()
    val valueLen = valueAsBytes.length
    val valueAsCArray = valueAsBytes.asInstanceOf[ByteArray].at(0)*/

    _bind(
      "bind(String)", {
        if (value.length <= 20) s"bind($index, $value)"
        else s"bind($index, ${value.substring(0, 20)}....)"
      },
      SQLiteWrapper.sqlite3BindText(_getHandleOrFail(), index, value)
    )
  }

  /**
    * Binds SQL parameter to a value of type String.
    *
    * @param name  parameter name
    * @param value String value, if null then { @link #bindNull} will be called
    * @return this object
    * @throws SQLiteException if SQLite returns an error,
    *                         or if parameter with specified name was not found,
    *                         or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_text</a>
    */
  @throws[SQLiteException]
  def bind(name: String, value: String): SQLiteStatement = bind(getValidBindParameterIndex(name), value)

  @throws[SQLiteException]
  def bind(index: Int, value: String, z: Zone): SQLiteStatement = {
    if (value == null) {
      logger.trace(mkLogMessage("bind(null string)"))
      return bindNull(index)
    }

    _bind(
      "bind(String)", {
        if (value.length <= 20) s"bind($index, $value)"
        else s"bind($index, very long cstring...)"
      },
      SQLiteWrapper.sqlite3BindStaticText(_getHandleOrFail(), index, value)(z)
    )
  }

  @throws[SQLiteException]
  def bind(name: String, value: String, z: Zone): SQLiteStatement = bind(getValidBindParameterIndex(name), value, z)

  /**
    * Binds SQL parameter to a BLOB value, represented by a byte array.
    *
    * @param index the index of the bindable parameter, starting with 1
    * @param value an array of bytes to be used as the blob value; if null, { @link #bindNull} is called
    * @return this object
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_blob</a>
    */
  @throws[SQLiteException]
  def bind(index: Int, value: Array[Byte]): SQLiteStatement = if (value == null) bindNull(index)
  else bind(index, value, 0, value.length)

  /**
    * Binds SQL parameter to a BLOB value, represented by a byte array.
    *
    * @param name  parameter name
    * @param value an array of bytes to be used as the blob value; if null, { @link #bindNull} is called
    * @return this object
    * @throws SQLiteException if SQLite returns an error,
    *                         or if parameter with specified name was not found,
    *                         or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_blob</a>
    */
  @throws[SQLiteException]
  def bind(name: String, value: Array[Byte]): SQLiteStatement = bind(getValidBindParameterIndex(name), value)

  /**
    * Binds SQL parameter to a BLOB value, represented by a range within byte array.
    *
    * @param index the index of the bindable parameter, starting with 1
    * @param value anarray of bytes; if null, { @link #bindNull} is called
    * @param offset position in the byte array to start reading value from
    * @param length number of bytes to read from value
    * @return this object
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_blob</a>
    */
  @throws[SQLiteException]
  def bind(index: Int, value: Array[Byte], offset: Int, length: Int): SQLiteStatement = {
    if (value == null) {
      logger.trace(mkLogMessage("bind(null blob)"))
      return bindNull(index)
    }

    val cArray = value.asInstanceOf[ByteArray].at(0)
    this.bind(index, cArray, value.length, offset, length, SQLITE_TRANSIENT)
  }

  /**
    * Binds SQL parameter to a BLOB value, represented by a range within byte array.
    *
    * @param name  parameter name
    * @param value an array of bytes; if null, { @link #bindNull} is called
    * @param offset position in the byte array to start reading value from
    * @param length number of bytes to read from value
    * @return this object
    * @throws SQLiteException if SQLite returns an error,
    *                         or if parameter with specified name was not found,
    *                         or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_blob</a>
    */
  @throws[SQLiteException]
  def bind(name: String, value: Array[Byte], offset: Int, length: Int): SQLiteStatement = bind(getValidBindParameterIndex(name), value, offset, length)

  /**
    * Binds SQL parameter to a BLOB value, represented by a range within byte array.
    *
    * @param index the index of the bindable parameter, starting with 1
    * @param value a C array of bytes; if null, { @link #bindNull} is called
    * @param valueLength length of the C array
    * @param offset position in the byte array to start reading value from
    * @param length number of bytes to read from value
    * @return this object
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_blob</a>
    */
  @throws[SQLiteException]
  def bind(
    index: Int,
    value: Ptr[Byte],
    valueLength: Int,
    offset: Int,
    length: Int,
    destructor: sqlite3_destructor_type
  ): SQLiteStatement = {
    if (value == null) {
      logger.trace(mkLogMessage("bind(null blob)"))
      return bindNull(index)
    }

    if (offset < 0 || offset + length > valueLength)
      throw new ArrayIndexOutOfBoundsException(s"$valueLength $offset $length")

    _bind(
      "bind(blob)",
      s"bind($index,[$length])",
      SQLiteWrapper.sqlite3_bind_blob(_getHandleOrFail(), index, value, valueLength, offset, length, destructor)
    )
  }

  /**
    * Binds SQL parameter to a BLOB value, represented by a range within byte array.
    *
    * @param name  parameter name
    * @param value a C array of bytes; if null, { @link #bindNull} is called
    * @param offset position in the byte array to start reading value from
    * @param length number of bytes to read from value
    * @return this object
    * @throws SQLiteException if SQLite returns an error,
    *                         or if parameter with specified name was not found,
    *                         or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_blob</a>
    */
  @throws[SQLiteException]
  def bind(name: String, value: Ptr[Byte], valueLength: Int, offset: Int, length: Int, destructor: sqlite3_destructor_type): SQLiteStatement = {
    bind(getValidBindParameterIndex(name), value, valueLength, offset, length, destructor)
  }

  /**
    * Binds SQL parameter to a BLOB value, consisting of a given number of zero bytes.
    *
    * @param index  the index of the bindable parameter, starting with 1
    * @param length number of zero bytes to use as a parameter
    * @return this object
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_zeroblob</a>
    */
  @throws[SQLiteException]
  def bindZeroBlob(index: Int, length: Int): SQLiteStatement = {
    if (length < 0) {
      logger.trace(mkLogMessage("bind(null blob)"))
      return bindNull(index)
    }

    _bind(
      "bindZeroBlob()",
      s"bindZeroBlob($index,$length)",
      sqlite.sqlite3_bind_zeroblob(_getHandleOrFail(), index, length)
    )
  }

  /**
    * Binds SQL parameter to a BLOB value, consiting of a given number of zero bytes.
    *
    * @param name   parameter name
    * @param length number of zero bytes to use as a parameter
    * @return this object
    * @throws SQLiteException if SQLite returns an error,
    *                         or if parameter with specified name was not found,
    *                         or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_zeroblob</a>
    */
  @throws[SQLiteException]
  def bindZeroBlob(name: String, length: Int): SQLiteStatement = bindZeroBlob(getValidBindParameterIndex(name), length)

  /**
    * Binds SQL parameter to a NULL value.
    *
    * @param index the index of the bindable parameter, starting with 1
    * @return this object
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_null</a>
    */
  @throws[SQLiteException]
  def bindNull(index: Int): SQLiteStatement = {
    _bind(
      "bindNull()",
      s"bindNull($index)",
      sqlite.sqlite3_bind_null(_getHandleOrFail(), index)
    )
  }

  /**
    * Binds SQL parameter to a NULL value.
    *
    * @param name parameter name
    * @return this object
    * @throws SQLiteException if SQLite returns an error,
    *                         or if parameter with specified name was not found,
    *                         or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_null</a>
    */
  @throws[SQLiteException]
  def bindNull(name: String): SQLiteStatement = bindNull(getValidBindParameterIndex(name))

  /**
    * Binds SQL parameter to a BLOB value, represented by a stream. The stream can further be used to write into,
    * before the first call to {@link #step}.
    * <p/>
    * After the application is done writing to the parameter stream, it should be closed.
    * <p/>
    * If statement is executed before the stream is closed, the value will not be set for the parameter.
    *
    * @param index the index of the bindable parameter, starting with 1
    * @return stream to receive data for the BLOB parameter
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_blob</a>
    */
 /* @throws[SQLiteException]
  def bindStream(index: Int): Nothing = bindStream(index, 0)*/

  /**
    * Binds SQL parameter to a BLOB value, represented by a stream. The stream can further be used to write into,
    * before the first call to {@link #step}.
    * <p/>
    * After the application is done writing to the parameter stream, it should be closed.
    * <p/>
    * If statement is executed before the stream is closed, the value will not be set for the parameter.
    *
    * @param name parameter name
    * @return stream to receive data for the BLOB parameter
    * @throws SQLiteException if SQLite returns an error,
    *                         or if parameter with specified name was not found,
    *                         or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_blob</a>
    */
  /*@throws[SQLiteException]
  def bindStream(name: String): Nothing = bindStream(getValidBindParameterIndex(name), 0)*/

  /**
    * Binds SQL parameter to a BLOB value, represented by a stream. The stream can further be used to write into,
    * before the first call to {@link #step}.
    * <p/>
    * After the application is done writing to the parameter stream, it should be closed.
    * <p/>
    * If statement is executed before the stream is closed, the value will not be set for the parameter.
    *
    * @param index      the index of the bindable parameter, starting with 1
    * @param bufferSize the number of bytes to be allocated for the buffer (the buffer will grow as needed)
    * @return stream to receive data for the BLOB parameter
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_blob</a>
    */
  /*@throws[SQLiteException]
  def bindStream(index: Int, bufferSize: Int): Nothing = {
    myController.validate()
    if (SQLiteException.isFineLogging) SQLiteException.logFine(this, "bindStream(" + index + "," + bufferSize + ")")
    try {
      val buffer = myController.allocateBuffer(bufferSize)
      val out = new SQLiteStatement#BindStream(index, buffer)
      var list = myBindStreams
      if (list == null) myBindStreams = list = new util.ArrayList[SQLiteStatement#BindStream](1)
      list.add(out)
      myHasBindings = true
      out
    } catch {
      case e: IOException =>
        throw new SQLiteException(WRAPPER_WEIRD, "cannot allocate buffer", e)
    }
  }*/

  /**
    * Binds SQL parameter to a BLOB value, represented by a stream. The stream can further be used to write into,
    * before the first call to {@link #step}.
    * <p/>
    * After the application is done writing to the parameter stream, it should be closed.
    * <p/>
    * If statement is executed before the stream is closed, the value will not be set for the parameter.
    *
    * @param name       parameter name
    * @param bufferSize the number of bytes to be allocated for the buffer (the buffer will grow as needed)
    * @return stream to receive data for the BLOB parameter
    * @throws SQLiteException if SQLite returns an error,
    *                         or if parameter with specified name was not found,
    *                         or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/bind_blob.html">sqlite3_bind_blob</a>
    */
  /*@throws[SQLiteException]
  def bindStream(name: String, bufferSize: Int): Nothing = bindStream(getValidBindParameterIndex(name), bufferSize)
*/

  @inline
  private def _validateCheckColumnAndReturnHandle(column: Int, mustHaveRow: Boolean): SQLiteStatement.Handle = {
    myController.validate()

    val handle = _getHandleOrFail()
    checkColumn(column, handle, mustHaveRow)

    handle
  }

  /**
    * Gets a column value after step has returned a row of the result set.
    * <p/>
    * Call this method to retrieve data of type String after {@link #step()} has returned true.
    *
    * @param column the index of the column, starting with 0
    * @return a String value or null if database value is NULL
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_blob.html">sqlite3_column_text16</a>
    */
  @throws[SQLiteException]
  def columnString(column: Int): String = {
    val handle = _validateCheckColumnAndReturnHandle(column, true)

    logger.trace(mkLogMessage(s"columnString($column)"))

    val sqliteWrapper = myController.getSQLiteWrapper()
    val result = sqliteWrapper.sqlite3ColumnText(handle, column)
    myController.throwResult(sqliteWrapper.getLastReturnCode(), "columnString()", this)

    logger.trace {
      if (result == null) s"columnString($column) is null"
      else if (result.length <= 20) s"columnString($column)=$result"
      else s"columnString($column)=${result.substring(0, 20)}...."
    }

    result
  }

  /**
    * Gets a column value after step has returned a row of the result set.
    * <p/>
    * Call this method to retrieve data of type int after {@link #step()} has returned true.
    *
    * @param column the index of the column, starting with 0
    * @return an int value, or value converted to int, or 0 if value is NULL
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_blob.html">sqlite3_column_int</a>
    */
  @throws[SQLiteException]
  def columnInt(column: Int): Int = {
    val handle = _validateCheckColumnAndReturnHandle(column, true)

    logger.trace(mkLogMessage(s"columnInt($column)"))

    // TODO: we don't know how to check if it failed (memory allocation error)
    // See: http://sqlite.1065341.n5.nabble.com/Checking-for-errors-in-sqlite3-column-td102290.html
    val r = sqlite.sqlite3_column_int(handle, column)

    logger.trace(mkLogMessage(s"columnInt($column)=$r"))

    r
  }

  /**
    * Gets a column value after step has returned a row of the result set.
    * <p/>
    * Call this method to retrieve data of type double after {@link #step()} has returned true.
    *
    * @param column the index of the column, starting with 0
    * @return a double value, or value converted to double, or 0.0 if value is NULL
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_blob.html">sqlite3_column_double</a>
    */
  @throws[SQLiteException]
  def columnDouble(column: Int): Double = {
    val handle = _validateCheckColumnAndReturnHandle(column, true)

    logger.trace(mkLogMessage(s"columnDouble($column)"))

    // TODO: we don't know how to check if it failed (memory allocation error)
    // See: http://sqlite.1065341.n5.nabble.com/Checking-for-errors-in-sqlite3-column-td102290.html
    val r = sqlite.sqlite3_column_double(handle, column)

    logger.trace(mkLogMessage(s"columnDouble($column)=$r"))

    r
  }

  /**
    * Gets a column value after step has returned a row of the result set.
    * <p/>
    * Call this method to retrieve data of type long after {@link #step()} has returned true.
    *
    * @param column the index of the column, starting with 0
    * @return a long value, or value converted to long, or 0L if the value is NULL
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_blob.html">sqlite3_column_int64</a>
    */
  @throws[SQLiteException]
  def columnLong(column: Int): Long = {
    val handle = _validateCheckColumnAndReturnHandle(column, true)

    logger.trace(mkLogMessage(s"columnLong($column)"))

    // TODO: we don't know how to check if it failed (memory allocation error)
    // See: http://sqlite.1065341.n5.nabble.com/Checking-for-errors-in-sqlite3-column-td102290.html
    val r = sqlite.sqlite3_column_int64(handle, column)

    logger.trace(mkLogMessage(s"columnLong($column)=$r"))

    r
  }

  /**
    * Gets a column value after step has returned a row of the result set.
    * <p/>
    * Call this method to retrieve data of type BLOB after {@link #step()} has returned true.
    *
    * @param column the index of the column, starting with 0
    * @return a byte array with the value, or null if the value is NULL
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_blob.html">sqlite3_column_blob</a>
    */
  @throws[SQLiteException]
  def columnBlob(column: Int): Array[Byte] = {
    val handle = _validateCheckColumnAndReturnHandle(column, true)

    logger.trace(mkLogMessage(s"columnBytes($column)"))

    val wrapper = myController.getSQLiteWrapper()
    val r = wrapper.sqlite3ColumnBlob(handle, column)
    myController.throwResult(wrapper.getLastReturnCode(), "columnBytes", this)

    logger.trace(mkLogMessage(
      s"columnBytes($column)=[${if (r == null) "null" else r.length}]"
    ))

    r
  }

  /**
    * Gets an InputStream for reading a BLOB column value after step has returned a row of the result set.
    * <p/>
    * Call this method to retrieve data of type BLOB after {@link #step()} has returned true.
    * <p/>
    * The stream should be read and closed before next call to step or reset. Otherwise, the stream is automatically
    * closed and disposed, and the following attempts to read from it result in IOException.
    *
    * @param column the index of the column, starting with 0
    * @return a stream to read value from, or null if the value is NULL
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_blob.html">sqlite3_column_blob</a>
    */
  /*@throws[SQLiteException]
  def columnStream(column: Int): Nothing = {
    myController.validate()
    val handle = _getHandleOrFail()
    checkColumn(column, handle, true)
    if (SQLiteException.isFineLogging) SQLiteException.logFine(this, "columnStream(" + column + ")")
    val sqlite = myController.getSQLiteWrapper()
    val buffer = sqlite.wrapper_column_buffer(handle, column)
    myController.throwResult(sqlite.getLastReturnCode, "columnStream", this)
    if (buffer == null) return null
    val in = new SQLiteStatement#ColumnStream(buffer)
    var table = myColumnStreams
    if (table == null) myColumnStreams = table = new util.ArrayList[SQLiteStatement#ColumnStream](1)
    table.add(in)
    in
  }*/

  /**
    * Checks if the value returned in the given column is null.
    *
    * @param column the index of the column, starting with 0
    * @return true if the result for the column was NULL
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_blob.html">sqlite3_column_type</a>
    */
  @throws[SQLiteException]
  def columnNull(column: Int): Boolean = {
    myController.validate()
    val valueType = getColumnType(column, _getHandleOrFail())
    valueType == SQLITE_NULL
  }

  /**
    * Gets the number of columns in the result set.
    * <p/>
    * This method may be called before statement is executed, during execution or after statement has executed (
    * {@link #step} returned false).
    * <p/>
    * However, for some statements where the number of columns may vary - such as
    * "SELECT * FROM ..." - the correct result is guaranteed only if method is called during statement execution,
    * when <code>step()</code> has returned true. (That is so because sqlite3_column_count function does not
    * force statement recompilation if database schema has changed, but sqlite3_step does.)
    *
    * @return the number of columns
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_count.html">sqlite3_column_count</a>
    */
  @throws[SQLiteException]
  def columnCount(): Int = {
    myController.validate()
    getColumnCount(_getHandleOrFail())
  }

  /**
    * Gets a column value after step has returned a row of the result set.
    * <p/>
    * Call this method to retrieve data of any type after {@link #step()} has returned true.
    * <p/>
    * The class of the object returned depends on the value and the type of value reported by SQLite. It can be:
    * <ul>
    * <li><code>null</code> if the value is NULL
    * <li><code>String</code> if the value has type SQLITE_TEXT
    * <li><code>Integer</code> or <code>Long</code> if the value has type SQLITE_INTEGER (depending on the value)
    * <li><code>Double</code> if the value has type SQLITE_FLOAT
    * <li><code>byte[]</code> if the value has type SQLITE_BLOB
    * </ul>
    *
    * @param column the index of the column, starting with 0
    * @return an object containing the value
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_blob.html">sqlite3_column_blob</a>
    */
  @throws[SQLiteException]
  def columnValue(column: Int): Any = {
    myController.validate()

    val valueType = getColumnType(column, _getHandleOrFail())
    valueType match {
      case SQLITE_NULL =>
        null
      case SQLITE_FLOAT =>
        columnDouble(column)
      case SQLITE_INTEGER =>
        val value = columnLong(column)
        if (value == value.toInt) value.toInt
        else value
      case SQLITE_TEXT =>
        columnString(column)
      case SQLITE_BLOB =>
        columnBlob(column)
      case _ =>
        SQLiteException.logWarnOrThrowError(msg => logger.warn(msg), s"value type $valueType not yet supported", true)
        null
    }
  }

  /**
    * Gets a type of a column after step() has returned a row.
    * <p/>
    * Call this method to retrieve data of any type after {@link #step()} has returned true.
    * <p/>
    * Note that SQLite has dynamic typing, so this method returns the affinity of the specified column.
    * See <a href="http://sqlite.org/datatype3.html">dynamic typing</a> for details.
    * <p/>
    * This method returns an integer constant, defined in {@link SQLiteConstants}: <code>SQLITE_NULL</code>,
    * <code>SQLITE_INTEGER</code>, <code>SQLITE_TEXT</code>, <code>SQLITE_BLOB</code> or <code>SQLITE_FLOAT</code>.
    * <p/>
    * The value returned by this method is only meaningful if
    * no type conversions have occurred as the result of calling columnNNN() methods.
    *
    * @param column the index of the column, starting with 0
    * @return an integer code, indicating the type affinity of the returned column
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_blob.html">sqlite3_column_type</a>
    */
  @throws[SQLiteException]
  def columnType(column: Int): Int = {
    myController.validate()
    getColumnType(column, _getHandleOrFail())
  }

  /**
    * Gets a name of the column in the result set.
    *
    * @param column the index of the column, starting with 0
    * @return column name
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_name.html">sqlite3_column_name</a>
    */
  @throws[SQLiteException]
  def getColumnName(column: Int): String = {
    val handle = _validateCheckColumnAndReturnHandle(column, false)

    logger.trace(mkLogMessage(s"columnName($column)"))

    val rAsCStr = sqlite.sqlite3_column_name(handle, column)
    val rAsStr = fromCString(rAsCStr)

    logger.trace(mkLogMessage(s"columnName($column)=$rAsStr"))

    rAsStr
  }

  /**
    * Gets a name of the column's table in the result set.
    *
    * @param column the index of the column, starting with 0
    * @return name of the table that the column belongs to
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_database_name.html">sqlite3_column_table_name</a>
    */
  @throws[SQLiteException]
  def getColumnTableName(column: Int): String = {
    val handle = _validateCheckColumnAndReturnHandle(column, false)

    logger.trace(mkLogMessage(s"columnTableName($column)"))

    val rAsCStr = sqlite.sqlite3_column_table_name(handle, column)
    val rAsStr = fromCString(rAsCStr)

    logger.trace(mkLogMessage(s"columnTableName($column)=$rAsStr"))

    rAsStr
  }

  /**
    * Gets a name of the column's table's database in the result set.
    *
    * @param column the index of the column, starting with 0
    * @return name of the database that contains the table that the column belongs to
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_database_name.html">sqlite3_column_database_name</a>
    */
  @throws[SQLiteException]
  def getColumnDatabaseName(column: Int): String = {
    val handle = _validateCheckColumnAndReturnHandle(column, false)

    logger.trace(mkLogMessage(s"columnDatabaseName($column)"))

    val rAsCStr = sqlite.sqlite3_column_database_name(handle, column)
    val rAsStr = fromCString(rAsCStr)

    logger.trace(mkLogMessage(s"columnDatabaseName($column)=$rAsStr"))

    rAsStr
  }

  /**
    * Gets the original name of the column that is behind the given column in the result set. The name
    * is not aliased (not defined in the SQL).
    *
    * @param column the index of the column, starting with 0
    * @return name of the table column
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/column_database_name.html">sqlite3_column_database_name</a>
    */
  @throws[SQLiteException]
  def getColumnOriginName(column: Int): String = {
    val handle = _validateCheckColumnAndReturnHandle(column, false)

    logger.trace(mkLogMessage(s"columnOriginName($column)"))

    val rAsCStr = sqlite.sqlite3_column_origin_name(handle, column)
    val rAsStr = fromCString(rAsCStr)

    logger.trace(mkLogMessage(s"columnOriginName($column)=$rAsStr"))

    rAsStr
  }

  /**
    * Check if the underlying statement is a SELECT.
    *
    * @return true if statement is a SELECT; false if it is UPDATE, INSERT or other DML statement. The return value is undefined for some statements - see SQLite docs.
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/stmt_readonly.html">sqlite3_stmt_readonly</a>
    */
  @throws[SQLiteException]
  def isReadOnly(): Boolean = {
    myController.validate()
    sqlite.sqlite3_stmt_readonly(_getHandleOrFail()) != 0 // TODO: DBO => check return type
  }

  /**
    * Clear all data, disposing the statement. May be called by SQLiteConnection on close.
    */
  def clear(): Unit = {
    //clearBindStreams(false)
    //clearColumnStreams()
    myHandle = null
    myHasRow = false
    myColumnCount = -1
    myHasBindings = false
    myStepped = false
    myController = SQLiteController.getDisposed(myController)
    //myProfiler = null
    logger.trace(mkLogMessage("cleared"))
  }

  /*private def clearColumnStreams(): Unit = {
    val table = myColumnStreams
    if (table != null) {
      myColumnStreams = null
      import scala.collection.JavaConversions._
      for (stream <- table) {
        try
          stream.close()
        catch {
          case e: IOException =>
            SQLiteException.logFine(this, e.toString)
        }
      }
    }
  }*/

  /*
  private def clearBindStreams(bind: Boolean): Unit = {
    val table = myBindStreams
    if (table != null) {
      myBindStreams = null
      import scala.collection.JavaConversions._
      for (stream <- table) {
        if (bind && !stream.isDisposed) try
          stream.close()
        catch {
          case e: IOException =>
            SQLiteException.logFine(this, e.toString)
        }
        else stream.dispose()
      }
      table.clear()
    }
  }*/

  @throws[SQLiteException]
  private def getColumnType(column: Int, handle: SQLiteStatement.Handle): CInt = {
    checkColumn(column, handle, false)

    logger.trace(mkLogMessage(s"columnType($column)"))

    val r = sqlite.sqlite3_column_type(handle, column)

    logger.trace(mkLogMessage(s"columnType($column)=$r"))

    r
  }

  @throws[SQLiteException]
  private def checkColumn(column: Int, handle: SQLiteStatement.Handle, mustHaveRow: Boolean): Unit = { // assert right thread
    if (mustHaveRow && !myHasRow) throw new SQLiteException(WRAPPER_NO_ROW, null)
    if (column < 0) throw new SQLiteException(WRAPPER_COLUMN_OUT_OF_RANGE, String.valueOf(column))
    val columnCount = getColumnCount(handle)
    if (column >= columnCount) throw new SQLiteException(WRAPPER_COLUMN_OUT_OF_RANGE, column + "(" + columnCount + ")")
  }

  private def getColumnCount(handle: SQLiteStatement.Handle): Int = {
    var cc = myColumnCount

    if (cc < 0) { // data_count seems more safe than column_count
      logger.trace("asking column count")
      cc = sqlite.sqlite3_column_count(handle)
      myColumnCount = cc
      if (cc < 0) {
        SQLiteException.logWarnOrThrowError(msg => logger.warn(msg), s"columnsCount=$cc", true)
        cc = 0
      }
      else logger.trace(s"columnCount=$cc")
    }

    cc
  }

  @throws[SQLiteException]
  private def prepareStep(): ProgressHandler = {
    // FIXME: implement me
    /*clearBindStreams(true)
    clearColumnStreams()*/

    val ph = myController.getProgressHandler()
    if (ph != null) {
      ph.reset()

      this synchronized {
        if (myCancelled) {
          throw new SQLiteInterruptedException()
        }
        myProgressHandler = ph
      }
    }

    ph
  }

  private def finalizeStep(ph: ProgressHandler, methodName: String): Unit = {
    this synchronized {
      myProgressHandler = null
    }

    if (ph != null) {
      logger.trace(Internal.mkLogMessage(this.toString(), s"$methodName ${ph.getSteps} steps" ))
      ph.reset()
    }
  }

  @throws[SQLiteException]
  // TODO: DBO => rename this methods because its name is misleading
  private def stepResult(rc: Int, methodName: String): Unit = {
    if (!myStepped) { // if this is a first step, the statement may have been recompiled and column count changed
      myColumnCount = -1
    }
    myStepped = true

    if (rc == SQLITE_ROW) {
      logger.trace(mkLogMessage(s"$methodName ROW"))
      myHasRow = true
    }
    else if (rc == SQLITE_DONE) {
      logger.trace(mkLogMessage(s"$methodName DONE"))
      myHasRow = false
    }
    else myController.throwResult(rc,s"methodName()", this)
  }

  override def toString(): String = s"[$mySqlParts]$myController"

  /*
  final private class BindStream @throws[IOException]
  (val myIndex: Int, var myBuffer: DirectBuffer) extends Nothing {
    myBuffer.data.clear

    @throws[IOException]
    def write(b: Int): Unit = {
      try {
        myController.validate()
        val data = buffer(1)
        data.put(b.toByte)
      } catch {
        case e: SQLiteException =>
          dispose()
          throw new IOException("cannot write: " + e)
      }
    }

    @throws[IOException]
    def write(b: Array[Byte], off: Int, len: Int): Unit = {
      try {
        myController.validate()
        val data = buffer(len)
        data.put(b, off, len)
      } catch {
        case e: SQLiteException =>
          dispose()
          throw new IOException("cannot write: " + e)
      }
    }

    @throws[IOException]
    @throws[SQLiteException]
    private def buffer(len: Int) = {
      val buffer = getBuffer
      var data = buffer.data
      if (data.remaining < len) {
        var newBuffer = null
        try
          newBuffer = myController.allocateBuffer(buffer.getCapacity + len)
        catch {
          case e: IOException =>
            dispose()
            throw e
        }
        val newData = newBuffer.data
        data.flip
        newData.put(data)
        myController.freeBuffer(buffer)
        data = newData
        myBuffer = newBuffer
        assert(data.remaining >= len, data.capacity)
      }
      data
    }

    @throws[IOException]
    def close(): Unit = {
      try {
        myController.validate()
        val buffer = myBuffer
        if (buffer == null) return
        if (SQLiteException.isFineLogging) SQLiteException.logFine(thisSQLiteStatement, "BindStream.close:bind([" + buffer.data.capacity + "])")
        val rc = SQLiteWrapper.wrapper_bind_buffer(handle, myIndex, buffer)
        dispose()
        myController.throwResult(rc, "bind(buffer)", thisSQLiteStatement)
      } catch {
        case e: SQLiteException =>
          throw new IOException("cannot write: " + e)
      }
    }

    def isDisposed: Boolean = myBuffer == null

    @throws[IOException]
    private def getBuffer = {
      val buffer = myBuffer
      if (buffer == null) throw new IOException("stream discarded")
      if (!buffer.isValid) throw new IOException("buffer discarded")
      if (!buffer.isUsed) throw new IOException("buffer not used")
      buffer
    }

    def dispose(): Unit = {
      val buffer = myBuffer
      if (buffer != null) {
        myBuffer = null
        myController.freeBuffer(buffer)
      }
      val list = myBindStreams
      if (list != null) list.remove(this)
    }
  }

  private class ColumnStream(var myBuffer: Nothing) extends Nothing {
    assert(myBuffer != null)

    @throws[IOException]
    def read: Int = {
      val buffer = getBuffer
      if (buffer.remaining <= 0) return -1
      var b = 0
      try
        b = buffer.get
      catch {
        case e: BufferUnderflowException =>
          SQLiteException.logWarn(this, "weird: " + e)
          return -1
      }
      b.toInt & 0xFF
    }

    @throws[IOException]
    def read(b: Array[Byte], off: Int, len: Int): Int = {
      val buffer = getBuffer
      val rem = buffer.remaining
      if (rem <= 0) return -1
      try {
        if (rem < len) len = rem
        buffer.get(b, off, len)
        len
      } catch {
        case e: BufferUnderflowException =>
          SQLiteException.logWarn(this, "weird: " + e)
          -1
      }
    }

    @throws[IOException]
    def close(): Unit = {
      myBuffer = null
      val table = myColumnStreams
      if (table != null) table.remove(this)
    }

    @throws[IOException]
    def getBuffer(): Nothing = {
      val buffer = myBuffer
      if (buffer == null) throw new IOException("stream closed")
      buffer
    }
  }*/

}