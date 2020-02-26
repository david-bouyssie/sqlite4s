/*
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

package com.github.sqlite4s.bindings

import scala.scalanative.unsafe._

import sqlite.SQLITE_CONSTANT._

@link("sqlite3")
@extern
object sqlite_addons {

  object SQLITE_CONSTANT {
    val SQLITE_WIN32_DATA_DIRECTORY_TYPE = 1
    val SQLITE_WIN32_TEMP_DIRECTORY_TYPE = 2

    val SQLITE_STATIC: Long = 0L
    val SQLITE_TRANSIENT: Long = -1L
  }

  type sqlite3_destructor_type = sqlite.sqlite3_destructor_type //CFuncPtr1[Ptr[CSignedChar], Unit]
  object DESTRUCTOR_TYPE {

    @inline def castToDestructorType(dVal: Long): sqlite3_destructor_type = {
      scala.scalanative.runtime.Boxes.boxToCFuncRawPtr(
        scala.scalanative.runtime.Intrinsics.castLongToRawPtr(dVal)
      ).asInstanceOf[sqlite3_destructor_type]
    }

    val SQLITE_STATIC: sqlite3_destructor_type = castToDestructorType(SQLITE_CONSTANT.SQLITE_STATIC)
    val SQLITE_TRANSIENT: sqlite3_destructor_type = castToDestructorType(SQLITE_CONSTANT.SQLITE_TRANSIENT)
  }

  def sqlite3_win32_set_directory(dir_type: CInt, zValue: Ptr[Byte]): CInt = extern
  def sqlite3_win32_set_directory8(dir_type: CInt, zValue: Ptr[CChar]): CInt = extern
  def sqlite3_win32_set_directory16(dir_type: CInt, zValue: Ptr[Byte]): CInt = extern
}

/**
  ** In its default configuration, SQLite API routines return one of 30 integer
  ** [result codes].  However, experience has shown that many of
  ** these result codes are too coarse-grained.  They do not provide as
  ** much information about problems as programmers might like.  In an effort to
  ** address this, newer versions of SQLite (version 3.3.8 [dateof:3.3.8]
  ** and later) include support for additional result codes that provide more detailed information
  ** about errors. These [extended result codes] are enabled or disabled
  ** on a per database connection basis using the [sqlite3_extended_result_codes()] API.
  ** Or, the extended code for the most recent error can be obtained using [sqlite3_extended_errcode()].
  */
object SQLITE_EXTENDED_RESULT_CODE {

