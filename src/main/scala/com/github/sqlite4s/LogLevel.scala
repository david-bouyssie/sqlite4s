package com.github.sqlite4s

sealed abstract class LogLevel {
  def value: Int
  @inline final def >=(other: LogLevel): Boolean = this.value >= other.value
  @inline final def <=(other: LogLevel): Boolean = this.value <= other.value
}

object LogLevel  {
  case object OFF   extends LogLevel { val value = 0 }
  case object ERROR extends LogLevel { val value = 1 }
  case object WARN  extends LogLevel { val value = 2 }
  case object INFO  extends LogLevel { val value = 3 }
  case object DEBUG extends LogLevel { val value = 4 }
  case object TRACE extends LogLevel { val value = 5 }
}