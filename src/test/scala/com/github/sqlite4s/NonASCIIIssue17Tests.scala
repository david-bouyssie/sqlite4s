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

import utest._

object NonASCIIIssue17Tests extends SQLiteConnectionFixture {

  private val C01 = "0000" + 55360.toChar + 56384.toChar

  val tests = Tests {
    "testBind" - testBind()
    "testConcatSimple" - testConcatSimple()
  }

  private var cnx: SQLiteConnection = _

  @throws[Exception]
  override def setUp(): Unit = {
    super.setUp()
    cnx = new SQLiteConnection()
    cnx.open()
  }

  override def tearDown(): Unit = {
    cnx.dispose()
    super.tearDown()
  }

  @throws[SQLiteException]
  def run(st: SQLiteStatement, expected: String): Unit = {
    st.step()
    val result = st.columnString(0)
    assert(expected == result)
  }

  @throws[SQLiteException]
  def testBind(): Unit = {
    val v = "select ?;"
    val st = cnx.prepare(v)
    st.bind(1, NonASCIIIssue17Tests.C01)
    run(st, NonASCIIIssue17Tests.C01)
  }

  @throws[SQLiteException]
  def testConcatSimple(): Unit = {
    val v = "select '" + NonASCIIIssue17Tests.C01 + "';"
    val st = cnx.prepare(v)
    run(st, NonASCIIIssue17Tests.C01)
  }

  @throws[SQLiteException]
  def testConcat(): Unit = {
    val RAND = SQLiteTestFixture.createRandom()
    Logging.configureLogger(Logging.LogLevel.OFF)

    val attempts = 50000

    var i = 0
    while (i < attempts) {
      var myVal = 1 + RAND.nextInt(0xd800)
      if (myVal >= 0xd800) myVal += 0x800
      if (myVal != '\'') {
        val s = "000" + myVal.toChar
        val st = cnx.prepare("select '" + s + "';", false)
        try
          run(st, s)
        catch {
          case ex: SQLiteException =>
            println(s"code char = $myVal")
            throw ex
        } finally st.dispose()
      }

      i += 1
    }

    while (i < attempts) {
      val valTop = 0xd800 + RAND.nextInt(0x0400)
      val valLow = 0xdc00 + RAND.nextInt(0x0400)
      val s = "000" + valTop.toChar + valLow.toChar
      val st = cnx.prepare("select '" + s + "';", false)
      try
        run(st, s)
      catch {
        case ex: SQLiteException =>
          println(s"code chars: $valTop $valLow")
          throw ex
      } finally st.dispose()

      i += 1
    }
  }

}
