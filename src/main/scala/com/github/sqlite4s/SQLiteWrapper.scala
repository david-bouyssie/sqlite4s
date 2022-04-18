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

import java.nio.charset
import scala.scalanative.runtime.ByteArray
import scala.scalanative.unsafe._
import scala.scalanative.unsafe.Tag.CFuncPtr1
import scala.scalanative.unsigned._
import bindings.sqlite
import bindings.SQLITE_CONSTANT._
import bindings.DESTRUCTOR_TYPE._
import bindings.sqlite_addons.sqlite3_destructor_type
import com.github.sqlite4s.SQLITE_WRAPPER_ERROR_CODE._
import com.github.sqlite4s.c.util.{CUtils, PtrBox}


/*object Bytes2Hex {
  private val hexArray = "0123456789ABCDEF".toCharArray

  def convert(bytes: Array[Byte]): String = {
    val hexChars = new Array[Char](bytes.length * 2)

    var j = 0
    while (j < bytes.length) {
      val v = bytes(j) & 0xFF
      hexChars(j * 2) = hexArray(v >>> 4)
      hexChars(j * 2 + 1) = hexArray(v & 0x0F)

      j += 1
    }

    new String(hexChars)
  }
}*/

object SQLiteWrapper {

  //private val EMPTY_STRING: String = ""
  private val EMPTY_CSTRING: CString = c""

  //private val DEFAULT_CHARSET = charset.Charset.defaultCharset()
  private val UTF8_CHARSET = charset.StandardCharsets.UTF_8
  private val UTF16_CHARSET = charset.StandardCharsets.UTF_16

  private val WRAPPER_VERSION = "1.3"

  @inline
  private def _getDbErrCode(db: SQLiteConnection.Handle): Int = {
    if (db == null) WRAPPER_WEIRD else sqlite.sqlite3_errcode(db)
  }

  @inline
  private def _getStmtErrCode(stmt: Ptr[sqlite.sqlite3_stmt]): Int = {
    if (stmt == null) return WRAPPER_WEIRD

    val db = sqlite.sqlite3_db_handle(stmt)
    if (db == null) WRAPPER_WEIRD else sqlite.sqlite3_errcode(db)
  }

  def wrapper_version: String = WRAPPER_VERSION

  def apply(): SQLiteWrapper = new SQLiteWrapper()

  /**
    * High-level wrapper of sqlite3_exec()
    *
    * @param db       handle
    * @param sql      sql statements
    * @param throwErrors enable to throw an Exception on error
    * @return result code
    */
  def sqlite3Exec(db: SQLiteConnection.Handle, sql: String, throwErrors: Boolean = false): (Int, Option[String]) = {
    require(db != null, "db is null")
    require(sql != null, "sql is null")
    //require(outError == null || outError.length == 1, s"invalid outError length: ${outError.length}")

    val errMsgPtr: Ptr[CString] = stackalloc[CString]()

    val rc = Zone { implicit z: Zone =>

      // Note: we use the Charset "UTF-8" to get sql in correct UTF-8
      val utf8Sql = CUtils.toCString(sql, UTF8_CHARSET)

      sqlite.sqlite3_exec(
        db,
        utf8Sql,
        null, /* Callback function */
        null, /* 1st argument to callback */
        errMsgPtr
      )
    }

    if (rc != SQLITE_OK) {
      /*if (errMsgPtr != null) {

        //if (outError != null) { // warning! can fail with exception here if bad array is passed
        //  outError(0) = fromCString(!errMsgPtr)
        //}
        //sqlite.sqlite3_free(!errMsgPtr)
      }*/

      val errMsg = !errMsgPtr
      val error = if (errMsg == null) fromCString(sqlite.sqlite3_errstr(rc))
      else {
        // Note: the application should invoke sqlite3_free() on error message strings returned
        // through the 5th parameter of sqlite3_exec() after the error message string is no longer needed.
        // If the 5th parameter to sqlite3_exec() is not NULL and no errors occur,
        // then sqlite3_exec() sets the pointer in its 5th parameter to NULL before returning.
        val e = fromCString(errMsg)
        sqlite.sqlite3_free(errMsg)
        e
      }

      if (!throwErrors) return (rc, Some(error))
      else {
        throw new SQLiteException(
          rc,
          s"failed calling sqlite3_exec(): $error"
        )
      }
    }

    (rc, None)
  }

