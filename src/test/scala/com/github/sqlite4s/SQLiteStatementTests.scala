/*
 * Copyright 2010 ALM Works Ltd
 * Copyright 2019 David Bouyssié
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.sqlite4s

import com.github.sqlite4s.bindings.sqlite.SQLITE_CONSTANT._

import utest._

object SQLiteStatementTests extends SQLiteConnectionFixture {

  val tests = Tests {
    'testPrepareBad - testPrepareBad
    'testStatementLifecycle - testStatementLifecycle
    'testCloseFromCorrectThreadWithOpenStatement - testCloseFromCorrectThreadWithOpenStatement
    'testBadBindIndexes - testBadBindIndexes
    'testBindParameterNames - testBindParameterNames
    'testForgottenStatement - testForgottenStatement
    'testStatementNotReused - testStatementNotReused
    //'testCaching - testCaching // FIXME: DBO => issue with freed Ptr
    'testRollbackOnClose - testRollbackOnClose
    //'testColumnType - testColumnType // FIXME: DBO => issue with freed Ptr
  }

  @throws[SQLiteException]
  def testPrepareBad(): Unit = {
    val connection = fileDb()
    connection.open()

    intercept[SQLiteException] {
      connection.prepare("wrong sql")
      fail("prepared wrong sql")
    }

    intercept[IllegalArgumentException] {
      connection.prepare(null.asInstanceOf[SQLParts])
      fail("prepared null")
    }

    intercept[SQLiteException] {
      connection.prepare("   ")
      fail("prepared empty")
    }

    intercept[SQLiteException] {
      connection.prepare("")
      fail("prepared empty")
    }

    intercept[SQLiteException] {
      connection.prepare("select * from x")
      fail("prepared invalid")
    }
  }

  @throws[SQLiteException]
  def testStatementLifecycle(): Unit = {
    val connection = fileDb()
    connection.open()
    connection.exec("create table x (x)")

    val sql = "insert into x values (?)"
    val st1 = connection.prepare(sql, cached = false)
    val st2 = connection.prepare(sql, cached = false)
    val st3 = connection.prepare(sql, cached = true)
    val h3 = st3.statementHandle()
    st3.dispose()

    val st4 = connection.prepare(sql, cached = true)
    assert(st1 != st2)
    assert(st1 != st3)
    assert(st1 != st4)
    assert(st2 != st3)
    assert(st2 != st4)
    assert(st3 != st4)
    assert(h3 == st4.statementHandle)
    assert(3 == connection.getStatementCount())
    assert(!st1.isDisposed())
    assert(!st2.isDisposed())
    assert(st3.isDisposed())
    assert(!st4.isDisposed())
    st1.dispose()
    assert(2 == connection.getStatementCount())
    assert(st1.isDisposed())
    assert(!st2.isDisposed())
    assert(!st4.isDisposed())
    connection.dispose()
    assert(st2.isDisposed())
    assert(st4.isDisposed())
    assert(0 == connection.getStatementCount())
  }

  @throws[SQLiteException]
  def testCloseFromCorrectThreadWithOpenStatement(): Unit = {
    val connection = fileDb().open().exec("create table x (x, y)")
    connection.exec("insert into x values (2, '3');")
    val st = connection.prepare("select x, y from x")
    st.step()
    assert(st.hasRow)
    connection.dispose()
    assert(!connection.isOpen())
    assert(st.isDisposed())
    assert(!st.hasRow)
  }

  @throws[SQLiteException]
  def testBadBindIndexes(): Unit = {
    val connection = fileDb().open().exec("create table x (x, y)")
    val st = connection.prepare("insert into x values (?, ?)")

    intercept[SQLiteException] {
      st.bind(0, "0")
      fail("bound to 0")
    }

    st.bind(1, "1")
    st.bind(2, "2")

    intercept[SQLiteException] {
      st.bind(3, "3")
      fail("bound to 3")
    }

    intercept[SQLiteException] {
      st.bind(-99999, "-99999")
      fail("bound to 0-99999")
    }

    intercept[SQLiteException] {
      st.bind(99999, "99999")
      fail("bound to 99999")
    }
  }

  @throws[SQLiteException]
  def testBindParameterNames(): Unit = {
    val connection = fileDb().open().exec("create table x (x, y)")
    val st = connection.prepare("insert into x values (:val1, :val2)")

    try
      st.bind(":val1", "abc")
    catch {
      case e: SQLiteException => fail("can not bound to :val1")
    }

    intercept[SQLiteException] {
      st.bind(":val3", "missing parameter")
      fail("attempt to bound unexpected parameter")
    }
  }

  @throws[SQLiteException]
  def testBadColumnUse(): Unit = {
    val connection = fileDb().open().exec("create table x (x, y)")
    connection.exec("insert into x values (2, '3');")
    val st = connection.prepare("select x, y from x")
    assert(!st.hasRow)

    intercept[SQLiteException] {
      st.columnInt(0)
      fail("got column before step")
    }

    val r = st.step()
    assert(r)
    assert(st.hasRow)
    st.columnInt(0)
    st.columnString(1)

    intercept[SQLiteException] {
      st.columnInt(-1)
      fail("got column -1")
    }

    intercept[SQLiteException] {
      st.columnInt(-999999)
      fail("got column -999999")
    }

    intercept[SQLiteException] {
      st.columnInt(3)
      fail("got column 3")
    }

    intercept[SQLiteException] {
      st.columnInt(999999)
      fail("got column 999999")
    }
  }

  @throws[SQLiteException]
  @throws[InterruptedException]
  def testForgottenStatement(): Unit = {
    val connection = fileDb().open().exec("create table x (x)")
    connection.exec("insert into x values (1);")
    var st = connection.prepare("select x + ? from x")
    st.bind(1, 1)
    st.step()
    assert(st.hasRow)
    assert(st.hasBindings)
    st.dispose()
    st = connection.prepare("select x + ? from x")
    assert(!st.hasRow)
    assert(!st.hasBindings)
    st.bind(1, 1)
    st.step()
    assert(st.hasRow)
    assert(st.hasBindings)
    st = null
    System.gc()
    Thread.sleep(100)
    System.gc()
    st = connection.prepare("select x + ? from x")
    assert(!st.hasRow)
    assert(!st.hasBindings)
    st.bind(1, 1)
    st.step()
    assert(st.hasRow)
    assert(st.hasBindings)
  }

  @throws[SQLiteException]
  def testStatementNotReused(): Unit = {
    val connection = fileDb().open().exec("create table x (x)")
    connection.exec("insert into x values (1)")
    var st = connection.prepare("select x from x")
    assert(st != connection.prepare("select x from x"))
    st.step()
    assert(st.hasRow)
    assert(st != connection.prepare("select x from x"))
    st.step()
    assert(!st.hasRow)
    assert(st != connection.prepare("select x from x"))
    st.reset()
    assert(st != connection.prepare("select x from x"))
    st.dispose()
    assert(st != connection.prepare("select x from x"))
    st = connection.prepare("select x + ? from x")
    assert(st != connection.prepare("select x + ? from x"))
    st.bind(1, 1)
    assert(st != connection.prepare("select x + ? from x"))
    st.reset()
    assert(st != connection.prepare("select x + ? from x"))
    st.dispose()
    assert(st != connection.prepare("select x + ? from x"))
  }

  @throws[SQLiteException]
  def testCaching(): Unit = {
    val connection = fileDb().open().exec("create table x (x)")
    val sql = "select * from x"
    val st1 = connection.prepare(sql)
    val st2 = connection.prepare(sql)
    assert(st1 != st2)
    assert(st1.statementHandle != st2.statementHandle)
    val h1 = st1.statementHandle()
    st1.dispose()
    st2.dispose()
    val st3 = connection.prepare(sql)
    // first returned is in cache

    //---// DBO: warning, we cannot use the STH when it has been disposed //---//
    /*assert(h1 != st3.statementHandle())
    val st4 = connection.prepare(sql)
    val h4 = st4.statementHandle()
    assert(h1 != h4)
    st4.dispose()
    val st5 = connection.prepare(sql)
    val h5 = st5.statementHandle()
    assert(h4 == h5)
    st5.dispose()
    st3.dispose()
    val st6 = connection.prepare(sql)
    assert(h5 == st6.statementHandle())*/
  }

  @throws[SQLiteException]
  def testRollbackOnClose(): Unit = {
    var connection = fileDb().open().exec("create table x (x)")
    connection.exec("begin immediate")
    val st = connection.prepare("insert into x values (?)")
    st.bind(1, 1)
    st.step()
    connection.dispose()
    assert(st.isDisposed())
    connection = fileDb().open()
    val gotRow = connection.prepare("select * from x").step()
    assert(!gotRow)
  }

  @throws[SQLiteException]
  def testColumnType(): Unit = {
    val conn = memDb().open().exec("create table x (a integer, b text, c real, d blob, e)")
    var st = conn.prepare("insert into x values (1, 'one', 1.0001, ?, null)")
    st.bind(1, Array[Byte](1, 2, 3))
    st.step()
    st.dispose()
    st = conn.prepare("select * from x")
    st.step()
    assert(SQLITE_INTEGER == st.columnType(0))
    assert(SQLITE_TEXT == st.columnType(1))
    assert(SQLITE_FLOAT == st.columnType(2))
    assert(SQLITE_BLOB == st.columnType(3))
    assert(SQLITE_NULL == st.columnType(4))
    st.dispose()
  }
}

