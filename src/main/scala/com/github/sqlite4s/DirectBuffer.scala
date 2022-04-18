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
import java.nio._

object DirectBuffer {
  private[sqlite4s] val CONTROL_BYTES = 2

  sealed trait IHandle
  type Handle = Array[Byte] with IHandle

  // This is a place-holder
  // FIXME: check if we need to free manually the Handle
  def disposeHandle(handle: Handle): Unit = {
    ()
  }

}

// FIXME: check there is no memory leak and memory access issues
final class DirectBuffer(
  private var myUnderlyingArray: DirectBuffer.Handle, // managed by SN GC
  private var myControlBuffer: ByteBuffer,
  private var myDataBuffer: ByteBufferWrapper,
  private val mySize: Int
) {
  require(
    mySize == myControlBuffer.limit() + myDataBuffer.capacity,
    s"sum of buffer sizes should be equal to mySize: $mySize != ${myControlBuffer.limit()} + ${myDataBuffer.capacity}"
  )
  require(
    myControlBuffer.limit() == DirectBuffer.CONTROL_BYTES,
    "invalid control buffer size: " + myControlBuffer.limit()
  )
  require(mySize > DirectBuffer.CONTROL_BYTES, s"mysSize should be greater than control buffer size: $mySize")

  @throws[IOException]
  def data(): ByteBufferWrapper = {
    if (!isValid()) throw new IOException("buffer disposed")
    myDataBuffer
  }

  def getCapacity(): Int = mySize - DirectBuffer.CONTROL_BYTES

  def getPosition(): Int = {
    val buffer = myDataBuffer
    if (buffer == null) 0
    else buffer.position // - DirectBuffer.CONTROL_BYTES // Note: was buffer.position in sqlite4java
  }

  def getHandle(): DirectBuffer.Handle = myUnderlyingArray

  def isValid(): Boolean = {
    val controlBuffer = myControlBuffer
    if (controlBuffer == null || myDataBuffer == null || myUnderlyingArray == null) return false

    val pendingRemove = controlBuffer.get(1)
    pendingRemove == 0
  }

  def isUsed(): Boolean = {
    val controlBuffer = myControlBuffer
    if (controlBuffer == null) return false

    controlBuffer.get(0) != 0
  }

  def incUsed(): Unit = {
    val controlBuffer = myControlBuffer
    if (controlBuffer != null) controlBuffer.put(0, (controlBuffer.get(0) + 1).asInstanceOf[Byte])
  }

  def decUsed(): Unit = {
    val controlBuffer = myControlBuffer
    if (controlBuffer != null) {
      val usage = controlBuffer.get(0)
      if (usage > 0) controlBuffer.put(0, (usage - 1).toByte)
    }
  }

  def invalidate(): Unit = {
    myControlBuffer = null
    myDataBuffer = null
    myUnderlyingArray = null
  }
}


// FIXME: this is a workaround to our current incapacitity to reproduce the sqlite4java DirectByteBuffer behavior (two buffers for a single backed array)
class ByteBufferWrapper(val byteBuffer: ByteBuffer, val offset: Int) {

  def capacity: Int = byteBuffer.capacity() - offset

  def position: Int = byteBuffer.position() - offset

  def remaining(): Int = byteBuffer.remaining() // limit - position

  def hasRemaining: Boolean = byteBuffer.hasRemaining // position < limit

  def isReadOnly: Boolean = byteBuffer.isReadOnly

  def hasArray: Boolean = byteBuffer.hasArray

  def array(): AnyRef = byteBuffer.array()

  def arrayOffset(): Int = byteBuffer.arrayOffset()

  def isDirect: Boolean = byteBuffer.isDirect

  def compact(): this.type = { byteBuffer.compact(); this }

  def duplicate(): ByteBufferWrapper = new ByteBufferWrapper(byteBuffer.duplicate(), offset)

  def clear(): this.type = {
    byteBuffer.clear()
    byteBuffer.position(offset)
    this
  }