  /**
    * High-level wrapper of sqlite3_table_column_metadata()
    *
    * int sqlite3_table_column_metadata(
    * sqlite3 *db,                /* Connection handle */
    * const char *zDbName,        /* Database name or NULL */
    * const char *zTableName,     /* Table name */
    * const char *zColumnName,    /* Column name */
    * char const **pzDataType,    /* OUTPUT: Declared data type */
    * char const **pzCollSeq,     /* OUTPUT: Collation sequence name */
    * int *pNotNull,              /* OUTPUT: True if NOT NULL constraint exists */
    * int *pPrimaryKey,           /* OUTPUT: True if column part of PK */
    * int *pAutoinc               /* OUTPUT: True if column is auto-increment */
    * );
    *
    * @param db handle
    * @param dbName database name
    * @param tableName table name
    * @param columnName column name
    */
  @throws[SQLiteException]
  def sqlite3TableColumnMetadata(
    db: SQLiteConnection.Handle,
    dbName: String, // @Nullable TODO: DBO => find a way to annotate this
    tableName: String,
    columnName: String  // @Nullable TODO: DBO => find a way to annotate this
  ): SQLiteColumnMetadata = {
    require(db != null, "db is null")
    require(tableName != null, "tableName is null")

    val pzDataType: Ptr[CString] = stackalloc[CString]()
    val pzCollSeq: Ptr[CString] = stackalloc[CString]()
    val pNotNull: Ptr[CInt] = stackalloc[CInt]()
    val pPrimaryKey: Ptr[CInt] = stackalloc[CInt]()
    val pAutoinc: Ptr[CInt] = stackalloc[CInt]()

    var rc = Zone { implicit z: Zone =>
      sqlite.sqlite3_table_column_metadata(
        db,
        if (dbName == null) null else CUtils.toCString(dbName),
        CUtils.toCString(tableName),
        if (columnName == null) null else CUtils.toCString(columnName),
        pzDataType, pzCollSeq, pNotNull, pPrimaryKey, pAutoinc
      )
    }

    // Check returned values
    if (!pzDataType == null || !pzCollSeq == null) {
      rc = this._getDbErrCode(db)
    }

    if (rc != SQLITE_OK) {

      val errmsg = if (db == null) null else sqlite.sqlite3_errmsg(db)

      val error = if (errmsg == null) fromCString(sqlite.sqlite3_errstr(rc))
      else fromCString(errmsg)

      throw new SQLiteException(
        rc,
        s"failed calling sqlite3_table_column_metadata(): $error"
      )
    }

    SQLiteColumnMetadata(fromCString(!pzDataType), fromCString(!pzCollSeq), !pNotNull == 1, !pPrimaryKey == 1, !pAutoinc == 1)
  }


  /*
  // This should be avoided because CString can be anything
  def sqlite3BindCString(stmt: SQLiteStatement.Handle, index: Int, value: CString, len: Int): Int = {
    require(stmt != null, "db is null")
    require(value != null, "value is null")

    var destructor = SQLITE_STATIC
    val strNulTermOffset = len + 1

    // FIXME: we need to provide a proper destructor here (SQLITE_STATIC)
    sqlite.sqlite3_bind_text(stmt, index, value, strNulTermOffset, null)
  }*/

  def sqlite3BindStaticText(stmt: SQLiteStatement.Handle, index: Int, value: String)(implicit z: Zone): Int = {
    _sqlite3BindText(stmt, index, value, SQLITE_STATIC)
  }

  def sqlite3BindText(stmt: SQLiteStatement.Handle, index: Int, value: String): Int = {
    Zone { implicit z =>
      _sqlite3BindText(stmt, index, value, SQLITE_TRANSIENT)
    }
  }

  @inline private def _sqlite3BindText(
    stmt: SQLiteStatement.Handle,
    index: Int,
    value: String,
    destructor: sqlite3_destructor_type
  )(implicit z: Zone): Int = {

    require(stmt != null, "db is null")
    require(value != null, "value is null")

    val valueLen: CInt = value.length // + 1
    var effectiveDestructor = destructor

    var utf8Str: CString = null
    var utf8StrNulTermOffset: CInt = -1

    if (valueLen > 0) {
      /*val encodedCharsAsBytes = value.getBytes(UTF8_CHARSET)
      // TODO: try to understand why the doc explains to add nul-terminator for prepare call and not for bind call
      utf8StrNulTermOffset = encodedCharsAsBytes.length + 1
      //println("utf16StrNulTermOffset: " + utf16StrNulTermOffset)

      val encodedCharsAsBytes = value.toCharArray.map(_.toByte)
      utf8StrNulTermOffset = encodedCharsAsBytes.length + 1
      !utf8StrPtr = CUtils.bytesToCString(encodedCharsAsBytes)*/

      val encodedCharsAsBytes = value.getBytes(UTF16_CHARSET)
      utf8StrNulTermOffset = encodedCharsAsBytes.length
      utf8Str = CUtils.bytesToCString(encodedCharsAsBytes)

    } else {
      effectiveDestructor = SQLITE_STATIC
      //!utf8StrPtr = EMPTY_CSTRING
      utf8Str = EMPTY_CSTRING
      utf8StrNulTermOffset = 0
    }

    //println("valueLen: " + valueLen)
    //println("utf16ValueLen: " + utf16ValueLen)

    sqlite.sqlite3_bind_text16(stmt, index, utf8Str, utf8StrNulTermOffset, effectiveDestructor)
    //sqlite.sqlite3_bind_text64(stmt, index, !utf16StrPtr, utf16StrNulTermOffset.toULong, null, SQLITE_UTF8.toUByte)
  }

  def sqlite3BindBlob(
    stmt: SQLiteStatement.Handle,
    index: Int,
    value: Array[Byte],
    offset: Int,
    length: Int
  ): Int = {
    sqlite3_bind_blob(stmt, index, value.asInstanceOf[ByteArray].at(0), value.length, offset, length, SQLITE_TRANSIENT)
  }

  def sqlite3_bind_blob(
    stmt: SQLiteStatement.Handle,
    index: CInt,
    value: Ptr[Byte],
    valueLength: CInt,
    offset: CInt,
    length: CInt,
    destructor: sqlite3_destructor_type
  ): Int = {
    require(stmt != null, "stmt is null")
    require(value != null, "value is null")
    require(valueLength >= 0, "valueLength is invalid, it should not be negative")
    require(offset >= 0, "offset is invalid, it should not be negative")
    require(length >= 0, "length is invalid, it should not be negative")
    require(offset <= valueLength, "offset is invalid, it should not be greater than valueLength")
    require(offset + length <= valueLength, "offset+length sum is invalid, it should not be greater than valueLength")

    val rc = if (valueLength == 0) sqlite.sqlite3_bind_zeroblob(stmt, index, 0)
    else {
      sqlite.sqlite3_bind_blob(stmt, index, value + offset, length, destructor)
    }

    rc
  }

  def sqlite3BlobRead(
    blob: SQLiteBlob.Handle,
    blobOffset: Int,
    buffer: Array[Byte],
    bufferOffset: Int,
    length: Int
  ): Int = {
    this.sqlite3_blob_read(blob, blobOffset, buffer.asInstanceOf[ByteArray].at(0), buffer.length, bufferOffset, length)
  }

  def sqlite3_blob_read(
    blob: SQLiteBlob.Handle,
    blobOffset: CInt,
    buffer: Ptr[Byte],
    bufferLength: CInt,
    bufferOffset: CInt,
    length: CInt
  ): Int = {
    if (length == 0) return SQLITE_OK

    this._sqlite3CheckArgsForBlobReadOrWrite(blob, blobOffset, buffer, bufferLength, bufferOffset, length)

    sqlite.sqlite3_blob_read(blob, buffer + bufferOffset, length, blobOffset)
  }

  def sqlite3BlobWrite(
    blob: SQLiteBlob.Handle,
    blobOffset: Int,
    buffer: Array[Byte],
    bufferOffset: Int,
    length: Int
  ): Int = {
    this.sqlite3_blob_write(blob, blobOffset, buffer.asInstanceOf[ByteArray].at(0), buffer.length, bufferOffset, length)
  }

  def sqlite3_blob_write(
    blob: SQLiteBlob.Handle,
    blobOffset: CInt,
    buffer: Ptr[Byte],
    bufferLength: CInt,
    bufferOffset: CInt,
    length: CInt
  ): Int = {
    if (length == 0) return SQLITE_OK

    this._sqlite3CheckArgsForBlobReadOrWrite(blob, blobOffset, buffer, bufferLength, bufferOffset, length)

    sqlite.sqlite3_blob_write(blob, buffer + bufferOffset, length, blobOffset)
  }

  @inline
  private def _sqlite3CheckArgsForBlobReadOrWrite(
    blob: SQLiteBlob.Handle,
    blobOffset: CInt,
    buffer: Ptr[Byte],
    bufferLength: CInt,
    bufferOffset: CInt,
    length: CInt
  ): Unit = {
    require(blob != null, "blob is null")
    require(blobOffset >= 0, "blobOffset is invalid, it should not be negative")
    require(buffer != null, "blob is null")
    require(bufferOffset >= 0, "bufferOffset is invalid, it should not be negative")
    require(length >= 0, "length is invalid, it should not be negative")
    require(bufferOffset <= bufferLength, "offset is invalid, it should not be greater than bufferLength")
    require(bufferOffset + length <= bufferLength, "offset+length sum is invalid, it should not be greater than valueLength")
  }

  /*val progress_handler_cb: CFuncPtr1[Ptr[Byte], CInt] = new CFuncPtr1[Ptr[Byte], CInt] {
    def apply(ptr: Ptr[Byte]): CInt = {
        if (ptr == null) return 1

        val longPtr = ptr.asInstanceOf[Ptr[CLong]]

        longPtr.update(1,longPtr(1) + 1L)
        if (longPtr(0) != 0) return -1

        0
      }
    }
   */

  val progress_handler_cb = CFuncPtr1.fromScalaFunction((ptr: Ptr[Byte]) => {
    if (ptr == null) 1
    else {
      val longPtr = ptr.asInstanceOf[Ptr[CLong]]

      longPtr.update(1,longPtr(1) + 1L)
      if (longPtr(0) != 0) -1 else 0
    }
  })

  /*def progress_handler_cb(ptr: Ptr[Byte]): CInt = {
    if (ptr == null) return 1

    val longPtr = ptr.asInstanceOf[Ptr[CLong]]

    longPtr.update(1,longPtr(1) + 1L)
    if (longPtr(0) != 0) return -1

    0
  }*/

  /*
  int progress_handler(void *ptr) {
    jlong* lptr = 0;

    if (!ptr) return 1;
    lptr = *(jlong**)&ptr;

    lptr[1]++;
    if (lptr[0] != 0) return -1;

    return 0;
  }
   */

}

