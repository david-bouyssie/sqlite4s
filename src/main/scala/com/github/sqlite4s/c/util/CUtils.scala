package com.github.sqlite4s.c.util

import java.nio.charset.Charset

import scala.scalanative.native._
import scala.scalanative.native.string.strstr
import scala.scalanative.runtime.{ByteArray, DoubleArray, FloatArray}

object CUtils {

  // Alias string as libc
  // TODO: remove this alias when we upgrade the SN dependency
  val libc = string

  final val EMPTY_STRING = ""
  final val NULL_BYTE = 0.toByte //new java.lang.Character(0x0).charValue.toByte
  final val NULL_CHAR: CChar = NULL_BYTE
  final val ZERO_ASCII_CODE = 48

  def substr_idx(c_str: CString, c_sub_str: CString): Long = {
    val found_str = strstr(c_str, c_sub_str) // get the start pointer of substring
    if (found_str == null) return -1L

    val offset = found_str - c_str

    offset
  }

  @inline def strcpy(dest: CString, src: CString, length: CSize): Long = {
    libc.memcpy(dest, src, length)
    dest( length ) = NULL_CHAR
  }

  @inline def bytes2ByteArray(bytes: Ptr[Byte], len: CSize): Array[Byte] = {
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
    val dst = arr.at(0).cast[Ptr[Byte]]
    val src = doubles.cast[Ptr[Byte]]
    val size = sizeof[CDouble] * len

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
    val dst  = arr.at(0).cast[Ptr[Byte]]
    val src  = floats.cast[Ptr[Byte]]
    val size = sizeof[CFloat] * len

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
    if (len == 0) return EMPTY_STRING

    /*val bytes = new Array[Byte](len)

    var c = 0
    while (c < len) {
      bytes(c) = !(cstr + c)
      c += 1
    }*/
    val bytes = bytes2ByteArray(cstr, len)

    new String(bytes, charset)
  }

  def bytesToCString(bytes: Array[Byte])(implicit z: Zone): CString = {
    if (bytes == null) return null

    val nBytes = bytes.length
    val cstr = z.alloc(nBytes + 1)

    var c = 0
    while (c < nBytes) {
      !(cstr + c) = bytes(c)
      c += 1
    }
    !(cstr + c) = NULL_CHAR

    // TODO: find a way to cast bytes to a bytesPtr and then use CUtils.strcpy?
    /*val bytesPtr = bytes.cast[Ptr[Byte]]//.at(0)
    libc.memcpy(cstr, bytesPtr, nBytes)
    cstr(nBytes) = NULL_CHAR*/

    cstr
  }

  @inline
  def fromCString(cstr: CString): String = {
    fromCString(cstr, Charset.defaultCharset())
  }

  @inline
  def fromCString(cstr: CString, charset: Charset): String = {
    if (cstr == null) return null
    scala.scalanative.native.fromCString(cstr, charset)
  }

  @inline
  def toCString(str: String)(implicit z: Zone): CString = {
    toCString(str, Charset.defaultCharset())(z)
  }

  @inline
  def toCString(str: String, charset: Charset)(implicit z: Zone): CString = {
    if (str == null) return null
    scala.scalanative.native.toCString(str, charset)
  }

}

final class CStringWrapper(var c_str: CString, var length: CSize) {

  def this(c_str: CString) {
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

  @inline protected implicit def cint2typedValue(cint: CInt): Value = new NativeRichInt(cint).toUInt.asInstanceOf[Value]
  @inline protected implicit def cuint2typedValue(cuint: CUnsignedInt): Value = cuint.asInstanceOf[Value]

  @inline def Value(cint: CInt): Value = cint2typedValue(cint)

  def withName(valueName: String): Value
}