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

import scala.scalanative.native._
import java.io.File
import java.util.Random

import com.github.sqlite4s.bindings.sqlite
import utest.TestSuite

object SQLiteTestFixture {

  //private val SEED = "com.almworks.sqlite4java.seed"


  /**
    * Add {@code -Dcom.almworks.sqlite4java.seed=12312423455}
    * to system properties to reproduce the test
    **/
  def createRandom(): Random = new Random(System.currentTimeMillis)/*{
    val seedStr = System.getProperty(SEED)
    var seed = 0L
    if (seedStr != null) {
      seed = Long.parseLong(seedStr)
      System.out.println("Using seed from settings: " + seed)
    }
    else {
      seed = System.currentTimeMillis
      System.out.printf("Use -Dcom.almworks.sqlite4java.seed=%d to reproduce the test\n", seed)
    }
    new Random(seed)
  }*/

  def garbageString(count: Int): String = {
    val sb = new java.lang.StringBuilder()
    val r = createRandom()
    var i = 0

    while (i < count) {
      if (i == 500) {
        sb.appendCodePoint(0)
      } else {
        val c = r.nextInt(0x110000)

        if (c >= 0xD800 && c <= 0xDFFF) {} // surrogate
        else {
          if (c == 0xFFFF || c == 0xFFFE || c == 0xFEFF) {}
          else sb.appendCodePoint(c)
        }
      }
      i += 1
    }

    sb.toString
  }

/*
  private def installFormatter(logger: Logger, formatter: Formatter, level: Level): Unit = {
    logger.setLevel(level)
    val handlers = logger.getHandlers
    if (handlers != null) for (handler <- handlers) {
      handler.setFormatter(formatter)
      handler.setLevel(level)
    }
    val parent = logger.getParent
    if (parent != null) installFormatter(parent, formatter, level)
  }

  private object DecentFormatter {
    private val dateFormat = new SimpleDateFormat("yyMMdd-HHmmss")
  }

  private class DecentFormatter extends Formatter {
    override def format(record: LogRecord): String = {
      val sb = new StringBuilder
      sb.append(DecentFormatter.dateFormat.format(new Date(record.getMillis)))
      sb.append(' ')
      sb.append(record.getLevel.getLocalizedName)
      sb.append(' ')
      sb.append(record.getMessage)
      if (record.getThrown != null) try {
        val sw = new StringWriter
        val pw = new PrintWriter(sw)
        record.getThrown.printStackTrace(pw)
        pw.close()
        sb.append('\n')
        sb.append(sw.toString)
      } catch {
        case ex: Exception =>

      }
      sb.append('\n')
      sb.toString
    }
  }

  //try installFormatter(Logger.getLogger("com.almworks.sqlite4java"), new SQLiteTestFixture.DecentFormatter, Level.FINE)

*/

}

abstract class SQLiteTestFixture() extends TestSuite with Logging {

  // Configure the Logger
  Logging.configureLogger(Logging.LogLevel.INFO)

  private val enableTempDataDeletion = true
  final private val sqliteManual = new SQLiteWrapper()
  private var myTempDir: File = _
  private var myLastRC = 0
  private var myLastDb: SQLiteConnection.Handle = _

  protected def garbageString(count: Int): String = {
    SQLiteTestFixture.garbageString(count)
  }

  override def utestBeforeEach(path: Seq[String]): Unit = {
    println(s"on before each test")
    setUp()
  }

  override def utestAfterEach(path: Seq[String]): Unit = {
    println(s"on after each test")
    tearDown()
  }

  @throws[Exception]
  protected def setUp(): Unit = {
    val name = getClass.getName
    val dirName = s"${name.substring(name.lastIndexOf('.') + 1)}_${System.currentTimeMillis()}.test"

    //val dir = File.createTempFile(dirName)
    val dir = new File(s"./target/tests/$dirName").getCanonicalFile
    var success = true
    if (dir.isDirectory) {
      var success = dir.delete
      assert(success, dir)
    }

    success = dir.mkdirs
    assert(success, dir)
    myTempDir = dir
  }

  @throws[Exception]
  protected def tearDown(): Unit = {
    if (myLastDb != null) try
      close()
    catch {
      case e: Throwable =>

      // to heck
    }

    val dir = myTempDir
    if (dir != null) {
      myTempDir = null
      if (enableTempDataDeletion && dir.isDirectory) deleteRecursively(dir, 100)
    }
  }

