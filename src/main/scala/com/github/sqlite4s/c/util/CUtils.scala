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

package com.github.sqlite4s.c.util

import java.nio.charset.Charset

import scala.language.implicitConversions
import scala.scalanative.runtime.{Array => CArray, _}
import scala.scalanative.libc._
import scala.scalanative.libc.string.strstr
import scala.scalanative.unsafe._
import scala.scalanative.unsigned._

object CUtils {

  // Alias string as libc
  // TODO: remove this alias when we upgrade the SN dependency
  private val libc = string

  final val EMPTY_STRING = ""
  final val NULL_BYTE = 0.toByte //new java.lang.Character(0x0).charValue.toByte
  final val NULL_CHAR: CChar = NULL_BYTE
  final val ZERO_ASCII_CODE = 48
  private val ZERO_CSIZE: CSize = 0L.toULong

  def substr_idx(c_str: CString, c_sub_str: CString): Long = {
    val found_str = strstr(c_str, c_sub_str) // get the start pointer of substring
    if (found_str == null) return -1L

    val offset = found_str - c_str

    offset
  }

  @inline def strcpy(dest: CString, src: CString, length: CSize): CString = {
    libc.memcpy(dest, src, length)
    dest( length ) = NULL_CHAR
    dest
  }

  @inline def bytes2ByteArray(bytes: Ptr[Byte], len: CSize): Array[Byte] = {
    if (len == ZERO_CSIZE) return Array()

    val byteArray = ByteArray.alloc(len.toInt) // allocate memory
    val byteArrayPtr = byteArray.at(0)

    libc.memcpy(byteArrayPtr, bytes, len)

    /*var i = 0
    while (i < len) {
      byteArray(i) = !(bytes + i)
      i += 1
    }*/

    byteArray.asInstanceOf[Array[Byte]]
  }

  def doublePtr2DoubleArray(doubles: Ptr[CDouble], len: Int): Array[Double] = {
    val arr = DoubleArray.alloc(len)
    val dst = arr.at(0).asInstanceOf[Ptr[Byte]]
    val src = doubles.asInstanceOf[Ptr[Byte]]
    val size = sizeof[CDouble] * len.toULong

    libc.memcpy(dst, src, size)

    arr.asInstanceOf[Array[Double]]
  }

    /*val arrinfo = typeof[DoubleArray].cast[Ptr[ClassType]]
    val arrsize = 16 + 4 * len
    val arr     = GC.alloc_atomic(arrinfo, arrsize)
    val dst     = arr.cast[Ptr[Byte]]
    val src     = doubles.cast[Ptr[Byte]]

    libc.memcpy(dst, src, arrsize)

    arr.cast[DoubleArray]*/

    /*
        storeInt(elemRawPtr(arr, 8), length)

    storeInt(elemRawPtr(arr, 12), 4.toInt)
     */

    /*val doubleArray = new Array[Double](len)

    var i = 0
    while (i < len) {
      doubleArray(i) = !(doubles + i)
      i += 1
    }

    doubleArray*/

  def floatPtr2FloatArray(floats: Ptr[CFloat], len: Int): Array[Float] = {
    val arr  = FloatArray.alloc(len)
    val dst  = arr.at(0).asInstanceOf[Ptr[Byte]]
    val src  = floats.asInstanceOf[Ptr[Byte]]
    val size = sizeof[CFloat] * len.toULong

    libc.memcpy(dst, src, size)

    arr.asInstanceOf[Array[Float]]
  }

  /*@inline def str2String(src: CString, length: CSize): String = {
    val str2: CString = stackalloc[CChar](length + 1)
    strcpy(str2, src, length)
    str2( length ) = NULL_CHAR
    fromCString(str2)
  }*/

  def fromCString(cstr: CString, len: CSize, charset: Charset = Charset.defaultCharset()): String = {
    if (cstr == null) return null
    if (len == ZERO_CSIZE) return EMPTY_STRING

    /*val bytes = new Array[Byte](len)

    var c = 0
    while (c < len) {
      bytes(c) = !(cstr + c)
      c += 1
    }*/
    val bytes = bytes2ByteArray(cstr, len)

    new java.lang.String(bytes, charset)
  }

  def bytesToCString(bytes: Array[Byte])(implicit z: Zone): CString = {
    if (bytes == null) return null

    val nBytes = bytes.length
    val cstr = z.alloc( (nBytes + 1).toULong )

    /*var c = 0
    while (c < nBytes) {
      cstr.update(c, bytes(c))
      c += 1
    }
    cstr.update(c, NULL_CHAR)*/

    val bytesPtr = bytes.asInstanceOf[ByteArray].at(0)
    libc.memcpy(cstr, bytesPtr, nBytes.toULong)
    cstr(nBytes) = NULL_CHAR

    cstr
  }

  @inline
  def fromCString(cstr: CString): String = {
    fromCString(cstr, Charset.defaultCharset())
  }

  @inline
  def fromCString(cstr: CString, charset: Charset): String = {
    if (cstr == null) return null
    scala.scalanative.unsafe.fromCString(cstr, charset)
  }

  @inline
  def toCString(str: String)(implicit z: Zone): CString = {
    toCString(str, Charset.defaultCharset())(z)
  }

  @inline
  def toCString(str: String, charset: Charset)(implicit z: Zone): CString = {
    if (str == null) return null
    scala.scalanative.unsafe.toCString(str, charset)
  }

}

final class CStringWrapper(var c_str: CString, var length: CSize) {

  def this(c_str: CString) = {
    this(c_str, string.strlen(c_str))
  }

  override def toString: String = fromCString(c_str) // TODO: call fromCString(cstr: CString, len: CSize) instead, should be faster
}

final case class PtrBox(ptr: Ptr[Byte], length: CSize)

// TODO: move these traits to CUtils
trait CIntEnum {
  sealed trait _value_type
  type Value = CInt with _value_type

  @inline protected implicit def cint2typedValue(cint: CInt): Value = cint.asInstanceOf[Value]
  @inline def Value(cint: CInt): Value = cint2typedValue(cint)

  def withName(valueName: String): Value
}

trait CUIntEnum {
  sealed trait _value_type
  type Value = CUnsignedInt with _value_type

  @inline protected implicit def cint2typedValue(cint: CInt): Value = new UnsignedRichInt(cint).toUInt.asInstanceOf[Value]
  @inline protected implicit def cuint2typedValue(cuint: CUnsignedInt): Value = cuint.asInstanceOf[Value]

  @inline def Value(cint: CInt): Value = cint2typedValue(cint)

  def withName(valueName: String): Value
}