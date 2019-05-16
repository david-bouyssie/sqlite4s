package com.github.sqlite4s

private[this] object Internal {

  private final val LOG_PREFIX = "[sqlite4s] "

  type Logger = slogging.Logger

  def mkLogMessage(message: String): String = {
    mkLogMessage(null, message)
  }

  def mkLogMessage(source: String, message: String): String = {
    val builder = new StringBuilder(LOG_PREFIX)
    if (source != null) builder.append(source)

    /*if (source != null) {
      if (source.isInstanceOf[Class[_]]) {
        val className = source.asInstanceOf[Class[_]].getName
        builder.append(className.substring(className.lastIndexOf('.') + 1))
      }
      else builder.append(source)
      builder.append(": ")
    }*/
    if (message != null) builder.append(message)

    builder.toString
  }
}

