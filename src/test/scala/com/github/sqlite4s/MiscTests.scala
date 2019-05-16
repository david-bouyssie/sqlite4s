package com.github.sqlite4s

import java.io.File

import utest._

import com.github.sqlite4s.bindings.sqlite_addons.SQLITE_CONSTANT

object MiscTests extends SQLiteTestFixture {

  val tests = Tests {
    'testCreatingDatabaseInNonExistingDirectory - testCreatingDatabaseInNonExistingDirectory
    //'testSetDirectory - testSetDirectory
  }

  def testCreatingDatabaseInNonExistingDirectory(): Unit = {
    val dir = tempName("newDir")
    val db = new File(dir, "db")
    val c = new SQLiteConnection(db)

    try {
      c.open(true)
      fail("created a connection to db in a non-existing directory")
    } catch {
      case e: SQLiteException =>
        assert(e.getMessage.toLowerCase(java.util.Locale.US).contains("file"))
    }
  }

  // FIXME: this test is causing a linker issue
  /*@throws[SQLiteException]
  def testSetDirectory(): Unit = {
    if (scala.scalanative.runtime.Platform.isWindows()) {
      SQLite.setDirectory(SQLITE_CONSTANT.SQLITE_WIN32_DATA_DIRECTORY_TYPE, "test1")
      SQLite.setDirectory(SQLITE_CONSTANT.SQLITE_WIN32_DATA_DIRECTORY_TYPE, null)
      SQLite.setDirectory(SQLITE_CONSTANT.SQLITE_WIN32_TEMP_DIRECTORY_TYPE, "test2")
      SQLite.setDirectory(SQLITE_CONSTANT.SQLITE_WIN32_TEMP_DIRECTORY_TYPE, null)
    }
    else {
      intercept[SQLiteException] {
        SQLite.setDirectory(SQLITE_CONSTANT.SQLITE_WIN32_DATA_DIRECTORY_TYPE, "test1")
        fail("call to SQLite.setDirectory() should fail on non-Windows operating systems")
      }
    }
  }*/

  /*@throws[IOException]
  def testAdjustingLibPath(): Unit = {
    val jar = tempName("sqlite4java.jar")
    val jarFile = new File(jar)
    new RandomAccessFile(jarFile, "rw").close
    assertTrue(jarFile.exists)
    jarFile.deleteOnExit
    val dir = jarFile.getParentFile.getPath
    val c = File.pathSeparatorChar
    val url = "jar:file:" + jar + "!/sqlite/Internal.class"
    assertEquals(dir, Internal.getDefaultLibPath(null, url))
    assertEquals(dir, Internal.getDefaultLibPath("xxx", url))
    assertEquals(dir, Internal.getDefaultLibPath("xxx" + c + c + "yyy" + c, url))
    assertNull(Internal.getDefaultLibPath("xxx" + File.pathSeparatorChar + File.pathSeparatorChar + dir, url))
    assertEquals(dir, Internal.getDefaultLibPath(dir + "x", url))
    assertNull(Internal.getDefaultLibPath(dir, url))
  }

  @throws[IOException]
  def testJarSuffix(): Unit = {
    var jar = tempName("sqlite4java.jar")
    var jarFile = new File(jar)
    new RandomAccessFile(jarFile, "rw").close
    assertTrue(jarFile.exists)
    jarFile.deleteOnExit
    var url = "jar:file:" + jar + "!/sqlite/Internal.class"
    assertNull(Internal.getVersionSuffix(url))
    jar = tempName("sqlite4java-0.1999-SNAPSHOT.jar")
    jarFile = new File(jar)
    new RandomAccessFile(jarFile, "rw").close
    assertTrue(jarFile.exists)
    jarFile.deleteOnExit
    url = "jar:file:" + jar + "!/sqlite/Internal.class"
    assertEquals("-0.1999-SNAPSHOT", Internal.getVersionSuffix(url))
  }
  */

}
