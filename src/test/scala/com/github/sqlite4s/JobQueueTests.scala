/*package com.github.sqlite4s

import java.io.File
import java.util.concurrent._

import utest._

object JobQueueTests extends SQLiteConnectionFixture {

  class TestQueue(databaseFile: File, threadFactory: ThreadFactory) extends SQLiteQueue(databaseFile, threadFactory) {

    def this() {
      this(null, Executors.defaultThreadFactory)
    }

    override def start(): TestQueue = super.start().asInstanceOf[TestQueue]

    /*def this(databaseFile: File, threadFactory: ThreadFactory) {
      this()
      super(databaseFile, threadFactory)
    }*/

    @throws[SQLiteException]
    override protected def openConnection(): SQLiteConnection = memDb().open()
  }

  class FileQueue() extends SQLiteQueue {
    override def start(): FileQueue = super.start().asInstanceOf[FileQueue]

    @throws[SQLiteException]
    override protected def openConnection(): SQLiteConnection = fileDb().open()

    @throws[SQLiteException]
    override protected def handleJobException(job: SQLiteJob[_], e: Throwable): Unit = throw new RuntimeException("fail!", e)

    override protected def isReincarnationPossible = true

    override protected def getReincarnationTimeout = 200
  }

  abstract private class TestJob[T] extends SQLiteJob[T] {

    final private[sqlite4s] val started = new CountDownLatch(1)
    final private[sqlite4s] val finished = new CountDownLatch(1)
    final private[sqlite4s] val errored = new CountDownLatch(1)
    final private[sqlite4s] val cancelled = new CountDownLatch(1)

    private[sqlite4s] var connection: SQLiteConnection = _
    private[sqlite4s] var result: T = _
    private[sqlite4s] var error: Throwable = _

    def testState(started: Boolean, finished: Boolean, error: Boolean, cancelled: Boolean): Unit = {
      assert(started == (this.started.getCount == 0))
      assert(finished == (this.finished.getCount == 0))
      assert(error == (this.errored.getCount == 0))
      assert(cancelled == (this.cancelled.getCount == 0))
      assert(cancelled == isCancelled)
      assert(finished == isDone)
      assert(error == (getError != null))
    }

    def testResult(result: T): Unit = {
      assert(result != null)
      assert(result == complete)
      // additional wait to let callbacks be called
      while (getQueue() != null) {}
      testState(true, true, false, false)
    }

    def testNoResult(started: Boolean, cancelled: Boolean, errorClass: Class[_]): Unit = {
      assert(complete == null)
      while (getQueue() != null) {}
      testState(started, true, errorClass != null, cancelled)
      if (errorClass != null) assert(errorClass == (if (error == null) null else error.getClass))
    }

    @throws[Throwable]
    override protected def jobStarted(connection: SQLiteConnection): Unit = {
      started.countDown()
      this.connection = connection
    }

    @throws[Throwable]
    override protected def jobFinished(result: T): Unit = {
      finished.countDown()
      this.result = result
    }

    @throws[Throwable]
    override protected def jobError(error: Throwable): Unit = {
      errored.countDown()
      this.error = error
    }

    @throws[Throwable]
    override protected def jobCancelled(): Unit = cancelled.countDown()
  }

  type BooleanJobType = java.lang.Boolean

  private class SimpleJob extends TestJob[BooleanJobType] {
    @throws[Throwable]
    override protected def job(connection: SQLiteConnection) = true
  }

  private class BarrierJob extends TestJob[BooleanJobType] {
    final private[sqlite4s] val barrier = new CyclicBarrier(2)

    def await(): Boolean = try {
      barrier.await(1000, TimeUnit.MILLISECONDS)
      true
    } catch {
      case e: BrokenBarrierException =>
        false
      case e: TimeoutException =>
        false
      case e: InterruptedException =>
        Thread.currentThread.interrupt()
        throw new java.lang.AssertionError()
    }

    @throws[Throwable]
    override protected def job(connection: SQLiteConnection): BooleanJobType = {
      if (!await) return null
      if (isCancelled) return null
      true
    }

    override def cancel(mayInterruptIfRunning: Boolean): Boolean = {
      val r = super.cancel(mayInterruptIfRunning)
      barrier.reset()
      r
    }
  }

  val tests = Tests {
    'testStartStop - testStartStop
    'testStartStopUnusual - testStartStopUnusual
    'testBadThreadFactory - testBadThreadFactory
    'testGracefulStop - testGracefulStop
    'testNonGracefulStop - testNonGracefulStop
    'testExecuteAfterStop - testExecuteAfterStop
    'testExecuteBeforeStart - testExecuteBeforeStart
    'testFlush - testFlush
    'testExecute - testExecute
    'testBasicOpen - testBasicOpen
    'testCancelRollback - testCancelRollback
    'testJobError - testJobError
    'testAbnormalStop - testAbnormalStop
    'testReincarnation - testReincarnation
  }

  private var myQueue: TestQueue = _

  @throws[Exception]
  override protected def setUp(): Unit = {
    super.setUp()
    myQueue = new TestQueue().start()
  }

  @throws[Exception]
  override protected def tearDown(): Unit = {
    myQueue.stop(false).join()
    super.tearDown()
  }

  def testStartStop(): Unit = {
    assert(!myQueue.isStopped)
    assert(myQueue.getDatabaseFile == null)
    myQueue.stop(false)
    assert(myQueue.isStopped())
  }

  @throws[InterruptedException]
  def testStartStopUnusual(): Unit = {
    myQueue.start()
    myQueue.start()
    myQueue.stop(true)
    myQueue.stop(false)
    myQueue.join()
    myQueue.join()
  }

  @throws[InterruptedException]
  def testBadThreadFactory(): Unit = {
    myQueue.stop(true).join()
    myQueue = new TestQueue(null, new ThreadFactory() {
      override def newThread(r: Runnable): Thread = null
    })

    intercept[RuntimeException] {
      myQueue.start()
      fail("started without thread?")
    }
  }

  def testGracefulStop(): Unit = {
    val job1 = myQueue.execute(new BarrierJob())
    val job2 = myQueue.execute(new BarrierJob())
    myQueue.stop(true)
    assert(job1.await())
    assert(job2.await())
    job1.testResult(true)
    job2.testResult(true)
  }

  def testNonGracefulStop(): Unit = {
    val job1 = myQueue.execute(new BarrierJob())
    val job2 = myQueue.execute(new BarrierJob())
    myQueue.stop(false)
    job1.await() // job1 may be executed if it is started before stop() is called - no check
    assert(!job2.await)
    job2.testNoResult(false, true, null)
  }

  @throws[InterruptedException]
  def testExecuteAfterStop(): Unit = {
    myQueue.stop(true)
    var job = myQueue.execute(new SimpleJob())
    job.testNoResult(false, true, null)
    myQueue.join()
    myQueue = new TestQueue().start()
    myQueue.stop(false).join()
    job = myQueue.execute(new SimpleJob())
    job.testNoResult(false, true, null)
  }

  @throws[InterruptedException]
  def testExecuteBeforeStart(): Unit = {
    myQueue.stop(true).join()
    myQueue = new TestQueue()
    val job = myQueue.execute(new SimpleJob())
    myQueue.start()
    job.testResult(true)
  }

  @throws[InterruptedException]
  def testFlush(): Unit = {
    var i = 0
    while (i < 100) {
      myQueue.execute(new SimpleJob())
      i += 1
    }

    val job = myQueue.execute(new SimpleJob())
    myQueue.flush()
    job.testState(true, true, false, false)
  }

  def testExecute(): Unit = {
    val isOk = myQueue.execute(new SQLiteJob[Boolean]() {
      @throws[Throwable]
      override protected def job(connection: SQLiteConnection): Boolean = connection.getAutoCommit()
    }).complete()
    assert(isOk)
  }

  @throws[InterruptedException]
  def testBasicOpen(): Unit = {
    val queue = new SQLiteQueue().start()
    assert(queue.execute(new SimpleJob()).complete())
    queue.stop(false).join()
  }

  def testCancelRollback(): Unit = {
    myQueue.execute(new SQLiteJob[AnyRef]() {
      @throws[Throwable]
      override protected def job(connection: SQLiteConnection): AnyRef = {
        connection.exec("create table x (x)")
        null
      }
    }).complete()

    val job = myQueue.execute(new BarrierJob() {
      @throws[Throwable]
      override protected def job(connection: SQLiteConnection): BooleanJobType = {
        connection.exec("begin")
        connection.exec("insert into x values (1)")
        super.job(connection)
      }
    })

    while (job.barrier.getNumberWaiting < 1) {}

    job.cancel(true)
    job.testNoResult(true, true, null)
    assert(0.asInstanceOf[Integer] == myQueue.execute(new SQLiteJob[Integer]() {
      @throws[Throwable]
      override protected def job(connection: SQLiteConnection): Integer = {
        val st = connection.prepare("select count(*) from x")
        st.step()
        val r = st.columnInt(0)
        st.dispose()
        r
      }
    }).complete())

    ()
  }

  def testJobError(): Unit = {
    myQueue.execute(new SimpleJob() {
      @throws[Throwable]
      override protected def job(connection: SQLiteConnection): BooleanJobType = {
        connection.exec("BEIGN")
        super.job(connection)
      }
    }
    ).testNoResult(true, false, classOf[SQLiteException])
  }

  @throws[InterruptedException]
  def testAbnormalStop(): Unit = {

    val hijackThread: Array[Thread] = Array(null)
    myQueue.execute(new SQLiteJob[AnyRef]() {
      @throws[Throwable]
      override protected def job(connection: SQLiteConnection): AnyRef = {
        hijackThread(0) = Thread.currentThread()
        null
      }
    }).complete()

    val job1 = myQueue.execute(new BarrierJob)
    val job2 = myQueue.execute(new BarrierJob)
    hijackThread(0).interrupt()
    job1.await()
    job2.await()
    job2.testNoResult(false, true, null)
    myQueue.join()

    assert(myQueue.isStopped())
  }

  @throws[InterruptedException]
  def testReincarnation(): Unit = {
    myQueue.stop(false).join()
    val q = new FileQueue().start()
    q.execute(new SQLiteJob[AnyRef]() {
      @throws[Throwable]
      override protected def job(connection: SQLiteConnection): AnyRef = {
        connection.exec("create table x (x)")
        null
      }
    }).complete()

    // jobs:
    // 1. barrier
    val barrier = q.execute(new BarrierJob())
    // 2. break queue
    q.execute(new SQLiteJob[AnyRef]() {
      @throws[Throwable]
      override protected def job(connection: SQLiteConnection): AnyRef = {
        connection.exec("begin")
        connection.exec("insert into x values (1)")
        connection.exec("WHOA")
        null
      }
    })

    // 3. normal job - should be executed after reincarnation
    val job = q.execute(new SQLiteJob[Integer]() {
      @throws[Throwable]
      override protected def job(connection: SQLiteConnection): Integer = {
        val st = connection.prepare("select count(*) from x")
        st.step()
        st.columnInt(0)
      }
    })

    barrier.await()
    Thread.sleep(100)
    assert(!q.isStopped)
    assert(0.asInstanceOf[Integer] == job.complete)
    q.stop(true).join()
  }

}*/