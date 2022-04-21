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

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer

import utest._

object DirectBufferTests extends SQLiteConnectionFixture {

  val tests = Tests {
    "testCreation" - testCreation()
  }

  private val SIZE = 1024

  @throws[SQLiteException]
  @throws[IOException]
  def testCreation(): Unit = {
    val sqlite = new SQLiteWrapper()
    val buffer = sqlite.wrapper_alloc(DirectBufferTests.SIZE)
    assert(sqlite.getLastReturnCode() == 0)
    assert(buffer.isValid())
    val buf = buffer.data()
    assert(buf != null)
    assert(buf.isDirect)
    assert(!buf.isReadOnly)
    assert(DirectBufferTests.SIZE - 2 == buf.capacity)
    sqlite.wrapper_free(buffer)
    assert(!buffer.isValid())
  }

  /*@throws[SQLiteException]
  @throws[IOException]
  def testMemory(): Unit = {
    val m1 = bindings.sqlite.sqlite3_memory_used()
    val sqlite = new SQLiteWrapper()
    val sz = DirectBufferTests.SIZE * DirectBufferTests.SIZE
    val buffer = sqlite.wrapper_alloc(sz)
    var m2 = bindings.sqlite.sqlite3_memory_used()
    Predef.assert(Math.abs(m2 - m1 - sz) < 16, m1 + " " + sz + " " + m2)
    sqlite.wrapper_free(buffer)
    assert(m1 == bindings.sqlite.sqlite3_memory_used())

    val db = memDb().open(true)
    db.exec("create table t (v)")
    val st = db.prepare("insert into t values (?)")
    val m3 = bindings.sqlite.sqlite3_memory_used()
    val out = st.bindStream(1, sz - 10)
    m2 = _SQLiteSwigged.sqlite3_memory_used
    assertTrue(m3 + " " + sz + " " + m2, Math.abs(m2 - m3 - sz) < 16)
    out.write(generate(sz - 10))
    out.close()
    assertEquals(m2, _SQLiteSwigged.sqlite3_memory_used)
    st.step
    //    assertEquals(m2, _SQLiteSwigged.sqlite3_memory_used());
    db.dispose()
    assertEquals(m1, _SQLiteSwigged.sqlite3_memory_used)
  }*/

  /*@throws[SQLiteException]
  @throws[IOException]
  def testBind(): Unit = {
    val db = fileDb().open(true)
    db.exec("drop table if exists T")
    db.exec("create table T (value)")
    var st = db.prepare("insert into T values (?)")
    var out = st.bindStream(1)
    val data = generate(DirectBufferTests.SIZE * DirectBufferTests.SIZE)
    var i = 0
    while ( {
      i < DirectBufferTests.SIZE * DirectBufferTests.SIZE
    }) out.write(data(i)) {
      i += 1; i - 1
    }
    out.close()
    try {
      out.write(0)
      fail("wrote after closing")
    } catch {
      case e: IOException =>

      // ok
    }
    st.step
    st.reset
    out = st.bindStream(1)
    st.step
    try {
      out.write(0)
      fail("wrote after stepping")
    } catch {
      case e: IOException =>
    }
    st.dispose()
    st = db.prepare("select value from T")
    assertTrue(st.step)
    var in = st.columnStream(0)
    var i = 0
    while ( {
      i < data.length
    }) assertEquals("[" + i + "]", data(i), in.read.toByte) {
      i += 1; i - 1
    }
    assertEquals(-1, in.read)
    in.close()
    try {
      in.read
      fail("read after closing")
    } catch {
      case e: IOException =>
    }
    assertTrue(st.step)
    in = st.columnStream(0)
    assertNull(in)
    db.dispose()
  }*/
}