  val SQLITE_ERROR_MISSING_COLLSEQ: Int = SQLITE_ERROR | (1 << 8)
  val SQLITE_ERROR_RETRY: Int = SQLITE_ERROR | (2 << 8)
  val SQLITE_IOERR_READ: Int = SQLITE_IOERR | (1 << 8)
  val SQLITE_IOERR_SHORT_READ: Int = SQLITE_IOERR | (2 << 8)
  val SQLITE_IOERR_WRITE: Int = SQLITE_IOERR | (3 << 8)
  val SQLITE_IOERR_FSYNC: Int = SQLITE_IOERR | (4 << 8)
  val SQLITE_IOERR_DIR_FSYNC: Int = SQLITE_IOERR | (5 << 8)
  val SQLITE_IOERR_TRUNCATE: Int = SQLITE_IOERR | (6 << 8)
  val SQLITE_IOERR_FSTAT: Int = SQLITE_IOERR | (7 << 8)
  val SQLITE_IOERR_UNLOCK: Int = SQLITE_IOERR | (8 << 8)
  val SQLITE_IOERR_RDLOCK: Int = SQLITE_IOERR | (9 << 8)
  val SQLITE_IOERR_DELETE: Int = SQLITE_IOERR | (10 << 8)
  val SQLITE_IOERR_BLOCKED: Int = SQLITE_IOERR | (11 << 8)
  val SQLITE_IOERR_NOMEM: Int = SQLITE_IOERR | (12 << 8)
  val SQLITE_IOERR_ACCESS: Int = SQLITE_IOERR | (13 << 8)
  val SQLITE_IOERR_CHECKRESERVEDLOCK: Int = SQLITE_IOERR | (14 << 8)
  val SQLITE_IOERR_LOCK: Int = SQLITE_IOERR | (15 << 8)
  val SQLITE_IOERR_CLOSE: Int = SQLITE_IOERR | (16 << 8)
  val SQLITE_IOERR_DIR_CLOSE: Int = SQLITE_IOERR | (17 << 8)
  val SQLITE_IOERR_SHMOPEN: Int = SQLITE_IOERR | (18 << 8)
  val SQLITE_IOERR_SHMSIZE: Int = SQLITE_IOERR | (19 << 8)
  val SQLITE_IOERR_SHMLOCK: Int = SQLITE_IOERR | (20 << 8)
  val SQLITE_IOERR_SHMMAP: Int = SQLITE_IOERR | (21 << 8)
  val SQLITE_IOERR_SEEK: Int = SQLITE_IOERR | (22 << 8)
  val SQLITE_IOERR_DELETE_NOENT: Int = SQLITE_IOERR | (23 << 8)
  val SQLITE_IOERR_MMAP: Int = SQLITE_IOERR | (24 << 8)
  val SQLITE_IOERR_GETTEMPPATH: Int = SQLITE_IOERR | (25 << 8)
  val SQLITE_IOERR_CONVPATH: Int = SQLITE_IOERR | (26 << 8)
  val SQLITE_IOERR_VNODE: Int = SQLITE_IOERR | (27 << 8)
  val SQLITE_IOERR_AUTH: Int = SQLITE_IOERR | (28 << 8)
  val SQLITE_IOERR_BEGIN_ATOMIC: Int = SQLITE_IOERR | (29 << 8)
  val SQLITE_IOERR_COMMIT_ATOMIC: Int = SQLITE_IOERR | (30 << 8)
  val SQLITE_IOERR_ROLLBACK_ATOMIC: Int = SQLITE_IOERR | (31 << 8)
  val SQLITE_LOCKED_SHAREDCACHE: Int = SQLITE_LOCKED | (1 << 8)
  val SQLITE_LOCKED_VTAB: Int = SQLITE_LOCKED | (2 << 8)
  val SQLITE_BUSY_RECOVERY: Int = SQLITE_BUSY | (1 << 8)
  val SQLITE_BUSY_SNAPSHOT: Int = SQLITE_BUSY | (2 << 8)
  val SQLITE_CANTOPEN_NOTEMPDIR: Int = SQLITE_CANTOPEN | (1 << 8)
  val SQLITE_CANTOPEN_ISDIR: Int = SQLITE_CANTOPEN | (2 << 8)
  val SQLITE_CANTOPEN_FULLPATH: Int = SQLITE_CANTOPEN | (3 << 8)
  val SQLITE_CANTOPEN_CONVPATH: Int = SQLITE_CANTOPEN | (4 << 8)
  val SQLITE_CORRUPT_VTAB: Int = SQLITE_CORRUPT | (1 << 8)
  val SQLITE_CORRUPT_SEQUENCE: Int = SQLITE_CORRUPT | (2 << 8)
  val SQLITE_READONLY_RECOVERY: Int = SQLITE_READONLY | (1 << 8)
  val SQLITE_READONLY_CANTLOCK: Int = SQLITE_READONLY | (2 << 8)
  val SQLITE_READONLY_ROLLBACK: Int = SQLITE_READONLY | (3 << 8)
  val SQLITE_READONLY_DBMOVED: Int = SQLITE_READONLY | (4 << 8)
  val SQLITE_READONLY_CANTINIT: Int = SQLITE_READONLY | (5 << 8)
  val SQLITE_READONLY_DIRECTORY: Int = SQLITE_READONLY | (6 << 8)
  val SQLITE_ABORT_ROLLBACK: Int = SQLITE_ABORT | (2 << 8)
  val SQLITE_CONSTRAINT_CHECK: Int = SQLITE_CONSTRAINT | (1 << 8)
  val SQLITE_CONSTRAINT_COMMITHOOK: Int = SQLITE_CONSTRAINT | (2 << 8)
  val SQLITE_CONSTRAINT_FOREIGNKEY: Int = SQLITE_CONSTRAINT | (3 << 8)
  val SQLITE_CONSTRAINT_FUNCTION: Int = SQLITE_CONSTRAINT | (4 << 8)
  val SQLITE_CONSTRAINT_NOTNULL: Int = SQLITE_CONSTRAINT | (5 << 8)
  val SQLITE_CONSTRAINT_PRIMARYKEY: Int = SQLITE_CONSTRAINT | (6 << 8)
  val SQLITE_CONSTRAINT_TRIGGER: Int = SQLITE_CONSTRAINT | (7 << 8)
  val SQLITE_CONSTRAINT_UNIQUE: Int = SQLITE_CONSTRAINT | (8 << 8)
  val SQLITE_CONSTRAINT_VTAB: Int = SQLITE_CONSTRAINT | (9 << 8)
  val SQLITE_CONSTRAINT_ROWID: Int = SQLITE_CONSTRAINT | (10 << 8)
  val SQLITE_NOTICE_RECOVER_WAL: Int = SQLITE_NOTICE | (1 << 8)
  val SQLITE_NOTICE_RECOVER_ROLLBACK: Int = SQLITE_NOTICE | (2 << 8)
  val SQLITE_WARNING_AUTOINDEX: Int = SQLITE_WARNING | (1 << 8)
  val SQLITE_AUTH_USER: Int = SQLITE_AUTH | (1 << 8)
  val SQLITE_OK_LOAD_PERMANENTLY: Int = SQLITE_OK | (1 << 8)
}
