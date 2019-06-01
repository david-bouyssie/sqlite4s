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
import scala.scalanative.runtime.Platform

import com.github.sqlite4s.c.util.CUtils

import bindings._
import bindings.sqlite.SQLITE_CONSTANT._

/**
  * The SQLite object has several utility methods that are applicable to the whole instance of
  * SQLite library within the current process. It is not needed for basic operations.
  * <p/>
  * All methods in this class are <strong>thread-safe</strong>.
  *
  * @author Igor Sereda
  */
object SQLite {

  /**
    * The version of SQLite database.
    *
    * @see <a href="http://www.sqlite.org/c3ref/libversion.html">sqlite3_libversion</a>
    */
  lazy val libVersion: CString = sqlite.sqlite3_libversion()

  /** SQLite4Java backward compatibility */
  def getSQLiteVersion(): String = fromCString(libVersion)

  /**
    * The compile-time options used to compile the used library.
    *
    * @return a string with all compile-time options delimited by a space
    * @see <a href="http://www.sqlite.org/c3ref/compileoption_get.html">sqlite3_compileoption_get</a>
    */
  lazy val compileOptions: String = _getCompileOptions()

  private def _getCompileOptions(): String = {
    val sb = new StringBuilder()

    var i = 0
    while (true) {
      val option = sqlite.sqlite3_compileoption_get(i)

      if (option == null) return sb.toString
      if (sb.nonEmpty) sb.append(' ')

      sb.append(option)

      i += 1
    }

    sb.toString
  }

  /** SQLite4Java backward compatibility */
  def getSQLiteCompileOptions(): String = compileOptions

  /**
    * The numeric representation of the SQLite version.
    *
    * @return a number representing the version; example: version "3.6.23.1" is represented as 3006023.
    * @see <a href="http://www.sqlite.org/c3ref/libversion.html">sqlite3_version</a>
    */
  def versionNumber: CInt = sqlite.sqlite3_libversion_number()

  /** SQLite4Java backward compatibility */
  def getSQLiteVersionNumber(): Int = versionNumber

  /**
    * Checks if SQLite has been compiled with the THREADSAFE option.
    *
    * @return true if SQLite has been compiled with THREADSAFE option
    * @see <a href="http://www.sqlite.org/c3ref/threadsafe.html">sqlite3_threadsafe</a>
    */
  lazy val isThreadSafe: Boolean = sqlite.sqlite3_threadsafe() != 0

  /**
    * Checks if the given SQL is complete.
    *
    * @param sql the SQL
    * @return true if sql is a complete statement
    * @throws SQLiteException if native library cannot be loaded
    * @see <a href="http://www.sqlite.org/c3ref/complete.html">sqlite3_complete</a>
    */
  def isComplete(sql: String): Boolean = {
    Zone { implicit z =>
      sqlite.sqlite3_complete(CUtils.toCString(sql)) != 0
    }
  }

  /**
    * Gets the amount of memory currently used by SQLite library. The returned value shows the amount of non-heap
    * "native" memory taken up by SQLite caches and anything else allocated with sqlite3_malloc.
    * <p/>
    * This value does not include any heap or other JVM-allocated memory taken up by sqlite4java objects and classes.
    *
    * @return the number of bytes used by SQLite library in this process (for all connections)
    * @see <a href="http://www.sqlite.org/c3ref/memory_highwater.html">sqlite3_memory_used</a>
    */
  def getMemoryUsed(): Long = sqlite.sqlite3_memory_used()

  /**
    * Returns the maximum amount of memory that was used by SQLite since the last time the highwater
    * was reset.
    *
    * @param reset if true, the highwatermark is reset after this call
    * @return the maximum number of bytes ever used by SQLite library since the start of the application
    *         or the last reset of the highwatermark.
    * @see <a href="http://www.sqlite.org/c3ref/memory_highwater.html">sqlite3_memory_highwater</a>
    */
  def getMemoryHighwater(reset: Boolean): Long = {
    sqlite.sqlite3_memory_highwater(if (reset) 1 else 0)
  }

