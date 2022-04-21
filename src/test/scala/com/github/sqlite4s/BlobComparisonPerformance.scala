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

/*package com.github.sqlite4s

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.logging.Level
import java.util.logging.Logger

import utest._

/**
  * Tests to compare performance of byte[] and xxxStream operations on blobs
  */
object BlobComparisonPerformance extends SQLiteConnectionFixture {

  val tests = Tests {
    "testInsertion" - testInsertion
    "testRetrieval" - testRetrieval
  }

  abstract private[sqlite4s] class InsertionMethod(val myName: String) {
    override def toString: String = myName

    @throws[SQLiteException]
    @throws[IOException]
    def insert(st: SQLiteStatement, source: Array[Byte]): Unit
  }

  private class AllocateEachTimeMethod() extends BlobComparisonPerformance.InsertionMethod("ALLOCATE_EACH_TIME") {
    @throws[SQLiteException]
    override def insert(st: SQLiteStatement, source: Array[Byte]): Unit = {
      val length = source.length
      val buffer = new Array[Byte](length)
      System.arraycopy(source, 0, buffer, 0, length)
      st.bind(1, buffer, 0, length)
      st.step()
      st.reset()
    }
  }

  private class DirectBufferMethod() extends BlobComparisonPerformance.InsertionMethod("DIRECT_BUFFER") {
    @throws[SQLiteException]
    @throws[IOException]
    override def insert(st: SQLiteStatement, source: Array[Byte]): Unit = {
      val out = st.bindStream(1)
      out.write(source)
      out.close()
      st.step
      st.reset
    }
  }

  private class DirectBufferSizeKnownMethod() extends BlobComparisonPerformance.InsertionMethod("DIRECT_BUFFER_SIZE_KNOWN") {
    @throws[SQLiteException]
    @throws[IOException]
    override def insert(st: SQLiteStatement, source: Array[Byte]): Unit = {
      val out = st.bindStream(1, source.length)
      out.write(source)
      out.close()
      st.step
      st.reset
    }
  }

  abstract private class SelectionMethod {
    override def toString: String = {
      val className = getClass.getName
      className.substring(className.lastIndexOf('$') + 1)
    }

    @throws[SQLiteException]
    @throws[IOException]
    def select(st: SQLiteStatement): Unit
  }

  private class ByteArrayMethod extends BlobComparisonPerformance.SelectionMethod {
    @throws[SQLiteException]
    override def select(st: SQLiteStatement): Unit = {
      val bytes = st.columnBlob(0)
      if (bytes != null) {
        val length = bytes.length
        var i = 0
        while ( {
          i < length
        }) {
          i += 1; i - 1
        }
      }
    }
  }

  private class BufferReadByByte extends BlobComparisonPerformance.SelectionMethod {
    @throws[SQLiteException]
    @throws[IOException]
    override def select(st: SQLiteStatement): Unit = {
      val in = st.columnStream(0)
      while ( {
        in.read >= 0
      }) {}
      in.close()
    }
  }

  private var myBuffer = null
  private val COPY_TO_PERSISTENT_BUFFER = new BlobComparisonPerformance#CopyToPersistentBufferMethod
  private val ALLOCATE_EACH_TIME = new BlobComparisonPerformance.AllocateEachTimeMethod
  private val DIRECT_BUFFER = new BlobComparisonPerformance.DirectBufferMethod
  private val DIRECT_BUFFER_SIZE_KNOWN = new BlobComparisonPerformance.DirectBufferSizeKnownMethod
  private val BYTEARRAY = new BlobComparisonPerformance.ByteArrayMethod
  private val BUFFER_READ_BY_BYTE = new BlobComparisonPerformance.BufferReadByByte
  private val BUFFER_READ_INTO_ARRAY = new BlobComparisonPerformance#BufferReadIntoArray
  private val insertionMethods = Array(COPY_TO_PERSISTENT_BUFFER, ALLOCATE_EACH_TIME, DIRECT_BUFFER, DIRECT_BUFFER_SIZE_KNOWN)
  private val selectionMethods = Array(BYTEARRAY, BUFFER_READ_BY_BYTE, BUFFER_READ_INTO_ARRAY)

  @throws[Exception]
  override protected def setUp(): Unit = {
    SQLite.setDebugBinaryPreferred(false)
    super.setUp()
  }

