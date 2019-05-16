package com.github.sqlite4s

import scala.scalanative.native._
import Internal._
import SQLITE_WRAPPER_ERROR_CODE._
import com.github.sqlite4s.bindings.sqlite

object SQLiteBlob {

  sealed trait IHandle
  type Handle = Ptr[sqlite.sqlite3_blob] with IHandle

  implicit class HandleWrapper(val handle: Handle) extends AnyVal {
    def asPtr(): Ptr[sqlite.sqlite3_blob] = handle.asInstanceOf[Ptr[sqlite.sqlite3_blob]]
  }

}

/**
  * SQLiteBlob encapsulates <strong><code>sqlite3_blob*</code></strong> handle, which represents an open BLOB
  * (binary large object), stored in a single cell of a table.
  * <p>
  * SQLiteBlob is created by {@link SQLiteConnection#blob} method. After application is done using the instance
  * of SQLiteBlob, it should be disposed with {@link #dispose} method.
  * <p>
  * You can read or write portions of the stored blob using {@link #read} and {@link #write} methods. Note that
  * you cannot change the size of the blob using this interface.
  * <p>
  * Methods of this class are not thread-safe and confined to the thread that opened the SQLite connection.
  *
  * @author Igor Sereda
  * @see SQLiteConnection#blob
  * @see <a href="http://www.sqlite.org/c3ref/blob_open.html">sqlite3_blob_open</a>
  */
