package com.github.sqlite4s

import utest._

object RegressionIssue13Tests extends SQLiteConnectionFixture {

  val tests = Tests {
    'testColumnCountBug2 - testColumnCountBug2
    'testUnstableColumnResult - testUnstableColumnResult
  }

  /**
    * @author Olivier Monaco
    */
  @throws[Exception]
  def testColumnCountBug2(): Unit = {
    val cnx = new SQLiteConnection()
    cnx.open()

    try {
      var st = cnx.prepare("create table t (c text);")
      try {
        assert(!st.step())
        assert(0 == st.columnCount())
      } finally st.dispose()
      st = cnx.prepare("select name, type from sqlite_master;")
      try {
        assert(st.step())
        assert(2 == st.columnCount())
      } finally st.dispose()
      st = cnx.prepare("select name, type from sqlite_master " + "where name='not_exists';")

      try {
        assert(!st.step())
        assert(2 == st.columnCount())
      } finally st.dispose()

    } finally cnx.dispose()
  }

  @throws[SQLiteException]
  def testUnstableColumnResult(): Unit = {
    val c = new SQLiteConnection().open()
    c.exec("create table A (x, y)")
    c.exec("insert into A values (1, 2)")
    val st = c.prepare("select * from A")
    assert(2 == st.columnCount())
    c.exec("alter table A add column z")
    // unstable - nothing changed! even if we we to call sqlite3_column_count, it would still return 2!
    assert(2 == st.columnCount())
    // now the statement has been recompiled - the result updated
    st.step()
    assert(3 == st.columnCount())
  }
}
