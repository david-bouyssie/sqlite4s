package com.github.sqlite4s

import utest._

// see http://code.google.com/p/sqlite4java/issues/detail?id=18
object RegressionIssue18Tests extends SQLiteConnectionFixture {

  val tests = Tests {
    'testConcurrentDisposeCrash - testConcurrentDisposeCrash
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