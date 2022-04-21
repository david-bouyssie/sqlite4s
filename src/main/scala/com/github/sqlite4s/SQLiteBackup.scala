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

import scala.scalanative.unsafe._

import bindings.sqlite
import bindings.SQLITE_CONSTANT._
import SQLITE_WRAPPER_ERROR_CODE.WRAPPER_BACKUP_DISPOSED

object SQLiteBackup {
  sealed trait IHandle
  type Handle = Ptr[sqlite.sqlite3_backup] with IHandle
}

/**
  * SQLiteBackup wraps an instance of SQLite database backup, represented as <strong><code>sqlite3_backup*</strong></code>
  * in SQLite C API.
  * <p>
  * Usage example:
  * <pre>
  * SQLiteBackup backup = connection.initializeBackup(new File("filename"));
  * try {
  * while (!backup.isFinished()) {
  *     backup.backupStep(32);
  * }
  * } finally {
  *   backup.dispose();
  * }
  * </pre>
  * </p>
  * <p>
  * Unless a method is marked as thread-safe, it is confined to the thread that has opened the connection to the source
  * database. Calling a confined method from a different thread will result in exception.
  * </p>
  *
  * @author Igor Korsunov
  * @see <a href="http://www.sqlite.org/c3ref/backup_finish.html#sqlite3backupinit"> SQLite Online Backup API</a>
  * @see <a href="http://www.sqlite.org/backup.html">Using the SQLite Online Backup API</a>
  * @see SQLiteConnection#initializeBackup
  */
class SQLiteBackup private[sqlite4s](
  var mySourceController: SQLiteController,

  var myDestinationController: SQLiteController,

  /**
    * Handle for native operations
    */
  var myHandle: SQLiteBackup.Handle,

  /**
    * Source database connection
    */
  val mySource: SQLiteConnection,

  /**
    * Destination database connection
    */
  val myDestination: SQLiteConnection

) extends Logging {

  if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), "instantiated"))

  /**
    * If true, last call to sqlite3_backup_step() returned SQLITE_DONE
    */
  private var myFinished = false

  @throws[SQLiteException]
  private def _getHandleOrFail(): SQLiteBackup.Handle = {
    val handle = myHandle
    if (handle == null) throw new SQLiteException(WRAPPER_BACKUP_DISPOSED, null)
    handle
  }

  /**
    * Copy up to pagesToBackup pages from source database to destination. If pagesToBackup is negative, all remaining
    * pages are copied.
    * <p>
    * If source database is modified during backup by any connection other than the source connection,
    * then the backup will be restarted by the next call to backupStep.
    * If the source database is modified by the source connection itself, then destination
    * database is be updated without backup restart.
    * </p>
    *
    * @param pagesToBackup the maximum number of pages to back up during this step, or negative number to back up all pages
    * @return true if the backup was finished, false if there are still pages to back up
    * @throws SQLiteException     if SQLite returns an error or if the call violates the contract of this class
    * @throws SQLiteBusyException if SQLite cannot establish SHARED_LOCK on the source database or RESERVED_LOCK on
    *                             the destination database or source connection is currently used to write to the database.
    *                             In these cases call to backupStep can be retried later.
    * @see <a href="http://www.sqlite.org/c3ref/backup_finish.html#sqlite3backupstep">sqlite3_backup_step</a>
    */
  @throws[SQLiteException]
  @throws[SQLiteBusyException]
  def backupStep(pagesToBackup: Int): Boolean = {
    mySource.checkThread()
    myDestination.checkThread()
    if (myFinished) {
      logger.warn(Internal.mkLogMessage(this.toString(), "already finished"))
      return true
    }

    if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), s"backupStep($pagesToBackup)"))

    val rc = sqlite.sqlite3_backup_step(_getHandleOrFail(), pagesToBackup)
    if (rc != SQLITE_DONE) {
      if (rc != SQLITE_OK) myDestination.throwResult(rc, "backupStep failed")
    } else {
      if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), "finished"))
      myFinished = true
    }

    myFinished
  }

  /**
    * Checks whether the backup was successfully finished.
    *
    * @return true if last call to { @link #backupStep} has returned true.
    */
  def isFinished: Boolean = myFinished

  /**
    * Returns connection to the destination database, that was opened by {@link com.almworks.sqlite4java.SQLiteConnection#initializeBackup}.
    * <p>
    * <strong>Important!</strong> If you call this method, you should be careful about disposing the connection you got.
    * You should only dispose it <strong>after</strong> disposing SQLiteBackup instance, otherwise the JVM might crash.
    * </p>
    *
    * @return destination database connection
    */
  def getDestinationConnection(): SQLiteConnection = myDestination

  /**
    * Dispose this backup instance and, if <code>disposeDestination</code> is true, dispose the connection to
    * the destination database as well.
    * <p/>
    * You might want to pass <code>false</code> to this method to subsequently call {@link #getDestinationConnection()}
    * and perform any actions on the fresh backup of the database, then dispose it yourself.
    *
    * @param disposeDestination if true, connection to the destination database will be disposed
    */
  def dispose(disposeDestination: Boolean): Unit = {
    try {
      mySourceController.validate()
      myDestinationController.validate()
    } catch {
      case e: SQLiteException =>
        SQLiteException.logWarnOrThrowError(
          msg => logger.warn(msg),
          s"invalid dispose: $e",
          true
        )
        return
    }
    if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), "disposing"))

    val handle = myHandle
    if (handle != null) {
      sqlite.sqlite3_backup_finish(handle)
      myHandle = null
      mySourceController = SQLiteController.getDisposed(mySourceController)
      myDestinationController = SQLiteController.getDisposed(myDestinationController)
    }

    if (disposeDestination) myDestination.dispose()
  }

  /**
    * Disposes this backup instance and connection to the destination database.
    * <p>
    * This is a convenience method, equivalent to <code>dispose(true)</code>.
    * </p>
    *
    * @see #dispose(boolean)
    */
  def dispose(): Unit = {
    dispose(true)
  }

  /**
    * Returns the total number of pages in the source database.
    *
    * @return total number of pages to back up
    * @throws SQLiteException if called from a different thread or if source or destination connection are disposed
    * @see <a href="http://www.sqlite.org/c3ref/backup_finish.html#sqlite3backupfinish">SQLite Online Backup API</a>
    */
  @throws[SQLiteException]
  def getPageCount(): Int = {
    mySourceController.validate()
    myDestinationController.validate()
    sqlite.sqlite3_backup_pagecount(_getHandleOrFail())
  }

  /**
    * Returns the number of pages still to be backed up.
    *
    * @return number of remaining pages
    * @throws SQLiteException if called from a different thread or if source or destination connection are disposed
    */
  @throws[SQLiteException]
  def getRemaining(): Int = {
    mySourceController.validate()
    myDestinationController.validate()
    sqlite.sqlite3_backup_remaining(_getHandleOrFail())
  }

  override def toString(): String = s"Backup [$mySource -> $myDestination]"

}