// FIXME: de we need to make these methods synchronized?
class SQLiteWrapper {

  /**
    * Last return code received for non-static methods.
    */
  // TODO: do we really need this, since we always have direct access to SQLite API???
  private var myLastReturnCode: CInt = 0
  private var myLastOpenError: String = _

  def getLastReturnCode(): Int = myLastReturnCode

  def drainLastOpenError(): String = {
    val r = myLastOpenError
    myLastOpenError = null
    r
  }

  /**
    * High-level wrapper of sqlite3_open_v2
    *
    * @param filename database file name, not null
    * @param flags see SQLITE_OPEN_* constants
    * @param throwErrors enable to throw an Exception on error
    * @return return code SQLITE_OK or other
    */
  def sqlite3OpenV2(filename: String, flags: Int, throwErrors: Boolean = false): SQLiteConnection.Handle = {
    require(filename != null, "filename is null")

    myLastReturnCode = 0

    val dbPtr = stackalloc[Ptr[sqlite.sqlite3]]()

    val rc = Zone { implicit z: Zone =>
      // Note: we use the Charset "UTF-8" to get filename in correct UTF-8
      val utf8Str = CUtils.toCString(filename, SQLiteWrapper.UTF8_CHARSET)(z)
      sqlite.sqlite3_open_v2(utf8Str, dbPtr, flags, null)
    }
    myLastReturnCode = rc

    if (rc != SQLITE_OK) {

      var db = !dbPtr
      val errmsg = if (db != null) { // on error, open returns db anyway
        val e = sqlite.sqlite3_errmsg(db)

        sqlite.sqlite3_close(db)
        db = null

        e
      } else null

      val error = if (errmsg == null) fromCString(sqlite.sqlite3_errstr(rc))
      else fromCString(errmsg)

      myLastOpenError = error

      if (!throwErrors) return null
      else throw new SQLiteException(rc, s"failed calling sqlite3_open_v2(): $error")
    }

    assert(dbPtr != null, "dbPtr is null")

    (!dbPtr).asInstanceOf[SQLiteConnection.Handle]
  }

  def sqlite3PrepareV2(db: SQLiteConnection.Handle, sql: String, throwErrors: Boolean = false): SQLiteStatement.Handle = {
    _sqlite3PrepareV2OrV3(db, sql, None, throwErrors)
  }