  @throws[SQLiteException]
  @throws[IOException]
  def testInsertion(): Unit = {
    Logger.getLogger("sqlite").setLevel(Level.INFO)
    val db = fileDb.open(true)
    recreateTable(db)
    val st = db.prepare("insert into T values (?)")
    // 16 kb
    val data = generate(1 << 10)
    val transactions = 80
    val inserts = 100
    System.out.println("writing " + (data.length * transactions * inserts >> 20) + " mb of data")
    // make hot
    for (method <- insertionMethods) {
      runInsertion(db, st, transactions, inserts, data, method)
    }
    for (method <- insertionMethods) {
      runInsertion(db, st, transactions, inserts, data, method)
    }
    val times = new Array[Long](insertionMethods.length)
    var i = 0
    while ( {
      i < insertionMethods.length
    }) times(i) = runInsertion(db, st, transactions, inserts, data, insertionMethods(i)) {
      i += 1; i - 1
    }
    System.out.println()
    System.out.println("Result:")
    var i = 0
    while ( {
      i < insertionMethods.length
    }) {
      System.out.println(insertionMethods(i) + ": " + times(i))
      {
        i += 1; i - 1
      }
    }
    db.dispose()
  }

  @throws[SQLiteException]
  @throws[IOException]
  def testRetrieval(): Unit = {
    Logger.getLogger("sqlite").setLevel(Level.INFO)
    val db = fileDb.open(true)
    recreateTable(db)
    var st = db.prepare("insert into T values (?)")
    val data = generate(1 << 20)
    val transactions = 10
    val inserts = 20
    System.out.println("writing " + (data.length * transactions * inserts >> 20) + " mb of data")
    runInsertion(db, st, transactions, inserts, data, DIRECT_BUFFER)
    st.dispose()
    st = db.prepare("select V from T")
    for (method <- selectionMethods) {
      runSelection(st, method)
    }
    for (method <- selectionMethods) {
      runSelection(st, method)
    }
    val times = new Array[Long](selectionMethods.length)
    var i = 0
    while ( {
      i < selectionMethods.length
    }) times(i) = runSelection(st, selectionMethods(i)) {
      i += 1; i - 1
    }
    System.out.println()
    System.out.println("Result:")
    var i = 0
    while ( {
      i < selectionMethods.length
    }) {
      System.out.println(selectionMethods(i) + ": " + times(i))
      {
        i += 1; i - 1
      }
    }
    db.dispose()
  }

  @throws[SQLiteException]
  private def recreateTable(db: SQLiteConnection) = {
    db.exec("drop table if exists T")
    db.exec("create table T (V)")
  }

  @throws[SQLiteException]
  @throws[IOException]
  private def runInsertion(db: SQLiteConnection, st: SQLiteStatement, transactionCount: Int, insertsPerTransaction: Int, data: Array[Byte], method: BlobComparisonPerformance.InsertionMethod) = {
    recreateTable(db)
    System.out.println("running " + method)
    val start = System.currentTimeMillis
    var i = 0
    while ( {
      i < transactionCount
    }) {
      db.exec("BEGIN IMMEDIATE")
      var j = 0
      while ( {
        j < insertsPerTransaction
      }) {
        method.insert(st, data)
        {
          j += 1; j - 1
        }
      }
      db.exec("COMMIT")
      System.out.print(".")
      {
        i += 1; i - 1
      }
    }
    System.out.println()
    val end = System.currentTimeMillis
    end - start
  }

  @throws[SQLiteException]
  @throws[IOException]
  private def runSelection(st: SQLiteStatement, method: BlobComparisonPerformance.SelectionMethod) = {
    st.reset
    System.out.println("running " + method)
    val start = System.currentTimeMillis
    while ( {
      st.step
    }) method.select(st)
    val end = System.currentTimeMillis
    end - start
  }

  private class CopyToPersistentBufferMethod() extends BlobComparisonPerformance.InsertionMethod("COPY_TO_PERSISTENT_BUFFER") {
    @throws[SQLiteException]
    override def insert(st: SQLiteStatement, source: Array[Byte]): Unit = {
      val length = source.length
      if (myBuffer == null || myBuffer.length < length) myBuffer = new Array[Byte](length)
      System.arraycopy(source, 0, myBuffer, 0, length)
      st.bind(1, myBuffer, 0, length)
      st.step
      st.reset
    }
  }

  private class BufferReadIntoArray extends BlobComparisonPerformance.SelectionMethod {
    @throws[SQLiteException]
    @throws[IOException]
    override def select(st: SQLiteStatement): Unit = {
      val in = st.columnStream(0)
      if (myBuffer == null || myBuffer.length != 8192) myBuffer = new Array[Byte](8192)
      var len = 0
      while ( {
        (len = in.read(myBuffer)) > 0
      }) {
        var i = 0
        while ( {
          i < len
        }) {
          i += 1; i - 1
        }
      }
    }
  }

}*/