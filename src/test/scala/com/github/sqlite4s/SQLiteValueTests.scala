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

import utest._

object SQLiteValueTests extends SQLiteConnectionFixture {

  val tests = Tests {
    "testString" - testString()
    "testIntegerAndLong" - testIntegerAndLong()
    "testColumnValueReturnsIntegerOrLong" - testColumnValueReturnsIntegerOrLong()
    "testFloats" - testFloats()
  }

  @throws[SQLiteException]
  def testString(): Unit = {
    val con = fileDb().open()

    var st = SQLiteValueTests.insertAndSelect(con, "xyz")
    println("insertAndSelect result: " + st.columnString(0))
    assert("xyz" == st.columnString(0))
    assert(!st.columnNull(0))
    st.reset()

    st = SQLiteValueTests.insertAndSelect(con, "1")
    assert("1" == st.columnString(0))
    assert(1 == st.columnInt(0))
    assert(1 == st.columnLong(0))
    assert(!st.columnNull(0))
    st.reset()

    st = SQLiteValueTests.insertAndSelect(con, "")
    assert("" == st.columnString(0))
    assert(!st.columnNull(0))
    st.reset()

    st = SQLiteValueTests.insertAndSelect(con, null)
    println(s"col value [${st.columnString(0)}]")
    assert(st.columnString(0) == null)
    assert(st.columnNull(0))
    st.reset()

    st = SQLiteValueTests.insertNullAndSelect(con)
    assert(st.columnString(0) == null)
    assert(st.columnNull(0))
    st.reset()
  }

  @throws[SQLiteException]
  def testIntegerAndLong(): Unit = {
    val con = fileDb().open()
    var st = SQLiteValueTests.insertAndSelect(con, 1, false)
    assert(1 == st.columnInt(0))
    assert(1L == st.columnLong(0))
    assert("1" == st.columnString(0))
    assert(!st.columnNull(0))
    st.reset()
    st = SQLiteValueTests.insertAndSelect(con, 1, true)
    assert(1 == st.columnInt(0))
    assert(1L == st.columnLong(0))
    assert("1" == st.columnString(0))
    assert(!st.columnNull(0))
    st.reset()
    st = SQLiteValueTests.insertAndSelect(con, Integer.MIN_VALUE, false)
    assert(Integer.MIN_VALUE == st.columnInt(0))
    assert(Integer.MIN_VALUE.asInstanceOf[Long] == st.columnLong(0))
    assert(!st.columnNull(0))
    st.reset()
    st = SQLiteValueTests.insertAndSelect(con, Integer.MAX_VALUE, false)
    assert(Integer.MAX_VALUE == st.columnInt(0))
    assert(Integer.MAX_VALUE.asInstanceOf[Long] == st.columnLong(0))
    assert(!st.columnNull(0))
    st.reset()
    var v = Integer.MAX_VALUE
    v += 2
    st = SQLiteValueTests.insertAndSelect(con, v, true)
    assert(Integer.MIN_VALUE + 1 == st.columnInt(0))
    assert(v == st.columnLong(0))
    assert(!st.columnNull(0))
    st.reset()
    st = SQLiteValueTests.insertNullAndSelect(con)
    assert(0 == st.columnInt(0))
    assert(0L == st.columnLong(0))
    assert(st.columnNull(0))
    st.reset()
    st = SQLiteValueTests.insertAndSelect(con, Integer.MAX_VALUE, false)
    st.reset()
    con.exec("update x set x = x + 2")
    st = con.prepare("select x from x")
    st.step()
    assert(Integer.MIN_VALUE + 1 == st.columnInt(0))
    assert(Integer.MAX_VALUE.asInstanceOf[Long] + 2L == st.columnLong(0))
    assert(!st.columnNull(0))
    st.reset()
    st = SQLiteValueTests.insertAndSelect(con, java.lang.Long.MAX_VALUE, true)
    st.reset()
    st = con.prepare("select x from x")
    st.step()
    assert(java.lang.Long.MAX_VALUE == st.columnLong(0))
    assert(!st.columnNull(0))
    st.reset()
    st = SQLiteValueTests.insertAndSelect(con, java.lang.Long.MIN_VALUE, true)
    st.reset()
    st = con.prepare("select x from x")
    st.step()
    assert(java.lang.Long.MIN_VALUE == st.columnLong(0))
    assert(!st.columnNull(0))
    st.reset()
    st = SQLiteValueTests.insertAndSelect(con, java.lang.Long.MAX_VALUE, true)
    st.reset()
    con.exec("update x set x = x + 2")
    st = con.prepare("select x from x")
    st.step()
    assert(java.lang.Long.MAX_VALUE == st.columnLong(0))
    assert(!st.columnNull(0))
    st.reset()
    st = SQLiteValueTests.insertAndSelect(con, java.lang.Long.MIN_VALUE, true)
    st.reset()
    con.exec("update x set x = x - 2")
    st = con.prepare("select x from x")
    st.step()
    assert(java.lang.Long.MIN_VALUE == st.columnLong(0))
    assert(!st.columnNull(0))
    st.reset()
  }