  /*def sqlite3_prepare_v2(jenv: Nothing, jcls: Nothing, jdb: Nothing, jsql: Nothing, jresult: Nothing): Nothing = {
    var db = 0
    var sql = 0
    var stmt = 0
    var rc = 0
    val r = 0
    var length = 0
    if (!jdb) return WRAPPER_INVALID_ARG_1
    if (!jsql) return WRAPPER_INVALID_ARG_2
    if (!jresult) return WRAPPER_INVALID_ARG_3
    db = jdb.asInstanceOf[Nothing]
    length = jenv.GetStringLength(jenv, jsql) * sizeof(jchar)
    sql = jenv.GetStringCritical(jenv, jsql, 0)
    if (!sql) return WRAPPER_CANNOT_TRANSFORM_STRING
    stmt = 0.asInstanceOf[Nothing]
    rc = sqlite3_prepare16_v2(db, sql, length, stmt, 0)
    jenv.ReleaseStringCritical(jenv, jsql, sql)
    /*if (stmt) {
        *((sqlite3_stmt**)&r) = stmt;
        jenv.SetLongArrayRegion(jenv, jresult, 0, 1, &r);
      }*/ rc
  }*/

  def sqlite3PrepareV3(db: SQLiteConnection.Handle, sql: String, prepFlags: Int, throwErrors: Boolean = false): SQLiteStatement.Handle = {
    val prepFlagsAsUInt = prepFlags.toUInt
    _sqlite3PrepareV2OrV3(db, sql, Some(prepFlagsAsUInt), throwErrors)
  }

  /*def sqlite3_prepare_v3(jenv: Nothing, jcls: Nothing, jdb: Nothing, jsql: Nothing, jprepFlags: Nothing, jresult: Nothing): Nothing = {
    var db = 0
    var sql = 0
    var stmt = 0
    var rc = 0
    val r = 0
    var length = 0
    val prepFlags = jprepFlags.asInstanceOf[Nothing]

    if (!jdb) return WRAPPER_INVALID_ARG_1
    if (!jsql) return WRAPPER_INVALID_ARG_2
    if (!jresult) return WRAPPER_INVALID_ARG_3
    db = jdb.asInstanceOf[Nothing]
    length = jenv.GetStringLength(jenv, jsql) * sizeof(jchar)
    sql = jenv.GetStringCritical(jenv, jsql, 0)
    if (!sql) return WRAPPER_CANNOT_TRANSFORM_STRING
    stmt = 0.asInstanceOf[Nothing]
    rc = sqlite3_prepare16_v3(db, sql, length, prepFlags, stmt, 0)
    jenv.ReleaseStringCritical(jenv, jsql, sql)
    /*if (stmt) {
          *((sqlite3_stmt**)&r) = stmt;
          jenv.SetLongArrayRegion(jenv, jresult, 0, 1, &r);
        }*/ rc
  }*/

  @inline
  private def _sqlite3PrepareV2OrV3(
    db: SQLiteConnection.Handle,
    sql: String,
    prepFlagsOpt: Option[CUnsignedInt] = None,
    throwErrors: Boolean = false
  ): SQLiteStatement.Handle = {
    require(db != null, "db is null")
    require(sql != null, "sql is null")

    myLastReturnCode = 0

    val stmtPtr = stackalloc[Ptr[sqlite.sqlite3_stmt]]()

    val rc = Zone { implicit z: Zone =>
      // Note (https://sqlite.org/capi3ref.html#sqlite3_prepare):
      // If the caller knows that the supplied string is nul-terminated,
      // then there is a small performance advantage to passing an nByte parameter
      // that is the number of bytes in the input string including the nul-terminator.
      val utf16Bytes = sql.getBytes(SQLiteWrapper.UTF16_CHARSET)
      val utf16Sql = CUtils.bytesToCString(utf16Bytes)(z)
      // FIXME: adding a nul-terminator offset seems to lead to invalid string => check why
      val utf16SqlLen = utf16Bytes.length //+ 1 // add nul-terminator to length (see above)

      //println("utf16Bytes: " + Bytes2Hex.convert(fixedUtf16Bytes))
      //println("fromCString:" + fromCString(utf16Sql))

      if (prepFlagsOpt.isEmpty) {
        sqlite.sqlite3_prepare16_v2(db, utf16Sql, utf16SqlLen, stmtPtr, null)
      } else {
        sqlite.sqlite3_prepare16_v3(db, utf16Sql, utf16SqlLen, prepFlagsOpt.get, stmtPtr, null)
      }
    }

    myLastReturnCode = rc

    if (rc != SQLITE_OK) {
      if (!throwErrors) return null
      else {
        assert(db != null, "weird, db is null")

        val errmsg = sqlite.sqlite3_errmsg(db)

        // Finalize statement if not null
        val stmt = !stmtPtr
        if (stmt != null) {
          sqlite.sqlite3_finalize(stmt)
        }

        val error = fromCString(errmsg)
        val v = if (prepFlagsOpt.isEmpty) 2 else 3
        throw new SQLiteException(rc, s"failed calling sqlite3_prepare_v$v(): $error")
      }
    }

    assert(stmtPtr.unary_! != null, "weird, stmt is null")

    (!stmtPtr).asInstanceOf[SQLiteStatement.Handle]
  }

  def sqlite3ColumnText(statement: SQLiteStatement.Handle, column: Int): String = {
    require(statement != null, "stmt is null")

    val stmt = statement.asPtr()

    myLastReturnCode = 0

    //val text = sqlite.sqlite3_column_text16(stmt, column)
    val text = sqlite.sqlite3_column_text(stmt, column).asInstanceOf[Ptr[CChar]]
    var textAsStr: String = null

    val rc = if (text == null) { // maybe we're out of memory
      val err = SQLiteWrapper._getStmtErrCode(stmt)
      if (err == SQLITE_NOMEM) err else SQLITE_OK
    }
    else {
      /*val length = sqlite.sqlite3_column_bytes16(stmt, column)
      if (length < 0) WRAPPER_WEIRD_2

      textAsStr = fromCString(text, UTF16_CHARSET)*/

      val length = sqlite.sqlite3_column_bytes(stmt, column)
      if (length < 0) WRAPPER_WEIRD_2

      textAsStr = fromCString(text, SQLiteWrapper.UTF8_CHARSET)

      SQLITE_OK
    }

    myLastReturnCode = rc

    textAsStr
  }

  /*
  def sqlite3_column_text(jenv: Nothing, jcls: Nothing, jstmt: Nothing, jcolumn: Nothing, joutValue: Nothing): Nothing = {
    val stmt = jstmt.asInstanceOf[Nothing]
    var text = 0
    var result = 0
    var db = 0
    var err = 0
    var length = 0
    if (!stmt) return WRAPPER_INVALID_ARG_1
    if (!joutValue) return WRAPPER_INVALID_ARG_3
    text = sqlite3_column_text16(stmt, jcolumn)
    if (!text) { // maybe we're out of memory
      db = sqlite3_db_handle(stmt)
      if (!db) return WRAPPER_WEIRD
      err = sqlite3_errcode(db)
      if (err == SQLITE_NOMEM) return err
    }
    else {
      length = sqlite3_column_bytes16(stmt, jcolumn)
      if (length < 0) return WRAPPER_WEIRD_2
      result = jenv.NewString(jenv, text, length / sizeof(jchar))
      if (!result) return WRAPPER_CANNOT_ALLOCATE_STRING
    }
    jenv.SetObjectArrayElement(jenv, joutValue, 0, result)
    SQLITE_OK
  }*/

  def sqlite3ColumnBlob(stmt: SQLiteStatement.Handle, column: Int): Array[Byte] = {

    val ptrBox = sqlite3_column_blob(stmt, column)
    val rc = myLastReturnCode

    if (rc != SQLITE_OK) null
    else {
      CUtils.bytes2ByteArray(ptrBox.ptr, ptrBox.length) // will alloc memory using GC
    }
  }

  def sqlite3_column_blob(stmt: SQLiteStatement.Handle, column: Int): PtrBox = {

    myLastReturnCode = 0

    val value: Ptr[Byte] = sqlite.sqlite3_column_blob(stmt, column)
    var length: CInt = 0

    val rc = if (value == null) { // maybe we're out of memory
      val err = SQLiteWrapper._getStmtErrCode(stmt)
      if (err == SQLITE_NOMEM) err else SQLITE_OK
    }
    else {
      length = sqlite.sqlite3_column_bytes(stmt, column)
      if (length < 0) WRAPPER_WEIRD_2

      SQLITE_OK
    }

    myLastReturnCode = rc

    PtrBox(value, length.toULong )
  }

  /*
  def sqlite3_column_blob(jenv: Nothing, jcls: Nothing, jstmt: Nothing, jcolumn: Nothing, joutValue: Nothing): Nothing = {
    val stmt = jstmt.asInstanceOf[Nothing]
    var value: Unit = 0
    var db = 0
    var err = 0
    var length = 0
    var result = 0
    var resultPtr: Unit = 0
    if (!stmt) return WRAPPER_INVALID_ARG_1
    if (!joutValue) return WRAPPER_INVALID_ARG_3
    value = sqlite3_column_blob(stmt, jcolumn)
    if (!value) { // maybe we're out of memory
      db = sqlite3_db_handle(stmt)
      if (!db) return WRAPPER_WEIRD
      err = sqlite3_errcode(db)
      if (err == SQLITE_NOMEM) return err
    }
    else {
      length = sqlite3_column_bytes(stmt, jcolumn)
      if (length < 0) return WRAPPER_WEIRD_2
      result = jenv.NewByteArray(jenv, length)
      if (!result) return WRAPPER_CANNOT_ALLOCATE_STRING
      resultPtr = jenv.GetPrimitiveArrayCritical(jenv, result, 0).asInstanceOf[Unit]
      if (!resultPtr) return WRAPPER_CANNOT_ALLOCATE_STRING
      memcpy(resultPtr, value, length)
      jenv.ReleasePrimitiveArrayCritical(jenv, result, resultPtr, 0)
    }
    jenv.SetObjectArrayElement(jenv, joutValue, 0, result)
    SQLITE_OK
  }*/

  def sqlite3BlobOpen(
    db: SQLiteConnection.Handle,
    dbname: String, // @Nullable
    table: String,
    column: String,
    rowid: Long,
    writeAccess: Boolean
  ): SQLiteBlob.Handle = {
    require(db != null, "db is null")
    require(table != null, "table is null")
    require(column != null, "column is null")

    myLastReturnCode = 0

    val blobPtr = stackalloc[Ptr[sqlite.sqlite3_blob]]()

    val rc = Zone { implicit z: Zone =>
      sqlite.sqlite3_blob_open(
        db,
        CUtils.toCString(dbname),
        CUtils.toCString(table),
        CUtils.toCString(column),
        rowid,
        if (writeAccess) 1 else 0,
        blobPtr
      )
    }

    myLastReturnCode = rc

    (!blobPtr).asInstanceOf[SQLiteBlob.Handle]
  }

  /*
  def sqlite3_blob_open(jenv: Nothing, jcls: Nothing, jdb: Nothing, jdbname: Nothing, jtable: Nothing, jcolumn: Nothing, jrowid: Nothing, jwriteAccess: Nothing, jresult: Nothing): Nothing = {
    var db = 0
    var dbname = 0
    var table = 0
    var column = 0
    var rc = 0
    val blob = 0
    val r = 0
    if (!jdb) return WRAPPER_INVALID_ARG_1
    if (!jtable) return WRAPPER_INVALID_ARG_3
    if (!jcolumn) return WRAPPER_INVALID_ARG_4
    if (!jresult) return WRAPPER_INVALID_ARG_5
    db = jdb.asInstanceOf[Nothing]
    dbname = if (jdbname) jenv.GetStringUTFChars(jenv, jdbname, 0)
    else 0
    table = jenv.GetStringUTFChars(jenv, jtable, 0)
    column = jenv.GetStringUTFChars(jenv, jcolumn, 0)
    if (!table || !column || (!dbname && jdbname)) rc = WRAPPER_CANNOT_TRANSFORM_STRING
    else {
      rc = sqlite3_blob_open(db, dbname, table, column, jrowid, if (jwriteAccess) 1
      else 0, blob)
      /*if (blob) {
            *((sqlite3_blob**)&r) = blob;
            jenv.SetLongArrayRegion(jenv, jresult, 0, 1, &r);
          }*/
    }
    if (dbname) jenv.ReleaseStringUTFChars(jenv, jdbname, dbname)
    if (table) jenv.ReleaseStringUTFChars(jenv, jtable, table)
    if (column) jenv.ReleaseStringUTFChars(jenv, jcolumn, column)
    rc
  }*/

  // TODO: DBO => check this method is working
  def wrapper_alloc(size: Int): DirectBuffer = {
    require(size >= DirectBuffer.CONTROL_BYTES + 1, "invalid size")

    //println("*************** INSIDE wrapper_alloc ******************")

    myLastReturnCode = 0

    /*myLastReturnCode = _SQLiteManualJNI.wrapper_alloc(size, myLong, myObject)
    val controlBuffer = if (myObject(0).isInstanceOf[Nothing]) myObject(0).asInstanceOf[Nothing]
    else null
    val dataBuffer = if (myObject(1).isInstanceOf[Nothing]) myObject(1).asInstanceOf[Nothing]
    else null

    val ptr = myLong(0)*/

    val bufferArray = new Array[Byte](size)
    //val bufferPtr = bufferArray.asInstanceOf[ByteArray].at(0)
    //val controlBytePtr = bufferArray.asInstanceOf[ByteArray].at(0)
    //val dataBytesArray = bufferArray.asInstanceOf[ByteArray].at(DirectBuffer.CONTROL_BYTES).cast[ByteArray].asInstanceOf[Array[Byte]]

    //println("wrapping controlBuffer")
    val controlBuffer = java.nio.ByteBuffer.wrap(bufferArray, 0, DirectBuffer.CONTROL_BYTES)
    //println("wrapping dataBuffer")
    //val dataBuffer = java.nio.ByteBuffer.wrap(dataBytesArray)
    val dataBuffer = java.nio.ByteBuffer.wrap(bufferArray, DirectBuffer.CONTROL_BYTES, size - DirectBuffer.CONTROL_BYTES)
    val dataBufferWrapper = new ByteBufferWrapper(dataBuffer, DirectBuffer.CONTROL_BYTES)

    //println("controlBuffer limit() assert: " + controlBuffer.limit())
    //assert(controlBuffer.limit() == DirectBuffer.CONTROL_BYTES, "invalid limit")

    myLastReturnCode = SQLITE_OK

    //println("instantiating DirectBuffer")

    new DirectBuffer(bufferArray.asInstanceOf[DirectBuffer.Handle], controlBuffer, dataBufferWrapper, size) // PtrBox(bufferPtr, size)
  }

  /*def wrapper_alloc(jenv: Nothing, jcls: Nothing, size: Nothing, ppBuf: Nothing, ppByteBuffer: Nothing): Nothing = {
    var ptr: Unit = 0
    val lptr = 0
    var controlBuffer = 0
    var dataBuffer = 0
    if (size < 3) return WRAPPER_INVALID_ARG_1
    if (!ppBuf) return WRAPPER_INVALID_ARG_2
    if (!ppByteBuffer) return WRAPPER_INVALID_ARG_3
    ptr = sqlite3_malloc(size)
    if (!ptr) return WRAPPER_OUT_OF_MEMORY
    val lptr: Unit = ptr
    controlBuffer = jenv.NewDirectByteBuffer(jenv, ptr, 2)
    if (!controlBuffer) {
      sqlite3_free(ptr)
      return WRAPPER_OUT_OF_MEMORY
    }
    dataBuffer = jenv.NewDirectByteBuffer(jenv, (ptr.asInstanceOf[Nothing] + 2).asInstanceOf[Unit], size - 2)
    if (!dataBuffer) {
      sqlite3_free(ptr)
      return WRAPPER_OUT_OF_MEMORY
    }
    memset(ptr, 0, size)
    jenv.SetLongArrayRegion(jenv, ppBuf, 0, 1, lptr)
    jenv.SetObjectArrayElement(jenv, ppByteBuffer, 0, controlBuffer)
    jenv.SetObjectArrayElement(jenv, ppByteBuffer, 1, dataBuffer)
    SQLITE_OK
  }*/

  def wrapper_free(buffer: DirectBuffer): Int = {
    buffer.invalidate()

    SQLITE_OK
  }

  /*def wrapper_free(buffer: DirectBuffer): Int = {
    val handle = buffer.getHandle
    buffer.invalidate
    if (handle == null) return 0
    val rc = _SQLiteManualJNI.wrapper_free(SWIGTYPE_p_direct_buffer.getCPtr(handle))
    rc
  }
  def wrapper_free(jenv: Nothing, jcls: Nothing, jbuffer: Nothing): Nothing = {
    val ptr: Ptr[CString] = jbuffer

    if (!ptr) return SQLITE_OK
    // actually free if not in use
    if (!ptr(0)) {
      ptr(1) = -1
      sqlite3_free(ptr)
    }
    else ptr(1) = 1
    SQLITE_OK
  }
*/


  /*
  def wrapper_bind_buffer(stmt: Nothing, index: Int, buffer: DirectBuffer): Int = {
    val handle = buffer.getHandle
    if (handle == null) return SQLiteConstants.WRAPPER_WEIRD
    val size = buffer.getPosition
    _SQLiteManualJNI.wrapper_bind_buffer(SWIGTYPE_p_sqlite3_stmt.getCPtr(stmt), index, SWIGTYPE_p_direct_buffer.getCPtr(handle), size)
  }

  def wrapper_bind_buffer(jenv: Nothing, jcls: Nothing, jstmt: Nothing, jindex: Nothing, jbuffer: Nothing, jlength: Nothing): Nothing = {
    val stmt = jstmt.asInstanceOf[Nothing]
    val buffer: Unit = jbuffer.asInstanceOf[Nothing]
    var rc = 0
    if (!stmt) return WRAPPER_INVALID_ARG_1
    if (!buffer) return WRAPPER_INVALID_ARG_2
    // if should be freed...
    if (buffer(1)) return WRAPPER_INVALID_ARG_3
    // mark as used
    buffer(0) += 1
    rc = sqlite3_bind_blob(stmt, jindex, (buffer + 2).asInstanceOf[Unit], jlength, bind_release)
    if (rc != SQLITE_OK) buffer(0) -= 1
    rc
  }

  def bind_release(ptr: Nothing): Unit = {
    var buffer = null
    if (!ptr) return
    buffer = ptr.asInstanceOf[Nothing] - 2
    if (buffer(0) > 0) buffer(0) -= 1
    if (buffer(1) eq 1) sqlite3_free(buffer.asInstanceOf[Unit])
  }
  */


  /*
  def wrapper_column_buffer(stmt: Nothing, column: Int): Nothing = {
    myLastReturnCode = 0
    myObject(0) = null
    myLastReturnCode = _SQLiteManualJNI.wrapper_column_buffer(SWIGTYPE_p_sqlite3_stmt.getCPtr(stmt), column, myObject)
    val r = if (myObject(0).isInstanceOf[Nothing]) myObject(0).asInstanceOf[Nothing]
    else null
    myObject(0) = null
    r
  }

  def wrapper_column_buffer(jenv: Nothing, jcls: Nothing, jstmt: Nothing, jcolumn: Nothing, joutBuffer: Nothing): Nothing = {
    val stmt = jstmt.asInstanceOf[Nothing]
    var value = 0
    var db = 0
    var err = 0
    var length = 0
    var result = 0
    if (!stmt) return WRAPPER_INVALID_ARG_1
    if (!joutBuffer) return WRAPPER_INVALID_ARG_3
    value = sqlite3_column_blob(stmt, jcolumn)
    if (!value) { // maybe we're out of memory
      db = sqlite3_db_handle(stmt)
      if (!db) return WRAPPER_WEIRD
      err = sqlite3_errcode(db)
      if (err == SQLITE_NOMEM) return err
    }
    else {
      length = sqlite3_column_bytes(stmt, jcolumn)
      if (length < 0) return WRAPPER_WEIRD_2
      result = jenv.NewDirectByteBuffer(jenv, value.asInstanceOf[Unit], length)
      if (!result) return WRAPPER_CANNOT_ALLOCATE_STRING
    }
    jenv.SetObjectArrayElement(jenv, joutBuffer, 0, result)
    SQLITE_OK
  }*/

  def install_progress_handler(db: SQLiteConnection.Handle, stepsPerCallback: Int): ProgressHandler = {
    require(db != null, "db is null")
    require(stepsPerCallback > 0, "invalid value for stepsPerCallback")

    myLastReturnCode = 0

    val buffLen = 2 * sizeof[CLong].toInt
    //println("before wrapper_alloc")
    val buffer = wrapper_alloc(buffLen)
    //println("after wrapper_alloc")
    val bufferHandle = buffer.getHandle()

    sqlite.sqlite3_progress_handler(db, stepsPerCallback, SQLiteWrapper.progress_handler_cb, bufferHandle.asInstanceOf[ByteArray].at(0))

    myLastReturnCode = SQLITE_OK

    new ProgressHandler(bufferHandle, java.nio.ByteBuffer.wrap(bufferHandle), stepsPerCallback)
  }

  /*
  def install_progress_handler(db: Nothing, stepsPerCallback: Int): Nothing = {
    myLastReturnCode = 0
    myLong(0) = 0
    myObject(0) = null
    myLastReturnCode = _SQLiteManualJNI.install_progress_handler(SWIGTYPE_p_sqlite3.getCPtr(db), stepsPerCallback, myLong, myObject)
    val r = if (myObject(0).isInstanceOf[Nothing]) myObject(0).asInstanceOf[Nothing]
    else null
    myObject(0) = null
    val ptr = myLong(0)
    myLong(0) = 0
    if (ptr == 0 || r == null) return null
    new ProgressHandler(new SWIGTYPE_p_direct_buffer(ptr, true), r, stepsPerCallback)
  }

  def install_progress_handler(jenv: Nothing, jcls: Nothing, jdb: Nothing, steps: Nothing, ppBuf: Nothing, ppByteBuffer: Nothing): Nothing = {
    var db = 0
    val rc = 0
    var ptr = 0
    var lptr = 0
    var buffer = 0
    val len = 2 * sizeof(jlong)
    if (!jdb) return WRAPPER_INVALID_ARG_1
    if (!ppBuf) return WRAPPER_INVALID_ARG_2
    if (!ppByteBuffer) return WRAPPER_INVALID_ARG_3
    if (steps < 1) return WRAPPER_INVALID_ARG_4
    db = jdb.asInstanceOf[Nothing]
    ptr = sqlite3_malloc(len).asInstanceOf[Nothing]
    if (!ptr) return WRAPPER_OUT_OF_MEMORY
    lptr = ptr
    buffer = jenv.NewDirectByteBuffer(jenv, ptr, len)
    if (!buffer) {
      sqlite3_free(ptr)
      return WRAPPER_OUT_OF_MEMORY
    }
    memset(ptr, 0, len)
    jenv.SetLongArrayRegion(jenv, ppBuf, 0, 1, lptr)
    jenv.SetObjectArrayElement(jenv, ppByteBuffer, 0, buffer)
    sqlite3_progress_handler(db, steps, progress_handler, ptr)
    SQLITE_OK
  }*/

  def uninstall_progress_handler(db: SQLiteConnection.Handle, handler: ProgressHandler): Int = {
    require(db != null, "db is null")
    require(handler != null, "handler is null")

    val pointer = handler.dispose()
    if (pointer == null) return 0

    sqlite.sqlite3_progress_handler(db, 1, null, null)

    DirectBuffer.disposeHandle(pointer)

    SQLITE_OK
  }

  /*
  def uninstall_progress_handler(db: Nothing, handler: Nothing): Int = {
    val pointer = handler.dispose
    if (pointer == null) return 0
    _SQLiteManualJNI.uninstall_progress_handler(SWIGTYPE_p_sqlite3.getCPtr(db), SWIGTYPE_p_direct_buffer.getCPtr(pointer))
  }

  def uninstall_progress_handler(jenv: Nothing, jcls: Nothing, jdb: Nothing, jptr: Nothing): Nothing = {
    var db = 0
    var ptr = 0
    if (!jdb) return WRAPPER_INVALID_ARG_1
    if (!jptr) return WRAPPER_INVALID_ARG_2
    db = jdb.asInstanceOf[Nothing]
    ptr = jptr
    sqlite3_progress_handler(db, 1, 0, 0)
    sqlite3_free(ptr)
    SQLITE_OK
  }

*/

  def wrapper_load_ints(stmt: SQLiteStatement.Handle, column: Int, buffer: Array[Int], offset: Int, count: Int): Int = {
    require(stmt != null, "stmt is null")
    require(column >= 0, "invalid column index")
    require(buffer != null, "buffer is null")
    require(count > 0, "invalid count value")

    val bufferLen = buffer.length
    require(offset >= 0 && offset + count <= bufferLen, "invalid offset")

    myLastReturnCode = 0

    var p = offset
    var loaded = 0
    while (loaded < count && {myLastReturnCode = sqlite.sqlite3_step(stmt); myLastReturnCode == SQLITE_ROW}) {
      buffer(p) = sqlite.sqlite3_column_int(stmt, column)
      p += 1
      loaded += 1
    }

    loaded
  }
  /*
  def wrapper_load_ints(stmt: Nothing, column: Int, buffer: Array[Int], offset: Int, count: Int): Int = {
    myLastReturnCode = 0
    myInt(0) = 0
    myLastReturnCode = _SQLiteManualJNI.wrapper_load_ints(SWIGTYPE_p_sqlite3_stmt.getCPtr(stmt), column, buffer, offset, count, myInt)
    val r = myInt(0)
    myInt(0) = 0
    r
  }

  def wrapper_load_ints(jenv: Nothing, jcls: Nothing, jstmt: Nothing, column: Nothing, ppBuf: Nothing, offset: Nothing, count: Nothing, ppCount: Nothing): Nothing = {
    val stmt = jstmt.asInstanceOf[Nothing]
    var loaded = 0
    var p = offset
    var rc = 0
    var buf = 0
    var len = 0
    if (!stmt) return WRAPPER_INVALID_ARG_1
    if (!ppBuf) return WRAPPER_INVALID_ARG_2
    if (!ppCount) return WRAPPER_INVALID_ARG_3
    if (count <= 0) return WRAPPER_INVALID_ARG_4
    len = jenv.GetArrayLength(jenv, ppBuf)
    if (offset < 0 || offset + count > len) return WRAPPER_INVALID_ARG_4
    buf = jenv.GetIntArrayElements(jenv, ppBuf, 0)
    if (!buf) return WRAPPER_CANNOT_ALLOCATE_STRING
    while ( {
      (rc = sqlite3_step(stmt)) == SQLITE_ROW
    }) {
      buf({
        p += 1; p - 1
      }) = sqlite3_column_int(stmt, column)
      if ( {
        loaded += 1; loaded
      } >= count) break //todo: break is not supported
    }
    jenv.ReleaseIntArrayElements(jenv, ppBuf, buf, 0)
    jenv.SetIntArrayRegion(jenv, ppCount, 0, 1, loaded)
    rc
  }*/

  def wrapper_load_longs(stmt: SQLiteStatement.Handle, column: Int, buffer: Array[Long], offset: Int, count: Int): Int = {
    require(stmt != null, "stmt is null")
    require(column >= 0, "invalid column index")
    require(buffer != null, "buffer is null")
    require(count > 0, "invalid count value")

    val bufferLen = buffer.length
    require(offset >= 0 && offset + count <= bufferLen, "invalid offset")

    myLastReturnCode = 0

    var p = offset
    var loaded = 0
    while (loaded < count && {myLastReturnCode = sqlite.sqlite3_step(stmt); myLastReturnCode == SQLITE_ROW}) {
      buffer(p) = sqlite.sqlite3_column_int64(stmt, column)
      p += 1
      loaded += 1
    }

    loaded
  }

  /*
  def wrapper_load_longs(stmt: Nothing, column: Int, buffer: Array[Long], offset: Int, count: Int): Int = {
    myLastReturnCode = 0
    myInt(0) = 0
    myLastReturnCode = _SQLiteManualJNI.wrapper_load_longs(SWIGTYPE_p_sqlite3_stmt.getCPtr(stmt), column, buffer, offset, count, myInt)
    val r = myInt(0)
    myInt(0) = 0
    r
  }

  def wrapper_load_longs(jenv: Nothing, jcls: Nothing, jstmt: Nothing, column: Nothing, ppBuf: Nothing, offset: Nothing, count: Nothing, ppCount: Nothing): Nothing = {
    val stmt = jstmt.asInstanceOf[Nothing]
    var loaded = 0
    var p = offset
    var rc = 0
    val uf = 0
    var len = 0
    if (!stmt) return WRAPPER_INVALID_ARG_1
    if (!ppBuf) return WRAPPER_INVALID_ARG_2
    if (!ppCount) return WRAPPER_INVALID_ARG_3
    if (count <= 0) return WRAPPER_INVALID_ARG_4
    len = jenv.GetArrayLength(jenv, ppBuf)
    if (offset < 0 || offset + count > len) return WRAPPER_INVALID_ARG_4
    buf = jenv.GetLongArrayElements(jenv, ppBuf, 0)
    if (!buf) return WRAPPER_CANNOT_ALLOCATE_STRING
    while ( {
      (rc = sqlite3_step(stmt)) == SQLITE_ROW
    }) {
      buf({
        p += 1; p - 1
      }) = sqlite3_column_int64(stmt, column)
      if ( {
        loaded += 1; loaded
      } >= count) break //todo: break is not supported
    }
    jenv.ReleaseLongArrayElements(jenv, ppBuf, buf, 0)
    jenv.SetIntArrayRegion(jenv, ppCount, 0, 1, loaded)
    rc
  }*/


  /*
  def sqlite3_intarray_register(db: Nothing): Nothing = {
    myLastReturnCode = 0
    myLong(0) = 0
    myLastReturnCode = _SQLiteManualJNI.sqlite3_intarray_register(SWIGTYPE_p_sqlite3.getCPtr(db), myLong)
    if (myLong(0) eq 0) null
    else new Nothing(myLong(0), true)
  }

  def sqlite3_intarray_register(jenv: Nothing, jcls: Nothing, jdb: Nothing, ppBuf: Nothing): Nothing = {
    val db = jdb.asInstanceOf[Nothing]
    val module = 0
    val r = 0
    var rc = 0
    if (!db) return WRAPPER_INVALID_ARG_1
    if (!ppBuf) return WRAPPER_INVALID_ARG_3
    rc = sqlite3_intarray_register(db, module)
    /*if (module) {
        *((sqlite3_intarray_module**)&r) = module;
        jenv.SetLongArrayRegion(jenv, ppBuf, 0, 1, &r);
      }*/ rc
  }

  def sqlite3_intarray_create(module: Nothing, name: String): Nothing = {
    myLastReturnCode = 0
    myLong(0) = 0
    myLastReturnCode = _SQLiteManualJNI.sqlite3_intarray_create(SWIGTYPE_p_intarray_module.getCPtr(module), name, myLong)
    if (myLong(0) eq 0) null
    else new Nothing(myLong(0), true)
  }

  def sqlite3_intarray_create(jenv: Nothing, jcls: Nothing, jmodule: Nothing, jname: Nothing, ppBuf: Nothing): Nothing = {
    val module = jmodule
    val arr = 0
    val r = 0
    var name = 0
    var namec = 0
    var rc = 0
    if (!module) return WRAPPER_INVALID_ARG_1
    if (!ppBuf) return WRAPPER_INVALID_ARG_3
    name = jenv.GetStringUTFChars(jenv, jname, 0)
    if (!name) return WRAPPER_CANNOT_TRANSFORM_STRING
    namec = sqlite3_malloc(strlen(name).asInstanceOf[Int] + 1).asInstanceOf[Nothing]
    if (namec) strcpy(namec, name)
    jenv.ReleaseStringUTFChars(jenv, jname, name)
    if (!namec) return SQLITE_NOMEM
    rc = sqlite3_intarray_create(module, namec, arr)
    /*if (arr) {
        *((sqlite3_intarray**)&r) = arr;
        jenv.SetLongArrayRegion(jenv, ppBuf, 0, 1, &r);
      }*/ rc
  }

  def sqlite3_intarray_bind(array: Nothing, values: Array[Long], offset: Int, length: Int, ordered: Boolean, unique: Boolean): Int = _SQLiteManualJNI.sqlite3_intarray_bind(SWIGTYPE_p_intarray.getCPtr(array), values, offset, length, ordered, unique)

  def sqlite3_intarray_bind(jenv: Nothing, jcls: Nothing, jarray: Nothing, jbuffer: Nothing, joffset: Nothing, jlength: Nothing, ordered: Nothing, unique: Nothing): Nothing = {
    val array = jarray
    var buf = 0
    var copy = 0
    var len = 0
    var rc = 0
    if (!array) return WRAPPER_INVALID_ARG_1
    if (!jbuffer) return WRAPPER_INVALID_ARG_2
    len = jenv.GetArrayLength(jenv, jbuffer)
    if (len < 0) return WRAPPER_INVALID_ARG_3
    if (joffset < 0 || joffset > len) return WRAPPER_INVALID_ARG_4
    if (jlength < 0 || joffset + jlength > len) return WRAPPER_INVALID_ARG_5
    if (jlength > 0) {
      copy = sqlite3_malloc(jlength * sizeof(sqlite3_int64)).asInstanceOf[Nothing]
      if (!copy) return WRAPPER_CANNOT_ALLOCATE_STRING
      buf = jenv.GetPrimitiveArrayCritical(jenv, jbuffer, 0).asInstanceOf[Nothing]
      if (!buf) return WRAPPER_CANNOT_ALLOCATE_STRING
      memcpy(copy, buf + joffset, jlength * sizeof(sqlite3_int64))
      jenv.ReleasePrimitiveArrayCritical(jenv, jbuffer, buf.asInstanceOf[Unit], JNI_ABORT)
      rc = sqlite3_intarray_bind(array, jlength, copy, sqlite3_free, ordered.asInstanceOf[Int], unique.asInstanceOf[Int], 1)
    }
    else rc = sqlite3_intarray_bind(array, 0, 0, 0, 0, 0, 1)
    rc
  }

  def sqlite3_intarray_unbind(array: Nothing): Int = _SQLiteManualJNI.sqlite3_intarray_unbind(SWIGTYPE_p_intarray.getCPtr(array))

  def sqlite3_intarray_unbind(jenv: Nothing, jcls: Nothing, jarray: Nothing): Nothing = {
    val array = jarray
    var rc = 0
    if (!array) return WRAPPER_INVALID_ARG_1
    rc = sqlite3_intarray_bind(array, 0, 0, 0, 0, 0, 0)
    rc
  }

  def sqlite3_intarray_destroy(array: Nothing): Int = _SQLiteManualJNI.sqlite3_intarray_destroy(SWIGTYPE_p_intarray.getCPtr(array))

  def sqlite3_intarray_destroy(jenv: Nothing, jcls: Nothing, jarray: Nothing): Nothing = {
    val array = jarray
    var rc = 0
    if (!array) return WRAPPER_INVALID_ARG_1
    rc = sqlite3_intarray_destroy(array)
    rc
  }

  def sqlite3_load_extension(db: Nothing, file: String, proc: String): String = {
    myLastReturnCode = 0
    myString(0) = null
    myLastReturnCode = _SQLiteManualJNI.sqlite3_load_extension(SWIGTYPE_p_sqlite3.getCPtr(db), file, proc, myString)
    val r = myString(0)
    myString(0) = null
    r
  }

  def sqlite3_load_extension(jenv: Nothing, jcls: Nothing, jdb: Nothing, jfile: Nothing, jproc: Nothing, ppError: Nothing): Nothing = {
    val db = jdb.asInstanceOf[Nothing]
    var rc = 0
    var file = 0
    var proc = 0
    val error = 0
    var errorString = 0
    if (jfile) {
      file = jenv.GetStringUTFChars(jenv, jfile, 0).asInstanceOf[Nothing]
      if (!file) return WRAPPER_CANNOT_TRANSFORM_STRING
    }
    if (jproc) {
      proc = jenv.GetStringUTFChars(jenv, jproc, 0).asInstanceOf[Nothing]
      if (!proc) {
        if (file) jenv.ReleaseStringUTFChars(jenv, jfile, file.asInstanceOf[Nothing])
        return WRAPPER_CANNOT_TRANSFORM_STRING
      }
    }
    rc = sqlite3_load_extension(db, file.asInstanceOf[Nothing], proc.asInstanceOf[Nothing], error)
    if (error) {
      errorString = jenv.NewStringUTF(jenv, error)
      if (errorString) jenv.SetObjectArrayElement(jenv, ppError, 0, errorString)
      sqlite3_free(error)
    }
    if (proc) jenv.ReleaseStringUTFChars(jenv, jproc, proc.asInstanceOf[Nothing])
    if (file) jenv.ReleaseStringUTFChars(jenv, jfile, file.asInstanceOf[Nothing])
    rc
  }*/

  /*def sqlite3_win32_set_directory(jenv: Nothing, jcls: Nothing, jtype: Nothing, zValue: Nothing): Nothing = {
    //#ifdef SQLITE_OS_WIN
    var rc = 0
    var name = 0
    val `type` = jtype.asInstanceOf[Long]
    if (zValue) {
      name = jenv.GetStringCritical(jenv, zValue, 0)
      if (!name) return WRAPPER_CANNOT_TRANSFORM_STRING
      rc = sqlite_addons.sqlite3_win32_set_directory16(`type`, name)
      jenv.ReleaseStringCritical(jenv, zValue, name)
    }
    else rc = sqlite_addons.sqlite3_win32_set_directory16(`type`, 0)
    rc
    /*#else
      return SQLITE_ERROR;
    #endif*/
  }*/

}