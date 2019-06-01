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

object ProfilerTests extends SQLiteConnectionFixture {

  val tests = Tests {
    'testProfiler - testProfiler
  }

  @throws[SQLiteException]
  def testProfiler(): Unit = {
    val connection = memDb().open()
    val profiler = connection.profile()

    connection.exec("BEGIN IMMEDIATE")
    connection.exec("CREATE TABLE test (id INT PRIMARY KEY)")

    var st = connection.prepare("INSERT INTO test (id) VALUES (?)")

    for (i <- 1 until 10) {
      st.reset(true)
      st.bind(1, i)
      st.step()
    }

    st.dispose()

    connection.exec("COMMIT")

    st = connection.prepare("SELECT id FROM test ORDER BY id DESC")
    while (st.step) st.columnLong(0)
    st.reset()
    //---//st.loadInts(0, new Array[Int](10), 0, 10)
    st.dispose()

    val p = connection.stopProfiling()
    assert(p == profiler)

    println("profiler output:")
    println(profiler.printReport())
  }

}