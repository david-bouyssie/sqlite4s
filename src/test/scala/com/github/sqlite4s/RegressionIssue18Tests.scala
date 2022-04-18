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

// see http://code.google.com/p/sqlite4java/issues/detail?id=18
object RegressionIssue18Tests extends SQLiteConnectionFixture {

  val tests = Tests {
    "testConcurrentDisposeCrash" - testConcurrentDisposeCrash()
  }

  private var disposeCalled = false

  @throws[SQLiteException]
  def testConcurrentDisposeCrash(): Unit = {
    Logging.configureLogger(Logging.LogLevel.ERROR)

    var i = 0
    while (i < 50) {
      // FIXME: implement me
      /*val c = new SQLiteConnection().open()
      disposeCalled = false
      new Thread() {
        override def run(): Unit = {
          try {
            Thread.sleep(50)
            c.dispose()
          } catch {
            case e: Throwable => {
              logger.error("Caught an error", e)
            }

          } finally disposeCalled = true
        }
      }.start()

      try {
        while (!disposeCalled) {
          // we need sufficiently long method
          c.createArray()
        }
      }
      catch {
        case e: Nothing =>

        // ok
      }*/

      i += 1
    }
  }
}