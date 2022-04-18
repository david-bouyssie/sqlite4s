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

import java.io._
import java.util._

import scala.collection.JavaConverters._

import bindings.SQLITE_CONSTANT._

/**
  * SQLiteProfiler measures and accumulates statistics for various SQLite methods. The statistics is then available
  * in a report form.
  * <p>
  * To start profiling, call {@link SQLiteConnection#profile} and get the profiler. After profiling is done, call
  * {@link SQLiteConnection#stopProfiling} and inspect the profiler's results.
  * <p>
  * This is pure Java-based profiling, not related to <code>sqlite3_profile</code> method.
  *
  * @author Igor Sereda
  * @see SQLiteConnection#profile
  * @see SQLiteConnection#stopProfiling
  */
object SQLiteProfiler {
  private val HEADER = "-----------------------------------------------------------------------------"

  /*private def unwrapArg(arg: Any): AnyRef = arg match {
    case x: scala.math.ScalaNumber => x.underlying
    case x              => x.asInstanceOf[AnyRef]
  }*/

// FIXME: use implementations based on java.util.Locale when available in SN javalib
  /*
  @inline
  private def formatStr(l: Locale, format: String, args: Any*) = {
    //java.lang.String.format(l, format, args map unwrapArg: _*)
    format.formatLocal(l, args: _*)
  }

  private def formatDuration(nanos: Long): String = {
    if (nanos > 1000000000L) formatStr(Locale.US, "%.1fs", nanos.toDouble / 1000000000.0)
    else if (nanos > 100000000L) formatStr(Locale.US, "%dms", nanos / 1000000L)
    else if (nanos > 10000000L) formatStr(Locale.US, "%.1fms", nanos.toDouble / 1000000.0)
    else if (nanos > 100000L) formatStr(Locale.US, "%.2fms", nanos.toDouble / 1000000.0)
    else formatStr(Locale.US, "%.2fµs", nanos.toDouble / 1000.0)
  }*/

  private def formatStr(format: String, args: Any*) = {
    //java.lang.String.format(l, format, args map unwrapArg: _*)
    format.format(args: _*)
  }

  private def formatDuration(nanos: Long): String = {
    if (nanos > 1000000000L) formatStr("%.1fs", nanos.toDouble / 1000000000.0)
    else if (nanos > 100000000L) formatStr("%dms", nanos / 1000000L)
    else if (nanos > 10000000L) formatStr("%.1fms", nanos.toDouble / 1000000.0)
    else if (nanos > 100000L) formatStr("%.2fms", nanos.toDouble / 1000000.0)
    else formatStr("%.2fµs", nanos.toDouble / 1000.0)
  }

  private class SQLStat(val mySQL: String) {
    final private val myStats = new collection.mutable.HashMap[String, Stat]()

    def getSQL(): String = mySQL

    def report(name: String, nfrom: Long, nto: Long): Unit = {
      var stat = myStats.get(name).orNull
      if (stat == null) {
        stat = new SQLiteProfiler.Stat
        myStats.put(name, stat)
      }
      stat.report(nfrom, nto)
    }

    def getTotalTime(): Long = {
      var total = 0L

      for (stat <- myStats.values) {
        total += stat.getTotalNanos()
      }

      total
    }

    def printReport(out: PrintWriter): Unit = {
      out.println(HEADER)
      out.println(mySQL)
      out.println(HEADER)
      val totalPrefix = "total time"
      var maxPrefix = totalPrefix.length

      for (s <- myStats.keySet) {
        maxPrefix = Math.max(maxPrefix, s.length)
      }
      val b = new java.lang.StringBuilder()
      addLeftColumn(b, totalPrefix, maxPrefix)
      b.append(formatDuration(getTotalTime()))
      out.println(b.toString)

      for ((key, stat) <- myStats) {
        b.setLength(0)
        addLeftColumn(b, key, maxPrefix)
        b.append("total:").append(formatDuration(stat.getTotalNanos())).append(' ')
        b.append("count:").append(stat.getTotalCount()).append(' ')
        b.append("min|avg|max:").append(formatDuration(stat.getMinNanos())).append('|').append(formatDuration(stat.getAvgNanos())).append('|').append(formatDuration(stat.getMaxNanos())).append(' ')
        b.append("freq:").append(stat.getFrequency())
        out.println(b.toString)
      }

      out.println()
    }

    private def addLeftColumn(b: java.lang.StringBuilder, name: String, maxPrefix: Int): Unit = {
      b.append("    ")
      b.append(name)
      var add = maxPrefix + 4 - b.length

      while (add > 0) {
        b.append(' ')
        add -= 1
      }

      b.append("   ")
    }
  }

  private class Stat {
    private var myTotalCount = 0
    private var myTotalNanos = 0L
    private var myMinNanos = -1L
    private var myMaxNanos = -1L
    private var myFirstTime = 0L
    private var myLastTime = 0L

