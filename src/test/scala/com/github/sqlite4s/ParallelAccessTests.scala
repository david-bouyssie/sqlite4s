/*package com.github.sqlite4s

// FIXME: this test requires non implemented Java classes
//[error] cannot link: @java.lang.Thread::init
//[error] cannot link: @java.lang.Thread::isAlive_bool
//[error] cannot link: @java.lang.Thread::join_unit
//[error] cannot link: @java.lang.Thread::start_unit
//[error] cannot link: @java.util.concurrent.Semaphore

import java.io.File
import java.util
import java.util.concurrent.{Semaphore, TimeUnit}

import utest._

object ParallelAccessTests extends SQLiteConnectionFixture {

  private var t1: TestThread = _
  private var t2: TestThread = _

  @throws[Exception]
  override protected def setUp(): Unit = {
    super.setUp()

    t1 = new TestThread()
    t2 = new TestThread()
    t1.exec("create table x (x)")
    t1.exec("insert into x values (1);")
    t1.exec("insert into x values (2);")
    t1.exec("insert into x values (3);")
  }

  @throws[Exception]
  override protected def tearDown(): Unit = {
    val e1 = t1.finish()
    if (e1 != null) e1.printStackTrace()
    val e2 = t2.finish()
    if (e2 != null) e2.printStackTrace()
    if (e1 != null) throw e1
    if (e2 != null) throw e2

    super.tearDown()
  }

  val tests = Tests {
    'testParallelReads - testParallelReads
    'testWriteWhileReadInProgress - testWriteWhileReadInProgress
  }

  @throws[Exception]
  def testParallelReads(): Unit = {
    val st1 = t1.prepare("select x from x order by x")
    val st2 = t2.prepare("select x from x order by x")
    var b1 = false
    var b2 = false
    b1 = t1.step(st1)
    assert(b1)
    b1 = t1.step(st1)
    assert(b1)
    b2 = t2.step(st2)
    assert(b2)
    b1 = t1.step(st1)
    assert(b1)
    b2 = t2.step(st2)
    assert(b2)
    b1 = t1.step(st1)
    assert(!b1)
  }

  @throws[Exception]
  def testWriteWhileReadInProgress(): Unit = {
    if (true) return
    val st1 = t1.prepare("select x from x order by x")
    assert(t1.step(st1))
    t2.exec("begin immediate")
    assert(t1.step(st1))
    assert(t1.step(st1))
    t2.exec("insert into x values (4)")
    t2.exec("commit")
    assert(!t1.step(st1))
  }

  trait DBRunnable {
    @throws[SQLiteException]
    def dbrun(): Unit
  }

  private class TestThread() extends Thread {

    private var myException: Exception = _
    private var myConnection: SQLiteConnection = _
    private val myQueue = new util.ArrayList[DBRunnable]

    start()

    override def run(): Unit = {
      try {
        myConnection = new SQLiteConnection(new File(tempName("db")))
        myConnection.open()

        var hasFinished = false
        while (!hasFinished) {
          var r: DBRunnable = null
          val isQueueEmpty: Boolean = synchronized {
            if (myQueue.isEmpty) {
              wait(500)
              true
            }
            else {
              r = myQueue.remove(0)
              false
            }
          }

          if (!isQueueEmpty) {
            if (r == null)
              hasFinished = true
            else
              r.dbrun()
          }
        }
      } catch {
          case e: Exception =>
            myException = e
        }
      finally {
        try
          myConnection.dispose()
        catch {
          case e: Exception => {}
        }
      }
    }

    @throws[SQLiteException]
    @throws[InterruptedException]
    def exec(sql: String): Unit = {
      perform(true, new DBRunnable() {
        @throws[SQLiteException]
        def dbrun(): Unit = {
          myConnection.exec(sql)
        }
      }
      )
    }

    @throws[InterruptedException]
    @throws[SQLiteException]
    private def perform(wait: Boolean, runnable: DBRunnable): Unit = {
      if (!isAlive) return

      if (this == Thread.currentThread) {
        runnable.dbrun()
        return
      }

      var r = runnable
      var p: Semaphore = null
      if (wait) {
        val sp = new Semaphore(1)
        sp.acquire()

        r = new DBRunnable() {
          @throws[SQLiteException]
          def dbrun(): Unit = {
            try runnable.dbrun()
            finally sp.release()
          }
        }

        p = sp
      }

      this synchronized myQueue.add(r)
      this.notify()

      if (p != null) {
        while (!p.tryAcquire(500, TimeUnit.MILLISECONDS)) {
          if (!this.isAlive)
            return
        }
      }
    }

    @throws[InterruptedException]
    def finish(): Exception = {
      this synchronized myQueue.add(null)
      this.notify()
      this.join()

      myException
    }

    @throws[SQLiteException]
    @throws[InterruptedException]
    def prepare(sql: String): SQLiteStatement = {
      //val result = Array.fill[SQLiteStatement](1)(null)
      var stmt: SQLiteStatement = null

      perform(true, new DBRunnable() {
        @throws[SQLiteException]
        def dbrun(): Unit = {
          stmt = myConnection.prepare(sql)
        }
      })

      //result(0)
      stmt
    }

    @throws[SQLiteException]
    @throws[InterruptedException]
    def step(statement: SQLiteStatement): Boolean = {
      val result = Array(false)
      perform(true, new DBRunnable() {
        @throws[SQLiteException]
        def dbrun(): Unit = {
          result(0) = statement.step()
        }
      })
      result(0)
    }
  }

}
*/