  /**
    * Requests SQLite to try to release some memory from its heap. This could be called to clear cache.
    *
    * @param bytes the number of bytes requested to be released
    * @return the number of bytes actually released
    * @see <a href="http://www.sqlite.org/c3ref/release_memory.html">sqlite3_release_memory</a>
    */
  def releaseMemory(bytes: Int): Int = {
    sqlite.sqlite3_release_memory(bytes)
  }

  /**
    * Sets the "soft limit" on the amount of memory allocated before SQLite starts trying to free some
    * memory before allocating more memory.
    *
    * @param limit the number of bytes to set the soft memory limit to
    * @see <a href="http://www.sqlite.org/c3ref/soft_heap_limit.html">sqlite3_soft_heap_limit</a>
    */
  def setSoftHeapLimit(limit: Int): Unit = {
    sqlite.sqlite3_soft_heap_limit64(limit)
  }

  /**
    * Sets the "soft limit" on the amount of memory allocated before SQLite starts trying to free some
    * memory before allocating more memory.
    *
    * @param limit the number of bytes to set the soft memory limit to
    * @return size of the soft heap limit prior to the call
    * @see <a href="http://www.sqlite.org/c3ref/soft_heap_limit64.html">sqlite3_soft_heap_limit64</a>
    */
  def softHeapLimit(limit: Long): Long = {
    sqlite.sqlite3_soft_heap_limit64(limit)
  }

  /**
    * Sets whether <a href="http://www.sqlite.org/sharedcache.html">shared cache mode</a> will be used
    * for the connections that are opened after this call. All existing connections are not affected.
    * <p/>
    * <strong>sqlite4java</strong> explicitly disables shared cache on start. This is also the default declared by SQLite,
    * but it may change in the future, so <strong>sqlite4java</strong> enforces consistency.
    *
    * @param enabled if true, the following calls to { @link SQLiteConnection#open} will used shared-cache mode
    * @see <a href="http://www.sqlite.org/c3ref/enable_shared_cache.html">sqlite3_enable_shared_cache</a>
    */
  def setSharedCache(enabled: Boolean): Unit = {
    val rc = sqlite.sqlite3_enable_shared_cache(if (enabled) 1 else 0)
    if (rc != SQLITE_OK) throw new SQLiteException(rc, s"SQLite: cannot set shared_cache to $enabled")
  }

  /**
    * <strong>Only for Windows.</strong> Set the value associated with the
    * <a href="https://www.sqlite.org/c3ref/temp_directory.html">sqlite3_temp_directory</a> or
    * <a href="https://www.sqlite.org/c3ref/data_directory.html">sqlite3_data_directory</a> variables to
    * a path depending on the value of the directoryType parameter.
    *
    * @param directoryType Indicator of which variable to set. Can either be SQLITE_WIN32_DATA_DIRECTORY_TYPE
    *                      or SQLITE_WIN32_TEMP_DIRECTORY_TYPE.
    * @param path          The directory name to set one of the two variables to. If path is null, then the previous
    *                      value will be freed from memory and no longer effective.
    * @see <a href="https://www.sqlite.org/c3ref/win32_set_directory.html">sqlite3_win32_set_directory</a>
    */
  def setDirectory(directoryType: Int, path: String): Unit = {
    assert(Platform.isWindows, "setDirectory() is a windows specific method")

    val rc = Zone { implicit z =>
      sqlite_addons.sqlite3_win32_set_directory(directoryType, CUtils.toCString(path))
    }

    if (rc != SQLITE_OK) {
      val errorMessage = if (rc == SQLITE_NOMEM) "Memory could not be allocated"
      else "Error attempting to set win32 directory"

      throw new SQLiteException(rc, errorMessage)
    }
  }

}
