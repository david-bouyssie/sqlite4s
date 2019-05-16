package com.github.sqlite4s

import utest._

object ReuseStatementIssue67Tests extends SQLiteConnectionFixture {

  val tests = Tests {
    'testTest - testTest
  }

  @throws[SQLiteException]
  def testTest(): Unit = {
    Logging.configureLogger(Logging.LogLevel.OFF)

    val connection = fileDb().open()
    connection.exec("create table TBL (id unique, val integer)")
    val st = connection.prepare("insert into TBL values (?,?)", false)

    var id = 0
    while (id != 4) {
      try {
        st.bind(1, id)
        st.bind(2, id * id)
        st.step()
        st.reset(true)
      } catch {
        case ex: SQLiteException => {
          st.cancel()
          st.clearBindings()
          st.reset(true)
          id += 1
        }
      }
      id += 1
    }

    checkTable(connection, 0, 0, 1, 1, 2, 4, 3, 9)
  }

  @throws[SQLiteException]
  private def checkTable(connection: SQLiteConnection, expected: Int*): Unit = {
    var idx = 0
    val st = connection.prepare("select * from TBL")
    while (st.step()) {
      require(idx < expected.length, "idx >= expected.length" + idx + " " + expected.length)
      for (i <- 0 until st.columnCount()) {
        assert(st.columnInt(i) == expected(idx))
        idx += 1
      }
    }
  }
}