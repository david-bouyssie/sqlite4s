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

import java.io.File
import java.util

import SQLITE_WRAPPER_ERROR_CODE.WRAPPER_MISUSE
import utest._


object SQLiteBackupTests extends SQLiteConnectionFixture {

  private val ROWS_NUMBER = 5400

  val tests = Tests {
    "testOneStepBackupMemoryToFile" - testOneStepBackupMemoryToFile()
    "testOneStepBackupMemoryToMemory" - testOneStepBackupMemoryToMemory()
    "testOneStepBackupFileToFile" - testOneStepBackupFileToFile()
    "testOneStepBackupFileToMemory" - testOneStepBackupFileToMemory()
    "testStepFailWhenConnectionDisposed" - testStepFailWhenConnectionDisposed()
    "testDestinationAutoUpdate" - testDestinationAutoUpdate()
    "testBackupRestarting" - testBackupRestarting()
    "testBackupWithSharingLockOnSource" - testBackupWithSharingLockOnSource()
    "testBackupWithReservedLock" - testBackupWithReservedLock()
    "testBackupFailWithReservedLockEstablishedBySourceConnection" - testBackupFailWithReservedLockEstablishedBySourceConnection()
    "testBackupFailWhenExclusiveLockOnSourceEstablished" - testBackupFailWhenExclusiveLockOnSourceEstablished()
  }

  @throws[SQLiteException]
  def testOneStepBackupMemoryToFile(): Unit = {
    backupOneStep(true, new File(tempName("db")))
  }

  @throws[SQLiteException]
  def testOneStepBackupMemoryToMemory(): Unit = {
    backupOneStep(true, null)
  }

  @throws[SQLiteException]
  def testOneStepBackupFileToFile(): Unit = {
    backupOneStep(false, new File(tempName("db1")))
  }

  @throws[SQLiteException]
  def testOneStepBackupFileToMemory(): Unit = {
    backupOneStep(false, null)
  }

  @throws[SQLiteException]
  def testStepFailWhenConnectionDisposed(): Unit = {

    var source = createDB(true)
    var backup = source.initializeBackup(null)
    source.dispose()
    assertStepFailsWithError(backup, WRAPPER_MISUSE)

    source = createDB(true)
    backup = source.initializeBackup(null)
    var destination = backup.getDestinationConnection()
    destination.dispose()
    assertStepFailsWithError(backup, WRAPPER_MISUSE)

    source.dispose()
    source = createDB(true)
    backup = source.initializeBackup(null)
    destination = backup.getDestinationConnection()
    source.dispose()
    destination.dispose()
    assertStepFailsWithError(backup, WRAPPER_MISUSE)
  }

  @throws[SQLiteException]
  def testDestinationAutoUpdate(): Unit = {
    Logging.configureLogger(Logging.LogLevel.INFO)

    val source = createDB(false)
    val backup = source.initializeBackup(null)
    val destination = backup.getDestinationConnection()
    var finished = backup.backupStep(10)
    assert(!finished)
    val oldPageCount = backup.getPageCount()
    val oldRemaining = backup.getRemaining()
    modifyDB(source)
    val nPages = 1
    finished = backup.backupStep(nPages)
    assert(!finished)
    val newPageCount = backup.getPageCount()
    val newRemaining = backup.getRemaining()
    val additionalPages = newPageCount - oldPageCount
    val newRemainingExpected = oldRemaining - nPages + additionalPages
    assert(newRemainingExpected == newRemaining)
    backup.backupStep(-1)
    backup.dispose(false)
    assertDBSEquals(source, destination)
    source.dispose()
    destination.dispose()
  }

  @throws[SQLiteException]
  def testBackupRestarting(): Unit = {
    val source = createDB(false)
    val sourceDBFile = source.getDatabaseFile()
    val anotherConnectionToSource = new SQLiteConnection(sourceDBFile).open()
    val backup = source.initializeBackup(null)
    val destination = backup.getDestinationConnection()
    var finished = backup.backupStep(10)
    assert(!finished)
    modifyDB(anotherConnectionToSource)
    val nPages = 1
    finished = backup.backupStep(nPages)
    assert(!finished)
    val newPageCount = backup.getPageCount()
    val newRemaining = backup.getRemaining()
    val newRemainingExpected = newPageCount - nPages
    assert(newRemainingExpected == newRemaining)
    backup.backupStep(-1)
    backup.dispose(false)
    assertDBSEquals(source, destination)
    assertDBSEquals(anotherConnectionToSource, destination)
    source.dispose()
    destination.dispose()
    anotherConnectionToSource.dispose()
  }

  @throws[SQLiteException]
  def testBackupWithSharingLockOnSource(): Unit = {
    val source = createDB(false)
    val anotherConnectionToSource = new SQLiteConnection(source.getDatabaseFile()).open()
    val backup = source.initializeBackup(null)
    val destination = backup.getDestinationConnection()
    val select = anotherConnectionToSource.prepare("select * from tab")
    select.step()
    backup.backupStep(-1)
    backup.dispose(false)
    assertDBSEquals(source, destination)
    source.dispose()
    destination.dispose()
    anotherConnectionToSource.dispose()
  }

