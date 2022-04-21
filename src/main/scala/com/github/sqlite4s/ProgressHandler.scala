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

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.LongBuffer

object ProgressHandler {
  val DISPOSED = new ProgressHandler()
  private val OFFSET_STEPCOUNT = 1
  private val OFFSET_CANCEL = 0
}

class ProgressHandler private() {

  final private var myStepsPerCallback = 0
  private var myPointer: DirectBuffer.Handle = _
  private var myBuffer: ByteBuffer = _
  private var myLongs: LongBuffer = _

  def this(pointer: DirectBuffer.Handle, buffer: ByteBuffer, stepsPerCallback: Int) = {
    this()
    myStepsPerCallback = stepsPerCallback
    assert(buffer.isDirect)
    assert(buffer.capacity == 16, buffer.capacity)
    myPointer = pointer
    myBuffer = buffer
    myLongs = buffer.order(ByteOrder.nativeOrder).asLongBuffer
    assert(myLongs.capacity == 2)
  }

  def dispose(): DirectBuffer.Handle = synchronized {
    val ptr = myPointer
    myBuffer = null
    myPointer = null
    myLongs = null
    ptr
  }

  def reset(): Unit = synchronized {
    if (myLongs == null) return
    myLongs.put(ProgressHandler.OFFSET_CANCEL, 0L)
    myLongs.put(ProgressHandler.OFFSET_STEPCOUNT, 0L)
  }

  def cancel(): Unit = synchronized {
    if (myLongs == null) return
    myLongs.put(ProgressHandler.OFFSET_CANCEL, 1L)
  }

  def getSteps: Long = synchronized {
    if (myLongs == null) return -1
    myLongs.get(ProgressHandler.OFFSET_STEPCOUNT) * myStepsPerCallback
  }
}