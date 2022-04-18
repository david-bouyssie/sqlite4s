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

import bindings.SQLITE_CONSTANT._
import bindings.SQLITE_EXTENDED_RESULT_CODE._

object SQLITE_WRAPPER_ERROR_CODE {

  /**
    * Something strange happened.
    */
  val WRAPPER_WEIRD: Int = -99

  /**
    * Method called in thread that wasn't allowed.
    */
  val WRAPPER_CONFINEMENT_VIOLATED: Int = -98

  /**
    * Wasn't opened
    */
  val WRAPPER_NOT_OPENED: Int = -97

  /**
    * Statement disposed
    */
  val WRAPPER_STATEMENT_DISPOSED: Int = -96

  /**
    * column() requested when no row returned
    */
  val WRAPPER_NO_ROW: Int = -95

  val WRAPPER_COLUMN_OUT_OF_RANGE: Int = -94

  /**
    * Blob disposed
    */
  val WRAPPER_BLOB_DISPOSED: Int = -93

  /**
    * Backup disposed
    */
  val WRAPPER_BACKUP_DISPOSED: Int = -113

  val WRAPPER_INVALID_ARG_1: Int = -11
  val WRAPPER_INVALID_ARG_2: Int = -12
  val WRAPPER_INVALID_ARG_3: Int = -13
  val WRAPPER_INVALID_ARG_4: Int = -14
  val WRAPPER_INVALID_ARG_5: Int = -15
  val WRAPPER_INVALID_ARG_6: Int = -16
  val WRAPPER_INVALID_ARG_7: Int = -17
  val WRAPPER_INVALID_ARG_8: Int = -18
  val WRAPPER_INVALID_ARG_9: Int = -19

  val WRAPPER_CANNOT_TRANSFORM_STRING: Int = -20
  val WRAPPER_CANNOT_ALLOCATE_STRING: Int = -21
  val WRAPPER_OUT_OF_MEMORY: Int = -22

  val WRAPPER_WEIRD_2: Int = -199

  val WRAPPER_CANNOT_LOAD_LIBRARY: Int = -91
  val WRAPPER_MISUSE: Int = -92

  val WRAPPER_USER_ERROR: Int = -999
}

object SQLiteException {
  // FIXME: this function seems to be useless => investigate this issue (ask to sqlite4java author)
  def logWarnOrThrowError(warningLogger: String => Unit, message: String, throwError: Boolean): Unit = {
    val msg = Internal.mkLogMessage(null, message)

    if (!throwError) warningLogger(msg)
    else throw new Exception(msg)
  }
}

/**
  * SQLiteException is thrown whenever SQLite cannot execute an operation and returns an error code.
  *
  * <p>
  * Error codes can be compared against {@link SQLiteConstants}.
  * <p>
  * It's safe to rollback the transaction when SQLiteException is caught.
  *
  * @author Igor Sereda
  *
  * @param errorCode codes are defined in { @link SQLiteConstants}
  * @param errorMessage optional error message
  * @param cause        error cause
  */
class SQLiteException(
  val errorCode: Int,
  val errorMessage: String,
  val cause: Throwable
) extends Exception("[" + errorCode + "] " + (if (errorMessage == null) "sqlite error"  else errorMessage), cause) with Logging {
  def getErrorCode(): Int = errorCode
  def getErrorMessage(): String = errorMessage
  override def getCause(): Throwable = cause

  /**
    * Creates an instance of SQLiteException.
    *
    * @param errorCode codes are defined in { @link SQLiteConstants}
    * @param errorMessage optional error message
    */
  def this(errorCode: Int, errorMessage: String) = {
    this(errorCode, errorMessage, null)
  }

  /**
    * Gets base error code returned by SQLite.
    * Base error code is the lowest 8 bit from the extended error code, like SQLITE_IOERR_BLOCKED.
    *
    * @return error code
    */
  def getBaseErrorCode(): Int = if (errorCode >= 0) errorCode & 0xFF else errorCode

}

/**
  * SQLiteBusyException is a special exception that is thrown whenever SQLite returns SQLITE_BUSY or
  * SQLITE_IOERR_BLOCKED error code. These codes mean that the current operation cannot proceed because the
  * required resources are locked.
  * <p>
  * When a timeout is set via {@link SQLiteConnection#setBusyTimeout}, SQLite will attempt to get the lock during
  * the specified timeout before returning this error.
  * <p>
  * It is recommended to rollback the transaction when this exception is received. However, SQLite tries
  * to make sure that only the last statement failed and it's possible to retry that statement within the current
  * transaction.
  *
  * @author Igor Sereda
  *
  * @see <a href="http://www.sqlite.org/c3ref/busy_handler.html">sqlite3_busy_handler</a>
  * @see <a href="http://www.sqlite.org/lang_transaction.html">Response To Errors Within A Transaction</a>
  */
class SQLiteBusyException(override val errorCode: Int, override val errorMessage: String) extends SQLiteException(errorCode, errorMessage) {
  assert(errorCode == SQLITE_BUSY || errorCode == SQLITE_IOERR_BLOCKED, errorCode)
}

/**
  * SQLiteInterruptedException is a special exception that is thrown whenever SQLite returns SQLITE_INTERRUPT
  * following a call to {@link SQLiteConnection#interrupt}.
  * <p/>
  * The transaction is rolled back when interrupted.
  *
  * @author Igor Sereda
  * @see <a href="http://www.sqlite.org/c3ref/interrupt.html">sqlite3_interrupt</a>
  */
class SQLiteInterruptedException(val resultCode: Int, val message: String) extends SQLiteException(resultCode, message) {
  def this() = {
    this(SQLITE_INTERRUPT, "")
  }
}