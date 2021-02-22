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

// Used to abstract Logging from used library
trait Logging extends scribe.Logging

object Logging {

  type LogLevel = com.github.sqlite4s.LogLevel
  val LogLevel = com.github.sqlite4s.LogLevel

  private val namespace = "com.github.sqlite4s"

  // configureLogger function for scribe (https://github.com/outr/scribe/wiki/getting-started)
  // should provide better performance than slogging
  // See: http://www.matthicks.com/2018/02/scribe-2-fastest-jvm-logger-in-world.html
  def configureLogger(minLogLevel: Logging.LogLevel = LogLevel.DEBUG): Unit = {

    // set log level, e.g. to DEBUG
    val scribeMinLogLevelOpt = Option(minLogLevel match {
      case LogLevel.OFF => null
      case LogLevel.ERROR => Level.Error
      case LogLevel.WARN => Level.Warn
      case LogLevel.INFO => Level.Info
      case LogLevel.DEBUG => Level.Debug
      case LogLevel.TRACE => Level.Trace
    })

    val logger = scribe.Logger.root.clearHandlers().clearModifiers().withHandler(
      minimumLevel = scribeMinLogLevelOpt
    )

    if (scribeMinLogLevelOpt.isDefined) {
      logger.replace()
    } else {
      logger.withModifier(
        select(packageName.startsWith(namespace))
          .exclude(level <= Level.Error)
          .priority(Priority.High)
      ).replace()
    }

    /*if (scribeMinLogLevelOpt.isDefined) {
      logger.withModifier(
        select(packageName.startsWith(namespace))
          .exclude(level < scribeMinLogLevelOpt.get)
          .priority(Priority.High)
      ).replace()
    } else {
      logger.withModifier(
        select(packageName.startsWith(namespace))
          .exclude(level <= Level.Error)
          .priority(Priority.High)
      ).replace()
    }*/

  }

  // old configureLogger function for slogging
  /*
  def configureLogger(logLevel: Logging.LogLevel = LogLevel.DEBUG): Unit = {

    // select logger backend, e.g. simple logging using println (supported by Scala/JVM, Scala.js, and Scala Native)
    LoggerConfig.factory = PrintLoggerFactory()

    // set log level, e.g. to DEBUG
    LoggerConfig.level = logLevel

    // use stderr for ERROR and WARN
    PrintLoggerFactory.errorStream = System.err
    PrintLoggerFactory.warnStream = System.err

    // use stdout for all other levels
    PrintLoggerFactory.infoStream = System.out
    PrintLoggerFactory.debugStream = System.out
    PrintLoggerFactory.traceStream = System.out

    /**
      * FilterLogger: filter function is called after the current value of LoggerConfig.level has been checked.
      * Hence, if you want to log TRACE statements for a specific source using FilterLogger,
      * you need to set FilterConfig.level = LogLevel.TRACE.
      * This also means that all TRACE logging statements in the code are executed,
      * even if they are subsequently discarded by NullLogger, which may have a serious impact on performance.
      */
    FilterLogger.filter = {
      // use NullLogger for all trace statements from sources starting with "com.github.sqlite4s"
      case (_,source) if source.startsWith(namespace) => NullLogger
      // log all other levels
      case _ => PrintLogger
    }
  }
   */
}