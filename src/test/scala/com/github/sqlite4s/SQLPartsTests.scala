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

object SQLPartsTests extends TestSuite {

  val tests = Tests {
    "testAppendParams" - testAppendParams()
  }

  def testAppendParams(): Unit = {
    println(createParams(0))
    assert(createParams(0) == "")
    assert(createParams(-1) == "")
    assert(createParams(1) == "?")
    assert(createParams(2) == "?,?")

    var i = 3
    while (i < 1000) {
      check(i)
      i += 1
    }
  }

  private def check(count: Int): Unit = {
    val params = createParams(count)
    val b = new StringBuilder

    var i = 0
    while (i < count) {
      if (i > 0) b.append(',')
      b.append('?')
      i += 1
    }
  }

  private def createParams(count: Int) = new SQLParts().appendParams(count).toString()
}
