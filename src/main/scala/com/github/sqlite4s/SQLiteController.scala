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

import java.io.IOException

import SQLITE_WRAPPER_ERROR_CODE._

/**
  * This interface is used as a strategy for SQLiteStatement lifecycle. Initially it is set by {@link SQLiteConnection#prepare}
  * method, and when statement is disposed the strategy is reset to the dummy implementation.
  *
  * @author Igor Sereda
  */
object SQLiteController {

  def getDisposed(controller: SQLiteController): SQLiteController = {
    if (controller.isInstanceOf[SQLiteController.Disposed]) return controller

    Disposed.INSTANCE
  }

  /**
    * A stub implementation that replaces connection-based implementation when statement is disposed.
    */
  private object Disposed {
    val INSTANCE = new SQLiteController.Disposed("")
  }

  private class Disposed private(val namePrefix: String) extends SQLiteController {
    final private var myName = namePrefix + "[D]"

    override def toString: String = myName

    @throws[SQLiteException]
    override def validate(): Unit = {
      throw new SQLiteException(WRAPPER_MISUSE, "statement is disposed")
    }

    @throws[SQLiteException]
    override def throwResult(resultCode: Int, message: String, additionalMessage: Any): Unit = {
    }

    override def dispose(statement: SQLiteStatement): Unit = {
    }

    override def dispose(blob: SQLiteBlob): Unit = {
    }

    /*override def dispose(array: SQLiteLongArray): Unit = {
    }*/

    override def getSQLiteWrapper(): SQLiteWrapper = { // must not come here anyway
      new SQLiteWrapper()
    }

    /*
    @throws[IOException]
    @throws[SQLiteException]
    override def allocateBuffer(sizeEstimate: Int) = throw new IOException

    override def freeBuffer(buffer: DirectBuffer): Unit = {
    }*/

    override def getProgressHandler(): ProgressHandler = ProgressHandler.DISPOSED
  }

}

sealed trait SQLiteController {
  /**
    * @throws SQLiteException if connection or statement cannot be used at this moment by the calling thread.
    */
  @throws[SQLiteException]
  def validate(): Unit

  /**
    * If result code (from sqlite operation) is not zero (SQLITE_OK), then retrieves additional error info
    * and throws verbose exception.
    */
  @throws[SQLiteException]
  def throwResult(resultCode: Int, message: String, additionalMessage: Any): Unit

  /**
    * Performs statement life-keeping on disposal. If the statement is cached, its handle is returned to the
    * connection's cache. If it is not cached, the statement handle is finalized.
    * <p>
    * Implementation may call {@link SQLiteStatement#clear()} during execution.
    *
    * @param statement statement that is about to be disposed
    */
  def dispose(statement: SQLiteStatement): Unit

  def dispose(blob: SQLiteBlob): Unit

  //---//def dispose(array: Nothing): Unit

  def getSQLiteWrapper(): SQLiteWrapper

  /*
  @throws[IOException]
  @throws[SQLiteException]
  def allocateBuffer(sizeEstimate: Int): DirectBuffer

  def freeBuffer(buffer: DirectBuffer): Unit*/

  @throws[SQLiteException]
  def getProgressHandler(): ProgressHandler
}

abstract private[sqlite4s] class BaseController extends SQLiteController {

  protected val connection: SQLiteConnection

  @throws[SQLiteException]
  def validate(): Unit = {
    assert(validateImpl())
  }

  @throws[SQLiteException]
  private def validateImpl(): Boolean = {
    connection.checkThread()
    connection.handle()
    true
  }

  @throws[SQLiteException]
  def throwResult(resultCode: Int, message: String, additionalMessage: Any): Unit = {
    connection.throwResult(resultCode, message, additionalMessage)
  }

  def dispose(blob: SQLiteBlob): Unit = {
    if (checkDispose(blob)) connection.finalizeBlob(blob)
  }

  protected def checkDispose(obj: AnyRef): Boolean = {
    try {
      connection.checkThread()
    } catch {
      case e: SQLiteException => {
        throw new Exception(s"disposing $obj from alien thread", e)
      }
    }

    true
  }

  def getSQLiteWrapper(): SQLiteWrapper = connection.getSQLiteWrapper()

  // note: for backward compatibility with sqlite4java
  def getSQLiteManual(): SQLiteWrapper = getSQLiteWrapper()

  /*@throws[IOException]
  @throws[SQLiteException]
  def allocateBuffer(sizeEstimate: Int): DirectBuffer = thisSQLiteConnection.allocateBuffer(sizeEstimate)

  def freeBuffer(buffer: DirectBuffer): Unit = {
    try
      thisSQLiteConnection.freeBuffer(buffer)
    catch {
      case e: SQLiteException =>
        Internal.logWarn(thisSQLiteConnection, e.toString)
    }
  }*/

  @throws[SQLiteException]
  def getProgressHandler(): ProgressHandler = connection.myProgressHandler
}

private[sqlite4s] class CachedController(val connection: SQLiteConnection) extends BaseController {

  def dispose(statement: SQLiteStatement): Unit = {
    if (checkDispose(statement)) connection.cacheStatementHandle(statement)
  }

  /*def dispose(array: SQLiteLongArray): Unit = {
    if (checkDispose(array)) connection.cacheArrayHandle(array)
  }*/

  override def toString(): String = connection.toString + "[C]"
}

private[sqlite4s] class UncachedController(val connection: SQLiteConnection) extends BaseController {

  def dispose(statement: SQLiteStatement): Unit = {
    if (checkDispose(statement)) connection.finalizeStatement(statement)
  }

  /*def dispose(array: SQLiteLongArray): Unit = {
    if (checkDispose(array)) connection.finalizeArray(array)
  }*/

  override def toString(): String = connection.toString + "[U]"
}