    def report(nfrom: Long, nto: Long): Unit = {
      val duration = nto - nfrom
      if (duration < 0) return
      myTotalCount += 1
      myTotalNanos += duration
      if (myMinNanos < 0 || duration < myMinNanos) myMinNanos = duration
      if (myMaxNanos < 0 || duration > myMaxNanos) myMaxNanos = duration
      myLastTime = System.currentTimeMillis
      if (myFirstTime == 0) myFirstTime = myLastTime
    }

    def getTotalNanos(): Long = myTotalNanos

    def getTotalCount(): Int = myTotalCount

    def getMinNanos(): Long = myMinNanos

    def getAvgNanos(): Long = if (myTotalCount > 0) myTotalNanos / myTotalCount
    else 0

    def getMaxNanos(): Long = myMaxNanos

    def getFrequency(): String = {
      if (myTotalCount < 10) return "-"
      val millis = myLastTime - myFirstTime
      val t = millis / myTotalCount
      if (t == 0) return "-"
      "1/" + formatDuration(t * 1000000L)
    }
  }

}

class SQLiteProfiler extends Logging {

  final private val myStats = new collection.mutable.HashMap[String, SQLiteProfiler.SQLStat]()

  /**
    * Outputs current report into PrintWriter.
    *
    * @param out report writer
    */
  def printReport(out: PrintWriter): Unit = {
    myStats.values.toList.sortBy(_.getTotalTime()).foreach { stat =>
      stat.printReport(out)
    }
  }

  /**
    * Returns current report as a String.
    *
    * @return current report
    */
  def printReport(): String = {
    val sw = new StringWriter()
    printReport(new PrintWriter(sw))
    sw.toString
  }

  /**
    * Prints report to a file. If IOException occurs, write warning log message, but does not throw it on the caller.
    *
    * @param file target file
    */
  def printReport(file: File): Unit = {
    var fos: FileOutputStream = null
    try {
      fos = new FileOutputStream(file)
      val writer = new PrintWriter(new OutputStreamWriter(fos))
      printReport(writer)
      writer.close()
    } catch {
      case e: IOException =>
        logger.warn(Internal.mkLogMessage(e.toString))
    } finally {
      if (fos != null) try
        fos.close()
      catch {
        // ignore
        case e: IOException => ()
      }
    }
  }

  private[sqlite4s] def reportExec(sql: String, nfrom: Long, nto: Long, rc: Int): Unit = {
    getStat(sql).report(if (rc == 0) "exec"
    else "exec:error(" + rc + ")", nfrom, nto)
  }

  private[sqlite4s] def reportPrepare(sql: String, nfrom: Long, nto: Long, rc: Int): Unit = {
    getStat(sql).report(if (rc == 0) "prepare"
    else "prepare:error(" + rc + ")", nfrom, nto)
  }

  private[sqlite4s] def reportStep(alreadyStepped: Boolean, sql: String, nfrom: Long, nto: Long, rc: Int): Unit = {
    val stat = getStat(sql)
    if (rc != SQLITE_ROW && rc != SQLITE_DONE) {
      stat.report("step:error(" + rc + ")", nfrom, nto)
      return
    }
    stat.report("step", nfrom, nto)
    if (alreadyStepped || rc == SQLITE_ROW) stat.report(if (alreadyStepped) "step:next"
    else "step:first", nfrom, nto)
  }

  private[sqlite4s] def reportLoadInts(alreadyStepped: Boolean, sql: String, nfrom: Long, nto: Long, rc: Int, count: Int): Unit = {
    val stat = getStat(sql)
    if (rc != SQLITE_ROW && rc != SQLITE_DONE) {
      stat.report("loadInts:error(" + rc + ")", nfrom, nto)
      return
    }
    stat.report("loadInts", nfrom, nto)
    if (alreadyStepped || rc == SQLITE_ROW) stat.report(if (alreadyStepped) "loadInts:next"
    else "loadInts:first", nfrom, nto)
    // todo count
  }

  private[sqlite4s] def reportLoadLongs(alreadyStepped: Boolean, sql: String, nfrom: Long, nto: Long, rc: Int, count: Int): Unit = {
    val stat = getStat(sql)

    if (rc != SQLITE_ROW && rc != SQLITE_DONE) {
      stat.report(s"loadLongs:error($rc)", nfrom, nto)
      return
    }

    stat.report("loadLongs", nfrom, nto)
    if (alreadyStepped || rc == SQLITE_ROW) {
      stat.report(
        if (alreadyStepped) "loadLongs:next" else "loadLongs:first",
        nfrom,
        nto
      )
    }
  }

  private def getStat(sql: String): SQLiteProfiler.SQLStat = {
    myStats.getOrElseUpdate(sql, new SQLiteProfiler.SQLStat(sql))
  }
}

