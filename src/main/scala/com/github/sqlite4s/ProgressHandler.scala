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

  def this(pointer: DirectBuffer.Handle, buffer: ByteBuffer, stepsPerCallback: Int) {
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