  @throws[SQLiteException]
  def testBackupWithReservedLock(): Unit = {
    val source = createDB(false)
    val anotherConnectionToSource = new SQLiteConnection(source.getDatabaseFile()).open()
    val backup = source.initializeBackup(null)
    anotherConnectionToSource.exec("begin immediate")
    val finished = backup.backupStep(-1)
    assert(finished)
    backup.dispose()
    source.dispose()
    anotherConnectionToSource.dispose()
  }

  @throws[SQLiteException]
  def testBackupFailWithReservedLockEstablishedBySourceConnection(): Unit = {
    val source = createDB(false)
    val backup = source.initializeBackup(null)
    source.exec("begin immediate")
    try {
      backup.backupStep(-1)
      fail("Backup when RESERVED lock established on source connection by itself")
    } catch {
      case e: SQLiteBusyException =>

      //ok
    }
    backup.dispose()
    source.dispose()
  }

  @throws[SQLiteException]
  def testBackupFailWhenExclusiveLockOnSourceEstablished(): Unit = {
    val source = createDB(false)
    val anotherConnectionToSource = new SQLiteConnection(source.getDatabaseFile()).open()
    val backup = source.initializeBackup(null)
    anotherConnectionToSource.exec("begin exclusive")
    try {
      backup.backupStep(-1)
      fail("Backup when EXCLUSIVE lock established on source db")
    } catch {
      case e: SQLiteBusyException =>

    }
    backup.dispose()
    source.dispose()
  }

  @throws[SQLiteException]
  private def createDB(inMemory: Boolean): SQLiteConnection = {
    var connection = if (inMemory) memDb() else fileDb()

    connection = connection.open().exec("create table tab (val integer)")
    val statement = connection.prepare("insert into tab values (?)")

    //---// Setting log level to WARNING because FINE logging cause many useless and similarly identical messages/ that crash JUnit
    //---// val previousLevel = java.util.logging.Logger.getLogger("com.almworks.sqlite4java").getLevel
    //---// java.util.logging.Logger.getLogger("com.almworks.sqlite4java").setLevel(java.util.logging.Level.WARNING)

    connection.exec("begin immediate")
    var i = 0
    while (i < SQLiteBackupTests.ROWS_NUMBER) {
      statement.bind(1, i)
      statement.step()
      statement.reset()

      i += 1
    }
    //---// java.util.logging.Logger.getLogger("com.almworks.sqlite4java").setLevel(previousLevel)
    connection.exec("commit")
    statement.dispose()
    connection
  }

  @throws[SQLiteException]
  private def modifyDB(connection: SQLiteConnection): Unit = {
    val modifyStatement = connection.prepare("delete from tab where val <= 1000")
    modifyStatement.step()
    //    SQLiteStatement modifyStatement = connection.prepare("insert into tab values(?)");
    //    connection.exec("begin immediate");
    //    for (int i = 1; i < 400; i++) {
    //      modifyStatement.bind(1, ROWS_NUMBER + i);
    //      modifyStatement.step();
    //      modifyStatement.reset();
    //    }
    //    connection.exec("commit");
    modifyStatement.dispose()
  }

  @throws[SQLiteException]
  private def assertDBSEquals(source: SQLiteConnection, backup: SQLiteConnection): Unit = {
    val sourceColumnCount = columnCount(source)
    val backupColumnCount = columnCount(backup)
    assert(sourceColumnCount == backupColumnCount)
    val sourceValues = getArray(source, sourceColumnCount)
    val backupValues = getArray(backup, backupColumnCount)
    assert(util.Arrays.equals(sourceValues, backupValues))
  }

  @throws[SQLiteException]
  private def columnCount(connection: SQLiteConnection): Int = {
    val countStatement = connection.prepare("select count(val) from tab")
    countStatement.step()
    val result = countStatement.columnInt(0)
    countStatement.dispose()
    result
  }

  @throws[SQLiteException]
  private def getArray(connection: SQLiteConnection, arrayLength: Int): Array[Long] = {
    val result = new Array[Long](arrayLength)
    val selectStatement = connection.prepare("select val from tab order by val")
    selectStatement.loadLongs(0, result, 0, arrayLength)
    selectStatement.dispose()
    result
  }

  @throws[SQLiteException]
  private def backupOneStep(sourceInMemory: Boolean, destinationFile: File): Unit = {
    try {
      val source = createDB(sourceInMemory)
      val backup = source.initializeBackup(destinationFile)
      val destination = backup.getDestinationConnection()
      val finished = backup.backupStep(-1)
      assert(finished)
      backup.dispose(false)
      assertDBSEquals(source, destination)
      source.dispose()
      destination.dispose()
    } catch {
      case t: Throwable => "Caught an error while executing backupOneStep(): " + t.getMessage
    }
  }

  private def assertStepFailsWithError(backup: SQLiteBackup, errorCode: Int): Unit = {
    try {
      backup.backupStep(-1)
      fail("Backup disposed DB")
    } catch {
      case e: SQLiteException =>
        if (e.getErrorCode() != errorCode) println(s"Got error code '${e.getErrorCode()}' while expecting '$errorCode'")
        assert(e.getErrorCode() == errorCode)
    }
  }
}