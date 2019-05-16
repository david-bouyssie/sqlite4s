package com.github.sqlite4s

import java.io.File
import java.util.Random

abstract class SQLiteConnectionFixture protected() extends SQLiteTestFixture() {

  private var myDB: SQLiteConnection = _
  private var myDbFile: File = _

  @throws[Exception]
  override protected def setUp(): Unit = {
    super.setUp()
    myDbFile = new File(tempName("db"))
  }

  @throws[Exception]
  override protected def tearDown(): Unit = {
    if (myDB != null) {
      myDB.dispose()
      myDB = null
    }
    myDbFile = null
    super.tearDown()
  }

  protected def dbFile: File = myDbFile

  // TODO: rename createFileDb
  protected def fileDb(): SQLiteConnection = createDb(myDbFile)

  protected def memDb(): SQLiteConnection = createDb(null)

  private def createDb(dbfile: File): SQLiteConnection = {
    if (myDB != null) myDB.dispose()

    myDB = new SQLiteConnection(dbfile)
    myDB.setStepsPerCallback(1)

    myDB
  }

  protected def generate(size: Int): Array[Byte] = {
    val result = new Array[Byte](size)
    val r = new Random()
    var i = 0

    while (i < result.length ) {
      result(i) = r.nextInt.toByte
      i += 1
    }

    result
  }
}