final class SQLiteBlob private(

  /**
   * Controller, not null
   */
  private var myController: SQLiteController,

 /**
   * Handle, set to null when disposed
   */
  private var myHandle: SQLiteBlob.Handle,

  /**
    * Debug name
    */
  private var myName: String = "",

  /**
   * Whether blob was opened for writing
   */
  private val myWriteAccess: Boolean

) extends Logging {
  assert(myController != null, "myController is null")
  assert(myHandle != null, "myHandle is null")

  def this(
    controller: SQLiteController,
    handle: SQLiteBlob.Handle,
    dbname: String,
    table: String,
    column: String,
    rowid: Long,
    writeAccess: Boolean
  ) = {
    this(controller, handle, s"$dbname.$table.$column:$rowid", writeAccess)
  }

  def blobHandle: SQLiteBlob.Handle = myHandle

  @throws[SQLiteException]
  private def _getHandleOrFail(): SQLiteBlob.Handle = {
    val handle = myHandle
    if (handle == null)
      throw new SQLiteException(WRAPPER_BLOB_DISPOSED, "SQLiteBlob.Handle is null")

    handle
  }

  private var _blobLength: CInt = -1

  private def _retrieveBlobLength(): Unit = {
    myController.validate()
    _blobLength = sqlite.sqlite3_blob_bytes(_getHandleOrFail())
  }

  /**
    * Returns the size of the open blob. The size cannot be changed via this interface.
    * Note: the size is cached by SQLite4s.
    *
    * @return size of the blobs in bytes
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    */
  @throws[SQLiteException]
  def getSize(): Int = {
    if (_blobLength == -1) _retrieveBlobLength()
    _blobLength
  }

  /**
    * Disposes this blob and frees allocated resources.
    * <p>
    * After blob is disposed, it is no longer usable and holds no references to connection
    * or sqlite db.
    */
  def dispose(): Unit = {
    if (myHandle == null) return

    try
      myController.validate()
    catch {
      case e: SQLiteException =>
        SQLiteException.logWarnOrThrowError(msg => logger.warn(msg), "invalid dispose: " + e, true)
        return
    }
    logger.trace(mkLogMessage("disposing"))

    myController.dispose(this)

    // clear may be called from dispose() too
    clear()
  }

  /**
    * Checks if this instance has been disposed
    *
    * @return true if the blob is disposed and cannot be used
    */
  def isDisposed(): Boolean = myHandle == null

  /**
    * Read bytes from the blob into a buffer.
    * <p>
    * <code>blobOffset</code> and <code>length</code> should define a sub-range within blob's content. If attempt is
    * made to read blob beyond its size, an exception is thrown and no data is read.
    *
    * @param blobOffset the position in the blob where to start reading
    * @param buffer     target buffer
    * @param offset     starting offset in the buffer
    * @param length     number of bytes to read
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/blob_read.html">sqlite3_blob_read</a>
    */
  @throws[SQLiteException]
  def read(blobOffset: Int, buffer: Array[Byte], offset: Int, length: Int): Unit = {
    require(buffer != null, "buffer is null")

    if (offset < 0 || offset + length > buffer.length)
      throw new ArrayIndexOutOfBoundsException(s"buffer length=${buffer.length} offset=$offset length=$length")

    myController.validate()

    logger.trace(mkLogMessage(s"read[$blobOffset,$length]"))

    val rc = SQLiteWrapper.sqlite3BlobRead(_getHandleOrFail(), blobOffset, buffer, offset, length)
    myController.throwResult(rc, "read", this)
  }

  /**
    * Writes bytes into the blob. Bytes are taken from the specified range in the input byte buffer.
    * <p>
    * Note that you cannot write beyond the current blob's size. The size of the blob
    * cannot be changed via incremental I/O API. To change the size, you need to use {@link SQLiteStatement#bindZeroBlob}
    * method.
    * <p>
    * Bytes are written within the current transaction.
    * <p>
    * If blob was not open for writing, an error is thrown.
    *
    * @param blobOffset the position in the blob where to start writing
    * @param buffer     source bytes buffer
    * @param offset     starting offset in the buffer
    * @param length     number of bytes to write
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/blob_write.html">sqlite3_blob_write</a>
    */
  @throws[SQLiteException]
  def write(blobOffset: Int, buffer: Array[Byte], offset: Int, length: Int): Unit = {
    require(buffer != null, "buffer is null")

    if (offset < 0 || offset + length > buffer.length)
      throw new ArrayIndexOutOfBoundsException(s"${buffer.length} $offset $length")

    val bufferPtr = buffer.asInstanceOf[scala.scalanative.runtime.ByteArray].at(0).cast[Ptr[Byte]]
    val newBufferPtr = bufferPtr + offset

    myController.validate()

    logger.trace(mkLogMessage(s"write[$blobOffset,$length]"))

    val rc = sqlite.sqlite3_blob_write(_getHandleOrFail(), newBufferPtr, length, blobOffset)

    myController.throwResult(rc, "write", this)
  }

  /**
    * Returns true if this blob instance was opened for writing.
    *
    * @return true if { @link #write} is allowed
    */
  def isWriteAllowed: Boolean = myWriteAccess

  /**
    * Repositions BLOB to another row in the table. It should be quickier than closing the blob and opening another one.
    *
    * @param rowid row id to move to - it must exist and contain data
    * @throws SQLiteException if SQLite returns an error, or if the call violates the contract of this class
    * @see <a href="http://www.sqlite.org/c3ref/blob_reopen.html">sqlite3_blob_reopen</a>
    */
  @throws[SQLiteException]
  def reopen(rowid: Long): Unit = {
    myController.validate()

    logger.trace(mkLogMessage("reopen[" + rowid + "]"))

    val rc = sqlite.sqlite3_blob_reopen(_getHandleOrFail(), rowid)
    myController.throwResult(rc, "reopen", this)

    // If no error => reset size and update name with new rowid
    _blobLength = -1
    myName = myName.splitAt(myName.indexOf(':'))._1 + s":$rowid"
  }

  /**
    * Clear all data, disposing the blob. May be called by SQLiteConnection on close.
    */
  def clear(): Unit = {
    myHandle = null
    myController = SQLiteController.getDisposed(myController)
    logger.trace(mkLogMessage("cleared"))
  }

  // TODO: DBO => avoid to overriding toString and implement proper toErrorString() or toQualifiedName() method
  override def toString(): String = s"[$myName]$myController"
}