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

import java.io.File
import java.util.concurrent.Semaphore

import utest._

object BusyTests extends SQLiteConnectionFixture {

  val tests = Tests {
    "testReadLockTransactionFails" - testReadLockTransactionFails
    "testReadLockTransactionFailsWithTimeout" - testReadLockTransactionFailsWithTimeout
    "testReadLockTransactionWaits" - testReadLockTransactionWaits
    "testBusySpillPre"36 - testBusySpillPre36
    "testBusySpillPost"36 - testBusySpillPost36
  }


  private var myReader: SQLiteConnection = _
  private var myWriter: SQLiteConnection = _
  private var myFailure: String = _

  @throws[Exception]
  override protected def setUp(): Unit = {
    super.setUp()
    val dbfile = dbFile
    assert(!dbfile.exists)
    myReader = new SQLiteConnection(dbfile)
    myWriter = new SQLiteConnection(dbfile)
    myReader.open().exec("pragma cache_size=5").exec("pragma page_size=1024")
    myReader.exec("create table x (x)").exec("insert into x values (1)")
    myFailure = null
  }

  @throws[Exception]
  override protected def tearDown(): Unit = {
    myReader.dispose()
    myReader = null
    myWriter = null
    super.tearDown()
    if (myFailure != null) fail(myFailure)
  }

  @throws[SQLiteException]
  @throws[InterruptedException]
  def testReadLockTransactionFails(): Unit = {
    val st = myReader.prepare("select * from x")
    st.step()
    assert(st.hasRow)
    val t = new Thread() {
      override def run(): Unit = try {
        myWriter.open()
        myWriter.exec("begin immediate")
        myWriter.exec("insert into x values (2)")
        try {
          myWriter.exec("commit")
          myFailure = "successfully committed"
        } catch {
          case e: SQLiteBusyException =>
            e.printStackTrace()
            if (myWriter.getAutoCommit()) myFailure = "transaction rolled back"
        }
      } catch {
        case e: SQLiteException =>
          e.printStackTrace()
          myFailure = String.valueOf(e)
      } finally myWriter.dispose()
    }
    t.start()
    t.join()
  }

  @throws[SQLiteException]
  @throws[InterruptedException]
  def testReadLockTransactionFailsWithTimeout(): Unit = {
    val st = myReader.prepare("select * from x")
    st.step()
    assert(st.hasRow)

    val t = new Thread() {
      override def run(): Unit = try {
        myWriter.open()
        val timeout = 2000
        myWriter.setBusyTimeout(timeout)
        myWriter.exec("begin immediate")
        myWriter.exec("insert into x values (2)")
        val t1 = System.currentTimeMillis
        try {
          myWriter.exec("commit")
          myFailure = "successfully committed"
        } catch {
          case e: SQLiteBusyException =>
            val t2 = System.currentTimeMillis
            Predef.assert(t2 - t1 > timeout - 100, String.valueOf(t2 - t1))
            e.printStackTrace()
            if (myWriter.getAutoCommit()) myFailure = "transaction rolled back"
        }
      } catch {
        case e: SQLiteException =>
          e.printStackTrace()
          myFailure = String.valueOf(e)
      } finally myWriter.dispose()
    }
    t.start()
    t.join()
  }

  @throws[SQLiteException]
  @throws[InterruptedException]
  def testReadLockTransactionWaits(): Unit = {
    val timeout = 2000
    val st = myReader.prepare("select * from x")
    st.step()
    assert(st.hasRow)

    val s = new Semaphore(1)
    s.acquire()

    val t = new Thread() {
      override def run(): Unit = try {
        myWriter.open()
        myWriter.setBusyTimeout(timeout)
        myWriter.exec("begin immediate")
        myWriter.exec("insert into x values (2)")
        s.release()
        val t1 = System.currentTimeMillis
        myWriter.exec("commit")
        val t2 = System.currentTimeMillis
        System.out.println("commit waited for " + (t2 - t1))
      } catch {
        case e: SQLiteException =>
          e.printStackTrace()
          myFailure = String.valueOf(e)
      } finally myWriter.dispose()
    }

    t.start()
    s.acquire()
    s.release()
    Thread.sleep(timeout / 2)
    st.reset()
    t.join()
  }

  @throws[SQLiteException]
  @throws[InterruptedException]
  def testBusySpillPre36(): Unit = {
    if (SQLite.getSQLiteVersionNumber >= 3006000) { // skipping
      return
    }
    val st = myReader.prepare("select * from x")
    st.step()
    assert(st.hasRow)

    val t = new Thread() {
      override def run(): Unit = try {
        myWriter.open().exec("pragma cache_size=5")
        myWriter.exec("begin immediate")
        val st = myWriter.prepare("insert into x values (?)")
        try {
          var i = 0
          while (i < 20) {
            st.bind(1, garbageString(512))
            st.step()
            st.reset()

            i += 1
          }
          myFailure = "successfully inserted data in one transaction that exceeds disk cache and shared lock is preserved"
        } catch {
          case e: SQLiteBusyException =>
            if (!myWriter.getAutoCommit) myFailure = "transaction not rolled back"
        } finally st.dispose()
      } catch {
        case e: SQLiteException =>
          e.printStackTrace()
          myFailure = String.valueOf(e)
      } finally myWriter.dispose()
    }
    t.start()
    t.join()
  }

  @throws[SQLiteException]
  @throws[InterruptedException]
  def testBusySpillPost36(): Unit = {
    if (SQLite.getSQLiteVersionNumber < 3006000) return
    val st = myReader.prepare("select * from x")
    st.step()
    assert(st.hasRow)

    val t = new Thread() {
      override def run(): Unit = try {
        myWriter.open().exec("pragma cache_size=5")
        myWriter.exec("begin immediate")
        val st = myWriter.prepare("insert into x values (?)")
        try {
          myFailure = "couldn't insert data"
          var i = 0
          while (i < 20) {
            st.bind(1, garbageString(512))
            st.step()
            st.reset()

            i += 1
          }
          // should get here
          myFailure = "commit didn't throw busy exception"
          myWriter.exec("commit")
        } catch {
          case e: SQLiteBusyException =>
            if (myWriter.getAutoCommit()) myFailure = "transaction rolled back"
            myFailure = null
        } finally st.dispose()
      } catch {
        case e: SQLiteException =>
          e.printStackTrace()
          myFailure = String.valueOf(e)
      } finally myWriter.dispose()
    }
    t.start()
    t.join()
  }
}

*/