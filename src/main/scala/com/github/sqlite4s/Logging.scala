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

package com.github.sqlite4s

import scribe._
import scribe.filter._
import scribe.output.format.ASCIIOutputFormat

// Used to abstract Logging from used library
trait Logging extends scribe.Logging {
  @inline def canLogTrace: Boolean = LogLevel.TRACE <= Logging.MAX_LOG_LEVEL
  @inline def canLogDebug: Boolean = LogLevel.DEBUG <= Logging.MAX_LOG_LEVEL
  @inline def canLogInfo: Boolean = LogLevel.INFO <= Logging.MAX_LOG_LEVEL
  @inline def canLogWarn: Boolean = LogLevel.WARN <= Logging.MAX_LOG_LEVEL
  @inline def canLogError: Boolean = LogLevel.ERROR <= Logging.MAX_LOG_LEVEL
}

object Logging {

  type LogLevel = com.github.sqlite4s.LogLevel
  val LogLevel = com.github.sqlite4s.LogLevel

  private val namespace = "com.github.sqlite4s"
  private var MAX_LOG_LEVEL: LogLevel = LogLevel.DEBUG

  // configureLogger function for scribe (https://github.com/outr/scribe/wiki/getting-started)
  // should provide better performance than slogging
  // See: http://www.matthicks.com/2018/02/scribe-2-fastest-jvm-logger-in-world.html
  def configureLogger(maxLogLevel: Logging.LogLevel = LogLevel.DEBUG): Unit = {
    MAX_LOG_LEVEL = maxLogLevel

    // set log level, e.g. to DEBUG
    val scribeMaxLogLevelOpt = Option(maxLogLevel match {
      case LogLevel.OFF => null
      case LogLevel.ERROR => Level.Error
      case LogLevel.WARN => Level.Warn
      case LogLevel.INFO => Level.Info
      case LogLevel.DEBUG => Level.Debug
      case LogLevel.TRACE => Level.Trace
    })

    val logger = scribe.Logger.root.clearHandlers().clearModifiers().withHandler(
      minimumLevel = scribeMaxLogLevelOpt,
      outputFormat = ASCIIOutputFormat
    )

    if (scribeMaxLogLevelOpt.isDefined) {
      logger.replace()
    } else {
      logger.withModifier(
        select(packageName.startsWith(namespace))
          .exclude(level <= Level.Error)
          .priority(Priority.High)
      ).replace()
    }


  }


}