  private def deleteRecursively(dir: File, safe: Int): Int = {
    var localSafe = safe

    logger.trace(s"Deleting the full content of $dir")

    val files = try { dir.listFiles }
    catch {
      case ioe: java.io.IOException => {
        logger.warn(s"Content of directory $dir cannot be listed (maybe it is empty?)", ioe)
        null
      }
    }

    if (files != null) {
      for (file <- files) {
        val name = file.getName
        if ("." == name || ".." == name) {}
        else {
          if (localSafe < 0) throw new Exception("safe deletion threshold exceeded")
          localSafe -= 1

          if (file.isDirectory) localSafe = deleteRecursively(file, localSafe)
          else if (!file.delete) file.deleteOnExit()
        }
      }
    }

    if (!dir.delete) dir.deleteOnExit()

    localSafe
  }

  protected def tempDir(): File = {
    val dir = myTempDir
    assert(dir != null, "dir is null")
    dir
  }

  protected def tempName(fileName: String): String = {
    //println("tempDir(): " + tempDir())
    val tempName = new File(tempDir(), fileName).getAbsolutePath
    println("tempName: " + tempName)
    tempName
  }

  protected def open(name: String, flags: Int): Unit = {
    myLastDb = sqliteManual.sqlite3OpenV2(name, flags)
    myLastRC = sqliteManual.getLastReturnCode()
  }

  protected def lastResultCode(): Int = myLastRC

  protected def lastDb(): SQLiteConnection.Handle = myLastDb

  protected def close(): Unit = {
    val before = sqlite.sqlite3_memory_used()
    myLastRC = sqlite.sqlite3_close(myLastDb)

    val after = sqlite.sqlite3_memory_used()
    System.out.println("mem: " + before + "->" + after)
    myLastDb = null
  }

  protected def exec(sql: String): Unit = {
    try {
      myLastRC = SQLiteWrapper.sqlite3Exec(myLastDb, sql)._1
    } catch {
      case e: Exception => println("caught error in exec: " + e)
    }
  }

  // Emulate JUnit fail function
  @throws[Exception]
  protected def fail(msg: String): Unit = {
    throw new Exception(msg)
  }

  protected def assertResult(result: Int): Unit = {
    utest.assert(result == lastResultCode() )
  }
  protected def assertDb(): Unit = {
    utest.assert(lastDb != null)
  }

  protected def assertOk(): Unit = {
    assertResult(sqlite.SQLITE_CONSTANT.SQLITE_OK)
  }

  protected def prepare(sql: String): SQLiteStatement.Handle = {
    val stmt = sqliteManual.sqlite3PrepareV2(myLastDb, sql)
    myLastRC = sqliteManual.getLastReturnCode()
    stmt
  }

  protected def prepare(sql: String, prepFlags: Int): SQLiteStatement.Handle = {
    val stmt = sqliteManual.sqlite3PrepareV3(myLastDb, sql, prepFlags)
    myLastRC = sqliteManual.getLastReturnCode()
    stmt
  }

  protected def bindLong(stmt: SQLiteStatement.Handle, index: Int, value: Long): Unit = {
    myLastRC = sqlite.sqlite3_bind_int64(stmt, index, value)
  }

  protected def step(stmt: SQLiteStatement.Handle): Unit = {
    myLastRC = sqlite.sqlite3_step(stmt)
  }

  protected def reset(stmt: SQLiteStatement.Handle): Unit = {
    myLastRC = sqlite.sqlite3_reset(stmt)
  }

  protected def finalize(stmt: SQLiteStatement.Handle): Unit = {
    myLastRC = sqlite.sqlite3_finalize(stmt)
  }

  protected def bindText(stmt: SQLiteStatement.Handle, index: Int, value: String): Unit = {
    myLastRC = SQLiteWrapper.sqlite3BindText(stmt, index, value)
  }

  protected def columnText(stmt: SQLiteStatement.Handle, column: Int): String = {
    val r = sqliteManual.sqlite3ColumnText(stmt, column)
    myLastRC = sqliteManual.getLastReturnCode()
    r
  }
}
