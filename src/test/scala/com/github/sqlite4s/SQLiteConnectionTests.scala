package com.github.sqlite4s

import com.github.sqlite4s.bindings.sqlite.SQLITE_CONSTANT._
import utest._ // {Tests, assert, intercept}

object SQLiteConnectionTests extends SQLiteConnectionFixture {

  val tests = Tests {
    'testOpenFile - testOpenFile
    'testOpenMemory - testOpenMemory
    'testExec - testExec
    'testGetTableColumnMetadata - testGetTableColumnMetadata
    'testSetAndGetLimit - testSetAndGetLimit
    'testCannotReopen - testCannotReopen
    'testOpenV2 - testOpenV2
    'testPrepareV3 - testPrepareV3
    'testIsReadOnly - testIsReadOnly
    'testFlush - testFlush
  }

  @throws[SQLiteException]
  def testOpenFile(): Unit = {
    var connection = fileDb()
    assert(!connection.isOpen())

    intercept[SQLiteException]{
      connection.openReadonly()
      fail("successfully opened") // should not happen
    }

    assert(!connection.isOpen())
    connection.dispose()
    assert(!connection.isOpen())
    connection.dispose()
    assert(!connection.isOpen())

    //assert(!dbFile.exists)
    connection = fileDb()

    val allowCreate = false
    intercept[SQLiteException]{
      connection.open(allowCreate)
      //assert(!connection.isOpen())
      fail("successfully opened")
    }

    connection.open(true)
    assert(connection.isOpen())
    assert(dbFile == connection.getDatabaseFile)

    connection.dispose()
    assert(!connection.isOpen())
  }


  @throws[SQLiteException]
  def testOpenMemory(): Unit = {
    var connection = memDb()
    assert(!connection.isOpen())

    intercept[SQLiteException]{
      connection.openReadonly()
      fail("successfully opened")
    }

    assert(!connection.isOpen())
    connection.dispose()
    assert(!connection.isOpen())
    connection.dispose()
    assert(!connection.isOpen())

    connection = memDb()

    intercept[SQLiteException]{
      connection.open(false)
      fail("successfully opened")
    }

    connection.open()
    assert(connection.isOpen())
    assert(connection.getDatabaseFile() == null)
    assert(connection.isMemoryDatabase())
    connection.dispose()
    assert(!connection.isOpen())
  }

  @throws[SQLiteException]
  def testExec(): Unit = {
    val db = fileDb()

    intercept[SQLiteException]{
      db.exec("create table xxx (x)")
      fail("exec unopened")
    }

    db.open()
    db.exec("pragma encoding=\"UTF-8\";")
    db.exec("create table x (x)")
    db.exec("insert into x values (1)")

    try {
      db.exec("blablabla")
      fail("execed bad sql")
    } catch {
      case e: SQLiteException =>
    }
  }

  @throws[SQLiteException]
  def testGetTableColumnMetadata(): Unit = {
    val db = fileDb()
    db.open()
    db.exec("create table xxx (x INTEGER PRIMARY KEY)")

    try {
      val dbName = null
      val tableName = "xxx"
      val columnName = "x"
      val metadata = db.getTableColumnMetadata(dbName, tableName, columnName)
      assert("INTEGER" == metadata.getDataType)
      assert("BINARY" == metadata.getCollSeq)
      assert(!metadata.isNotNull)
      assert(metadata.isPrimaryKey)
      assert(!metadata.isAutoInc)
    } catch {
      case e: SQLiteException => fail("failed to get table column metadata")
    }
  }

  @throws[SQLiteException]
  def testSetAndGetLimit(): Unit = {
    val db = fileDb()
    db.open()

    val currentLimit = db.getLimit(SQLITE_LIMIT_COLUMN)
    assert(currentLimit == db.setLimit(SQLITE_LIMIT_COLUMN, 5))
    assert(5 == db.getLimit(SQLITE_LIMIT_COLUMN))

    db.exec("create table yyy (a integer, b integer, c integer, d integer, e integer);")

    intercept[SQLiteException]{
      db.exec("create table y (a integer, b integer, c integer, d integer, e integer, excessiveColumnName integer);")
      fail("exec should fail due to column count limitation")
    }
  }

  @throws[SQLiteException]
  def testCannotReopen(): Unit = {
    val connection = fileDb()
    connection.open()
    assert(connection.isOpen())

    intercept[IllegalStateException] {
      connection.open()
    }

    assert(connection.isOpen())
    connection.dispose()
    assert(!connection.isOpen())
    connection.dispose()
    assert(!connection.isOpen())

    intercept[SQLiteException] {
      connection.open()
      fail("reopened connection")
    }

    assert(!connection.isOpen())
  }

  @throws[SQLiteException]
  def testOpenV2(): Unit = {
    val db = fileDb()
    db.openV2(SQLITE_OPEN_CREATE | SQLITE_OPEN_READWRITE | SQLITE_OPEN_NOMUTEX)
    db.exec("create table x(x)")
    db.dispose()
  }

  @throws[SQLiteException]
  def testPrepareV3(): Unit = {
    val con = fileDb().open()
    con.exec("create table x(x)")
    val stmt = con.prepare("insert into x values(?)", SQLITE_PREPARE_PERSISTENT)
    stmt.bind(1, "42")
    stmt.step
    con.dispose()
  }

  @throws[Exception]
  def testIsReadOnly(): Unit = {
    var i = 0
    while (i < 4) {
      val readonlyOpen = (i & 1) != 0
      val readonlyFile = (i & 2) != 0
      // to recreate File
      setUp()

      var con = fileDb().open()
      con.exec("create table x (x)")
      var expected = 42
      con.exec("insert into x values (%d)".format(expected))
      con.dispose()

      val dataBaseFile = dbFile

      if (readonlyFile) scala.Predef.assert(dataBaseFile.setReadOnly(), "can't make file readonly")
      con = new SQLiteConnection(dataBaseFile).openV2(if (readonlyOpen) SQLITE_OPEN_READONLY else SQLITE_OPEN_READWRITE)

      val isReadonly = readonlyFile || readonlyOpen
      assert(isReadonly == con.isReadOnly(null))
      assert(isReadonly == con.isReadOnly("main"))

      try { // writable query
        con.exec("update x set x=x+1")
        expected += 1
        if (isReadonly) throw new Exception("should throw SQLiteException")
      } catch {
        case ex: SQLiteException =>
          if (!isReadonly) throw ex
      }
      val st = con.prepare("select x from x")
      st.step()
      assert(expected == st.columnLong(0))

      i += 1
    }
  }

  /**
    * Test sqlite3_db_cacheflush(). This is an attempt to produce a state where the flush occurs
    * during a write-transaction with dirty pages and an exclusive lock. Producing this state
    * predictably does not seem possible since it relies predicting the pager's exact caching behavior.
    * Therefore on both calls, SQLITE_OK and SQLITE_BUSY are assumed to be valid results.
    *
    * @throws Exception
    */
  @throws[Exception]
  def testFlush(): Unit = {
    setUp()
    val con = fileDb().open()
    con.exec("create table x (x integer)")
    con.exec("begin exclusive transaction")
    try {
      con.flush()
      con.exec("insert into x values(42)")
      con.exec("update x set x=x-1 where x=42")
      con.flush()
    } catch {
      case e: SQLiteException =>
        logger.info(Internal.mkLogMessage("flush on datatase returned SQLITE_BUSY"))
    } finally {
      con.exec("commit")
      con.dispose()
    }
  }
}