  def flip(): this.type = {
    byteBuffer.flip()
    byteBuffer.position(offset)
    this
  }

  def rewind(): this.type = {
    byteBuffer.rewind()
    byteBuffer.position(offset)
    this
  }

  def slice(): ByteBufferWrapper = new ByteBufferWrapper(byteBuffer.slice(), offset)

  def get(): Byte = byteBuffer.get()

  def put(b: Byte): this.type = { byteBuffer.put(b); this }

  def get(index: Int): Byte = byteBuffer.get(index)

  def put(index: Int, b: Byte): this.type = { byteBuffer.put(index, b); this }

  // TODO: copy data after offset before calling asReadOnlyBuffer?
  def asReadOnlyBuffer(): ByteBufferWrapper = new ByteBufferWrapper(byteBuffer.asReadOnlyBuffer(), offset)

  def getChar: Char = byteBuffer.getChar()

  def putChar(value: Char): this.type = { byteBuffer.putChar(value); this }

  def getChar(index: Int): Char = {
    byteBuffer.getChar(index + offset)
  }

  def putChar(index: Int, value: Char): this.type = {
    //require(index > offset, "invalid index value")
    byteBuffer.putChar(index + offset, value)
    this
  }

  // FIXME: copy data after offset before calling asCharBuffer
  def asCharBuffer(): CharBuffer = byteBuffer.asCharBuffer()

  def getShort(): Short = byteBuffer.getShort()

  def putShort(value: Short): this.type = { byteBuffer.putShort(value); this }

  def getShort(index: Int): Short = byteBuffer.getShort(index + offset)

  def putShort(index: Int, value: Short): this.type = {
    //require(index > offset, "invalid index value")
    byteBuffer.putShort(index + offset, value)
    this
  }

  // FIXME: copy data after offset before calling asShortBuffer
  def asShortBuffer(): ShortBuffer = byteBuffer.asShortBuffer()

  def getInt(): Int = byteBuffer.getInt()

  def putInt(value: Int): this.type = { byteBuffer.putInt(value); this }

  def getInt(index: Int): Int = byteBuffer.getInt(index + offset)

  def putInt(index: Int, value: Int): this.type = {
    //require(index > offset, "invalid index value")
    byteBuffer.putInt(index + offset, value)
    this
  }

  // FIXME: copy data after offset before calling asIntBuffer
  def asIntBuffer(): IntBuffer = byteBuffer.asIntBuffer()

  def getLong(): Long = byteBuffer.getLong()

  def putLong(value: Long): this.type = { byteBuffer.putLong(value); this }

  def getLong(index: Int): Long = byteBuffer.getLong(index + offset)

  def putLong(index: Int, value: Long): this.type = {
    byteBuffer.putLong(index + offset, value)
    this
  }

  // FIXME: copy data after offset before calling asLongBuffer
  def asLongBuffer(): LongBuffer = byteBuffer.asLongBuffer()

  def getFloat: Float = byteBuffer.getFloat()

  def putFloat(value: Float): this.type = { byteBuffer.putFloat(value); this }

  def getFloat(index: Int): Float = byteBuffer.getFloat(index + offset)

  def putFloat(index: Int, value: Float): this.type = {
    byteBuffer.putFloat(index + offset, value)
    this
  }

  // FIXME: copy data after offset before calling asFloatBuffer
  def asFloatBuffer(): FloatBuffer = byteBuffer.asFloatBuffer()

  def getDouble: Double = byteBuffer.getDouble()

  def putDouble(value: Double): this.type = { byteBuffer.putDouble(value); this }

  def getDouble(index: Int): Double = byteBuffer.getDouble(index)

  def putDouble(index: Int, value: Double): this.type = {
    byteBuffer.putDouble(index + offset, value)
    this
  }

  // FIXME: copy data after offset before calling asDoubleBuffer
  def asDoubleBuffer(): DoubleBuffer = byteBuffer.asDoubleBuffer()

}