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

import java.util.Random

import utest._

object IncrementalBlobIOAnalysis extends SQLiteConnectionFixture {

  val tests = Tests {
    "testRead" - testRead()
  }

  private val COUNT = 100
  private val ROWS = 600

  @throws[SQLiteException]
  def testRead(): Unit = {

    val db = fileDb().open(true)
    //db.exec("PRAGMA page_size = 1024")
    db.exec("PRAGMA cache_size = 100")
    db.exec("PRAGMA legacy_file_format = off")
    db.exec("create table A (id integer not null primary key autoincrement, value integer)")
    db.exec("create table B (value)")
    db.exec("begin")

    var st = db.prepare("insert into A (value) values (?)")
    val r = new Random()

    var i = 0
    while (i < ROWS) {
      st.bind(1, r.nextInt)
      st.step()
      st.reset()

      i += 1
    }
    st.dispose()
    db.exec("commit")

    val blobSize = 1 << 20
    val data = generate(blobSize)
    db.prepare("insert into B values (?)").bind(1, data).stepThrough().dispose()
    st = db.prepare("select value from A")
    println("Without blob access:")

    go(db, st, data, false)

    println("With blob access:")
    go(db, st, data, true)
  }

  @throws[SQLiteException]
  private def go(db: SQLiteConnection, st: SQLiteStatement, data: Array[Byte], readBlob: Boolean): Unit = {
    var k = 0
    while (k < 5) {
      var total = 0L

      var i = 0
      while (i < COUNT) {
        if (readBlob) _readBlob(db, data)
        st.reset()

        val start = System.nanoTime()
        while (st.step()) st.columnInt(0)
        val stop = System.nanoTime() - start
        total += stop

        i += 1
      }

      println(s"total = ${total / 1000000L}ms")

      k += 1
    }
  }

  @throws[SQLiteException]
  private def _readBlob(db: SQLiteConnection, data: Array[Byte]) = {
    val b = db.blob("B", "value", 1, false)

    var j = 0
    while (j < data.length) {
      val len = Math.min(data.length - j, 5000)
      b.read(j, data, j, len)
      j += len
    }
  }
}