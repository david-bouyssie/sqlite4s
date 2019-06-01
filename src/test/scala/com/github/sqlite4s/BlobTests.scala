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

object BlobTests extends SQLiteConnectionFixture {

  val tests = Tests {
    'testOpen - testOpen
    'testReadWrite - testReadWrite
    'testRowidEqualsPK - testRowidEqualsPK
    'testBlobBindColumn - testBlobBindColumn
  }

  private val SIZE = 100000

  @throws[SQLiteException]
  private def doMultipleCheck(db: SQLiteConnection) = {
    val blob1 = db.blob(null, "T", "value", 1, true)
    val blob2 = db.blob(null, "T", "value", 1, true)
    val blob3 = db.blob(null, "T", "value", 1, false)
    val blob4 = db.blob(null, "T", "value", 1, false)

    val data = Array[Byte](111, 0, 0, 0, 0)
    blob1.write(0, data, 0, 1)
    blob2.write(1, data, 0, 1)
    blob3.read(0, data, 1, 2)
    blob4.read(0, data, 3, 2)

    for (i <- 1 until 5) {
      Predef.assert(data(i) == data(0), s"[$i]")
    }

    blob1.dispose()
    blob2.dispose()
    blob3.dispose()
    blob4.dispose()
  }

  @throws[SQLiteException]
  def testOpen(): Unit = {
    val db = open
    var blob = db.blob(null, "T", "value", 1, true)
    assert(!blob.isDisposed())
    assert(blob.isWriteAllowed)
    blob.dispose()
    assert(blob.isDisposed())
    blob = db.blob(null, "T", "value", 1, false)
    assert(!blob.isDisposed())
    assert(!blob.isWriteAllowed)
    assert(BlobTests.SIZE == blob.getSize)
    blob.dispose()
    assert(blob.isDisposed())
    try {
      blob = db.blob(null, "T", "value", -99, false)
      fail("opened " + blob)
    } catch {
      case e: SQLiteException =>

      // normal
    }
    try {
      blob = db.blob(null, "T", "value", 2, false)
      fail("opened " + blob)
    } catch {
      case e: SQLiteException =>
    }
    try {
      blob = db.blob(null, "T", "value", 0, false)
      fail("opened " + blob)
    } catch {
      case e: SQLiteException =>
    }
    try {
      blob = db.blob(null, "T", "value1", 1, false)
      fail("opened " + blob)
    } catch {
      case e: SQLiteException =>
    }
    try {
      blob = db.blob(null, "T1", "value", 1, false)
      fail("opened " + blob)
    } catch {
      case e: SQLiteException =>
    }
    try {
      blob = db.blob("x", "T", "value", 1, false)
      fail("opened " + blob)
    } catch {
      case e: SQLiteException =>
    }
    db.dispose()
  }

  @throws[SQLiteException]
  private def open = {
    val db = fileDb().open(true)
    db.exec("drop table if exists T")
    db.exec("create table T (id integer not null primary key autoincrement, value blob)")
    db.prepare("insert into T (value) values (?)").bindZeroBlob(1, BlobTests.SIZE).stepThrough.dispose()
    val id = db.getLastInsertId()
    assert(1 == id)
    db
  }

  @throws[SQLiteException]
  def testReadWrite(): Unit = {
    val db = open
    db.exec("BEGIN IMMEDIATE")
    var blob = db.blob(null, "T", "value", 1, true)
    val data = generate(BlobTests.SIZE)
    val CHUNK = 8192
    var p = 0
    while ( {
      p < data.length
    }) {
      val length = Math.min(CHUNK, data.length - p)
      blob.write(p, data, p, length)
      p += length
    }
    blob.dispose()
    db.exec("COMMIT")

    blob = db.blob(null, "T", "value", 1, false)
    val chunk = new Array[Byte](CHUNK)
    p = 0
    while (p < data.length) {
      val length = Math.min(CHUNK, data.length - p)
      blob.read(p, chunk, 0, length)

      for (i <- 0 until length) {
        Predef.assert(data(p + i) == chunk(i), "[" + (p + i) + "]")
      }
      p += length
    }

    db.dispose()
  }

  @throws[SQLiteException]
  def testMultipleOpen(): Unit = {
    val db = open
    BlobTests.doMultipleCheck(db)
    db.exec("BEGIN DEFERRED")
    BlobTests.doMultipleCheck(db)
    db.exec("COMMIT")
    db.exec("BEGIN IMMEDIATE")
    BlobTests.doMultipleCheck(db)
    db.exec("COMMIT")
    db.dispose()
  }

  @throws[SQLiteException]
  def testRowidEqualsPK(): Unit = {
    val db = fileDb.open(true)
    db.exec("drop table if exists T")
    db.exec("create table T (id integer not null primary key autoincrement, value blob)")
    db.prepare("insert into T (id, value) values (?, ?)").bind(1, 999).bindZeroBlob(2, BlobTests.SIZE).stepThrough.dispose()
    val blob = db.blob("T", "value", 999, true)
    assert(blob != null)
    db.dispose()
  }

  @throws[SQLiteException]
  def testBlobBindColumn(): Unit = {
    val data = generate(BlobTests.SIZE)
    val db = open
    var st = db.prepare("insert into T (value) values (?)")
    st.bind(1, data)
    st.step()
    st.dispose()
    st = db.prepare("select value from T")
    assert(st.step())
    var b1 = st.columnBlob(0)
    assert(BlobTests.SIZE == b1.length)

    for (i <- 0 until b1.length) {
      Predef.assert(0 == b1(i), s"[$i]")
    }
    assert(st.step())

    b1 = st.columnBlob(0)
    for (i <- 0 until b1.length) {
      Predef.assert( data(i) == b1(i), s"[$i]")
    }
    assert(!st.step())

    db.dispose()
  }
}