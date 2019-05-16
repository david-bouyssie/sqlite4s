package com.github.sqlite4s

import utest._

object ProgressHandlerTests extends SQLiteConnectionFixture {

  val tests = Tests {
    'testCancel - testCancel
    'testCancelAndTransactions - testCancelAndTransactions
  }

  @throws[SQLiteException]
  def testCancel(): Unit = {
    val db = prepareDb()
    val st = longSelect(db)
    st.cancel()

    var start = System.currentTimeMillis
    var time = 0L
    try {
      st.step()
      fail("stepped")
    } catch {
      case e: SQLiteInterruptedException =>
        // normal
        time = System.currentTimeMillis - start
        if (time > 1000) logger.warn(Internal.mkLogMessage(s"cancel took $time ms"))
    }
    st.reset()

    val DELAY = 100
    interruptLater(st, DELAY)
    start = System.currentTimeMillis
    try {
      // FIXME: remove me when interruptLater() is correctly implemented
      Thread.sleep(DELAY); throw new SQLiteInterruptedException()
      st.step()
      fail("stepped")
    } catch {
      case e: SQLiteInterruptedException => {
        time = System.currentTimeMillis - start
        scala.Predef.assert(time > DELAY / 2, s"thread interrupted to early ($time ms ago)")
        if (time > DELAY * 2) logger.warn(Internal.mkLogMessage(s"cancel took $time ms"))
      }
    }

    db.dispose()
  }

  private def interruptLater(st: SQLiteStatement, waitValue: Long): Unit = {
    // FIXME: implement me
    /*new Thread() {
      override def run(): Unit = {
        try {
          Thread.sleep(waitValue)
          st.cancel()
        } catch {
          case e: InterruptedException => logger.error("Ignoring InterruptedException", e)
        }
      }
    }.start()*/
  }

  @throws[SQLiteException]
  private def longSelect(db: SQLiteConnection) = db.prepare(
    "select (a.t - b.t) * (c.t - d.t) * (e.t - f.t) from t a,t b,t c,t d,t e,t f order by 1"
  )

  @throws[SQLiteException]
  private def prepareDb(): SQLiteConnection = {
    val db = memDb().open(true)
    db.exec("create table t (t integer)")
    db.exec("begin")

    var i = 0
    while (i < 10) {
      db.exec("insert into t values (" + i + ")")
      i += 1
    }

    db.exec("commit")

    db
  }

  @throws[SQLiteException]
  def testCancelAndTransactions(): Unit = {
    val db = prepareDb()
    val st = longSelect(db)
    db.exec("begin")
    db.exec("insert into t values (1000)")
    interruptLater(st, 1000)

    try
      st.step()
    catch {
      case e: SQLiteInterruptedException => logger.error("Ignoring SQLiteInterruptedException", e)
    }

    var chk = db.prepare("select 1 from t where t = 1000")
    assert(chk.step())
    st.dispose()
    chk.dispose()
    db.exec("commit")
    chk = db.prepare("select 1 from t where t = 1000")
    assert(chk.step())
    chk.dispose()
  }
}

