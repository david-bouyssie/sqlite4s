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

import java.io._

import com.github.sqlite4s.bindings.sqlite.SQLITE_CONSTANT._

import utest._

object SQLiteBasicTests extends SQLiteTestFixture() {
  private val RW = SQLITE_OPEN_READWRITE | SQLITE_OPEN_CREATE

  val tests = Tests {
    'testOpen - testOpen
    'testOpenMemory - testOpenMemory
    'testOpenReadOnly - testOpenReadOnly
    'testPrepareBindStepResetFinalize - testPrepareBindStepResetFinalize
    'testPrepareWithParam - testPrepareWithParam
    'testUnparseableSql - testUnparseableSql
    'testStatementSurvivesSchemaChange - testStatementSurvivesSchemaChange
    'testBindText - testBindText
    'testTextBindAndColumn - testTextBindAndColumn
  }

  def testOpen(): Unit = {
    val name = tempName("db")
    open(name, SQLITE_OPEN_READONLY)
    assert(lastDb == null)
    assertResult(SQLITE_CANTOPEN)
    open(name, SQLITE_OPEN_READWRITE)
    assert(lastDb == null)
    assertResult(SQLITE_CANTOPEN)
    open(name, SQLiteBasicTests.RW)
    assertDb()
    assertOk()
    close()
    assertOk()
  }

  def testOpenMemory(): Unit = {
    open(":memory:", SQLITE_OPEN_READWRITE)
    assertDb()
    assertOk()
    close()
    assertOk()
  }

  def testOpenReadOnly(): Unit = {
    val name = tempName("db")
    open(name, SQLiteBasicTests.RW)
    assertDb()
    exec("create table x (x)")
    assertOk()
    close()
    assertOk()
    open(name, SQLITE_OPEN_READONLY)
    exec("select * from x")
    assertOk()
    exec("insert into x values (1)")
    assertResult(SQLITE_READONLY)
    exec("drop table x")
    assertResult(SQLITE_READONLY)
    // FIXME: on Windows "begin immediate" returns SQLITE_OK instead of SQLITE_READONLY, as if it was a valid operation
    //exec("begin immediate")
    //assertResult(SQLITE_READONLY)
  }

  def testPrepareBindStepResetFinalize(): Unit = {
    val name = tempName("db")
    open(name, SQLiteBasicTests.RW)
    assertDb()
    exec("create table x (x)")
    assertOk()
    val stmt = prepare("insert into x values (?)")
    assertOk()
    assert(stmt != null)
    exec("begin immediate")
    assertOk()

    var i = 0
    while (i < 10) {
      bindLong(stmt, 1, i)
      assertOk()
      step(stmt)
      assertResult(SQLITE_DONE)
      reset(stmt)
      assertOk()

      i += 1
    }

    exec("commit")
    assertOk()
    finalize(stmt)
    assertOk()
    close()
  }

  def testPrepareWithParam(): Unit = {
    val name = tempName("db")
    open(name, SQLiteBasicTests.RW)
    assertDb()
    exec("create table x (x)")
    assertOk()
    val stmt1 = prepare("insert into x values (?)", SQLITE_PREPARE_PERSISTENT)
    assertOk()
    bindLong(stmt1, 42, 1)
    step(stmt1)
    finalize(stmt1)
    assertOk()
    val stmt2 = prepare("select * from x where x=42", 33)
    assertOk()
    assert(stmt2 != null)
    bindLong(stmt2, 42, 1)
    step(stmt2)
    finalize(stmt2)
    assertOk()
    close()
  }

  def testUnparseableSql(): Unit = {
    open(":memory:", SQLITE_OPEN_READWRITE)
    val stmt = prepare("habahaba")
    assert(stmt == null)
    assertResult(SQLITE_ERROR)
  }

  def testStatementSurvivesSchemaChange(): Unit = {
    open(tempName("db"), SQLiteBasicTests.RW)
    exec("create table x (x)")
    val stmt = prepare("insert into x (x) values (?)")
    assertOk()
    exec("alter table x add column y")
    assertOk()
    bindLong(stmt, 1, 100L)
    assertOk()
    step(stmt)
    assertResult(SQLITE_DONE)
    finalize(stmt)
    assertOk()
  }

  def testBindText(): Unit = {
    open(tempName("db"), SQLiteBasicTests.RW)
    exec("create table x (x)")
    val stmt = prepare("insert into x (x) values (?)")
    assertOk()
    bsr(stmt, "")
    bsr(stmt, "short text")
    val v = garbageString(100000)
    bsr(stmt, v)
    finalize(stmt)
    close()
  }

  def testTextBindAndColumn(): Unit = {
    val name = tempName("db")
    open(name, SQLiteBasicTests.RW)
    //    exec("PRAGMA encoding = \"UTF-16\";");
    exec("create table x (x)")
    var stmt = prepare("insert into x (x) values (?)")
    val v = garbageString(100000)
    bsr(stmt, v)
    finalize(stmt)
    close()
    open(name, SQLITE_OPEN_READONLY)
    stmt = prepare("select x from x")
    assertOk()
    step(stmt)
    assertResult(SQLITE_ROW)
    val v2 = columnText(stmt, 0)
    assertOk()
    step(stmt)
    assertResult(SQLITE_DONE)
    if (!v.equals(v2)) { // detect bad code points
      var i = 0
      var i2 = 0
      val len = v.length
      val len2 = v2.length
      while ( {
        i < len || i2 < len2
      }) {
        val c = if (i < len) v.codePointAt(i)
        else 0
        val c2 = if (i2 < len2) v2.codePointAt(i2)
        else 0
        // TODO: implement this test using utest
        //if (c != c2) assertEquals("[" + i + "][" + i2 + "]", "0x" + Integer.toHexString(c).toUpperCase, "0x" + Integer.toHexString(c2).toUpperCase)
        if (i < len) i = v.offsetByCodePoints(i, 1)
        if (i2 < len2) i2 = v2.offsetByCodePoints(i2, 1)
      }
    }
  }

  private def write(s: String, f: String): Unit = {
    try {
      val out = new FileOutputStream(new File(f))
      val bout = new BufferedOutputStream(out)
      val writer = new PrintWriter(bout)
      val len = s.length
      var i = 0
      while ( {
        i < len
      }) {
        writer.println("0x" + Integer.toHexString(s.codePointAt(i)))
        i = s.offsetByCodePoints(i, 1)
      }
      writer.close()
      bout.close()
      out.close()
    } catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }

  private def bsr(stmt: SQLiteStatement.Handle, value: String): Unit = {
    bindText(stmt, 1, value)
    assertOk()
    step(stmt)
    assertResult(SQLITE_DONE)
    reset(stmt)
    assertOk()
  }
}