  @throws[SQLiteException]
  def testColumnValueReturnsIntegerOrLong(): Unit = {
    val c = memDb().open()
    var st = SQLiteValueTests.insertAndSelect(c, 1, false)
    var o = st.columnValue(0)
    assert(o != null)
    assertMatch(o){case i: Int => assert(i.intValue() == 1)}
    //assert(o.isInstanceOf[Int])
    //assert(o.asInstanceOf[Int] == 1)

    st.dispose()
    val value = 0xCAFEBABECAFEBABEL
    st = SQLiteValueTests.insertAndSelect(c, value, true)
    o = st.columnValue(0)
    assert(o != null)
    //assert(o.isInstanceOf[Long])
    //assert(o.asInstanceOf[Long] == value)
    assertMatch(o){case l: Long => assert(l.longValue() == 0xCAFEBABECAFEBABEL)}
    //assertEquals(classOf[Long], o.getClass)
    //assert(0xCAFEBABECAFEBABEL.toLong == o.asInstanceOf[Long])
  }

  @throws[SQLiteException]
  def testFloats(): Unit = {
    val con = fileDb().open()
    val v = 1.1
    val st = SQLiteValueTests.insertAndSelect(con, v)
    assert(v == st.columnDouble(0))
    assert(!st.columnNull(0))
    st.reset()
  }


  @throws[SQLiteException]
  private def insertNullAndSelect(con: SQLiteConnection): SQLiteStatement = {
    recreateX(con)
    var st = con.prepare("insert into x values (?)")
    st.bindNull(1)
    st.step()
    st.reset()
    println("before prepare")
    st = con.prepare("select x from x")
    println("after prepare")
    st.step()
    assert(st.hasRow)
    st
  }

  @throws[SQLiteException]
  private def insertAndSelect(con: SQLiteConnection, value: Double): SQLiteStatement = {
    recreateX(con)
    var st = con.prepare("insert into x values (?)")
    st.bind(1, value)
    st.step()
    st.reset()
    st = con.prepare("select x from x")
    st.step()
    assert(st.hasRow)
    st
  }

  @throws[SQLiteException]
  private def insertAndSelect(con: SQLiteConnection, value: String): SQLiteStatement = {
    recreateX(con)
    var st = con.prepare("insert into x values (?)")
    st.bind(1, value)
    st.step()
    st.reset()
    st = con.prepare("select x from x")
    st.step()
    assert(st.hasRow)
    st
  }

  @throws[SQLiteException]
  private def insertAndSelect(con: SQLiteConnection, value: Long, useLong: Boolean): SQLiteStatement = {
    recreateX(con)
    var st = con.prepare("insert into x values (?)")
    if (useLong) st.bind(1, value)
    else st.bind(1, value.toInt)
    st.step()
    st.reset()
    st = con.prepare("select x from x")
    st.step()
    assert(st.hasRow)
    st
  }

  @throws[SQLiteException]
  private def recreateX(con: SQLiteConnection): Unit = {
    con.exec("drop table if exists x")
    con.exec("create table x (x)")
  }
}
