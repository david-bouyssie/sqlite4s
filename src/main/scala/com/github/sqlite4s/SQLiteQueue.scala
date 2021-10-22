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

import java.io.File
import java.util._
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

/**
  * SQLiteQueue is a basic implementation of job queue for an SQLite connection. It provides multi-threaded
  * or GUI application with asynchronous execution of database tasks in a single separate thread with a single
  * {@link SQLiteConnection}.
  * <p/>
  * The queue is started and stopped using {@link #start} and {@link #stop} methods correspondingly.
  * Each database task is represented by a subclass of {@link SQLiteJob}. A task is scheduled for execution
  * by {@link #execute} method. Tasks are served on first-come, first-serve basis.
  * <p/>
  * Public methods of SQLiteQueue are <strong>thread-safe</strong>, unless noted otherwise.
  * </p>
  * When writing tasks, it's a good practice to keep transaction boundaries within single task. That is, if you
  * BEGIN TRANSACTION in the task, make sure you COMMIT or ROLLBACK in the end. Otherwise, your transaction will
  * remain unfinished, locks held, and you possible wouldn't know which job will execute next in the context of
  * this unfinished transaction.
  * <p/>
  * SQLiteQueue may be subclassed in order to change certain behavior. If you need some things to be done
  * differently, look for a protected method to override. For example, you can implement a priority queue
  * instead of FIFO queue.
  * <p/>
  * SQLiteQueue and SQLiteJob are written to handle exceptions and errors in a controlled way. In particular,
  * if the queue thread terminates abnormally, SQLiteQueue will try to "reincarnate" by starting another thread
  * and opening another connection to the database. All queued tasks (except for the one that caused the problem)
  * should survive the reincarnation and execute in the new thread.
  * <p/>
  * Reincarnation is not possible for in-memory database, since the database is lost after connection closes.
  * <p/>
  * Some examples:
  * <pre>
  * void start() {
  * myQueue = new SQLiteQueue(myDatabaseFile);
  *   myQueue.start();
  * }
  * <p/>
  * int getTableRowCount(final String tableName) {
  * return myQueue.execute(new SQLiteJob&lt;Integer&gt;() {
  * protected Integer job(SQLiteConnection connection) throws SQLiteException {
  * SQLiteStatement st = connection.prepare("SELECT COUNT(*) FROM " + tableName);
  * try {
  *         st.step();
  * return st.columnInt(0);
  * } finally {
  *         st.dispose();
  * }
  * }
  * }).complete();
  * }
  * </pre>
  *
  * @author Igor Sereda
  * @see SQLiteJob
  */
object SQLiteQueue {
  /**
    * Default timeout for reincarnating database thread.
    */
  val DEFAULT_REINCARNATE_TIMEOUT = 3000
}

/**
  * Constructs the queue and allows to specify a factory for the queue thread.
  *
  * @param myDatabaseFile  database file to connect to, or null to open an in-memory database
  * @param myThreadFactory the factory for thread(s), cannot be null
  */
class SQLiteQueue(
  /**
  * Database file to open. If null, memory database is used.
  */
  val myDatabaseFile: File,

  /**
    * Used to create queue thread and reincarnator thread.
    */
  val myThreadFactory: ThreadFactory
) extends Logging {
  require(myThreadFactory != null, "myThreadFactory is null")

  /**
    * Currently running queue thread.
    */
  @volatile
  private var myThread: Thread = _
  /**
    * Lock for protecting the following fields.
    */
  final private val myLock = new Object()
  /**
    * Stores queued jobs. <p/><i>protected by myLock</i>
    */
  protected var myJobs: java.util.Collection[SQLiteJob[_]] = _
  /**
    * If true, queue stop has been requested (or implied). <p/><i>protected by myLock</i>
    */
  private var myStopRequested = false
  /**
    * If true, non-gracious stop has been required by the user. Bears no sense if {@link #myStopRequested} is false.
    * <p/><i>protected by myLock</i>
    */
  private var myStopRequired = false
  /**
    * The job currently being executed. <p/><i>protected by myLock</i>
    */
  private var myCurrentJob: SQLiteJob[_] = _
  /**
    * Our running connection. May be null when connection is not yet created or when it's closed. <p/><i>confined to myThread</i>
    */
  private var myConnection: SQLiteConnection = _

  /**
    * Constructs the queue. SQLiteQueue will use {@link SQLiteConnection#open} method to create a connection within
    * queue thread.
    * <p/>
    * The queue must be started in order for jobs to be executed.
    *
    * @param databaseFile database file to connect to, or null to open an in-memory database
    * @see #start
    */
  def this(databaseFile: File) {
    this(databaseFile, Executors.defaultThreadFactory)
  }

  /**
    * Constructs the queue, which will use an in-memory database.
    * <p/>
    * The queue must be started in order for jobs to be executed.
    *
    * @see #start
    */
  def this() {
    this(null)
  }

  override def toString(): String = s"SQLiteQueue[${Option(myDatabaseFile.getName).getOrElse("")}]"

  /**
    * Get the underlying database file.
    *
    * @return database file or null if queue is working on an in-memory database
    */
  def getDatabaseFile(): File = myDatabaseFile

  /**
    * Starts the queue by creating a new thread, opening connection in that thread and executing all jobs there.
    * <p/>
    * The queue will remain active until {@link #stop} method is called, or until it is terminated by non-recoverable error.
    * <p/>
    * Calling this method second time does not have any effect. A queue cannot be started after it has stopped.
    * <p/>
    * Any jobs added to the queue prior to start() will be carried out.
    * <p/>
    * This method is thread-safe: it may be called from any thread.
    *
    * @return this queue
    * @throws IllegalStateException if threadFactory failed to produce a new thread
    */
  def start(): SQLiteQueue = {
    var thread: Thread = null
    myLock synchronized {
      if (myThread != null || myStopRequested) {
        logger.warn(Internal.mkLogMessage(this.toString(), if (myStopRequested) "stopped" else "already started"))
        return this
      }
      if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), "starting"))
      thread = myThreadFactory.newThread(new Runnable() {
        def run(): Unit = {
          runQueue()
        }
      })

      if (thread == null) throw new IllegalStateException(this + " cannot create new thread")

      val name = thread.getName
      // override default thread names
      if (name == null || name.startsWith("Thread-") || name.startsWith("pool-")) thread.setName(this.toString())

      myThread = thread
    }

    thread.start()

    this
  }

  /**
    * Stops the queue. After this method is called, no more jobs are accepted in {@link #execute} method. The thread
    * and connection are finished and disposed.
    * <p/>
    * If <code>gracefully</code> parameter is true, the currently queued jobs will be executed before queue stops.
    * Otherwise, any pending jobs are cancelled, and the currently running job may be cancelled to. (If the currently
    * running job is ignorant of job.isCancelled() status and does not run a long SQL statement, it still may finish
    * normally.)
    * <p/>
    * After call to <code>stop(true)</code> you can call <code>stop(false)</code> to force non-gracefull shutdown.
    * Other than that, calling <code>stop()</code> second time has no effect.
    * <p/>
    * If the queue hasn't been started, it will not be able to start later.
    * <p/>
    * This method is thread-safe: it may be called from any thread. It finishes immediately, while actual stopping
    * of the queue happening asynchronously. If you need to wait until queue is fully stopped, use {@link #join} method
    * after you called stop().
    *
    * @param gracefully if true, jobs already queued will be executed, then the queue will stop
    * @return this queue
    */
  def stop(gracefully: Boolean): SQLiteQueue = {
    var currentJob: SQLiteJob[_] = null
    myLock synchronized {
      if (!gracefully) {
        if (!myStopRequired && myStopRequested)
          if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), "now stopping non-gracefully"))

        myStopRequired = true
      }
      if (myStopRequested) { // already stopping
        return this
      }
      if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), if (gracefully) "stopping gracefully" else "stopping non-gracefully"))

      myStopRequested = true
      if (myStopRequired) currentJob = myCurrentJob
      myLock.notify()
    }

    if (currentJob != null) currentJob.cancel(true)

    this
  }

  /**
    * Waits for the queue to stop. The method uses {@link Thread#join} method to join with the queue thread.
    * <p/>
    * Note that this method does not stop the queue. You need to call {@link #stop} explicitly.
    * <p/>
    * If queue has not been started, the method returns immediately.
    *
    * @return this queue
    * @throws InterruptedException  if the current thread is interrupted
    * @throws IllegalStateException if called from the queue thread
    */
  @throws[InterruptedException]
  def join(): SQLiteQueue = {
    if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(),"waiting for queue to stop"))
    val thread = myThread
    if (thread == Thread.currentThread) throw new IllegalStateException()
    if (thread != null) thread.join()
    this
  }

  /**
    * Places a job in the queue for asynchronous execution in database thread.
    * <p/>
    * The added job's {@link SQLiteJob#job} method will be called from the database thread with an instance of
    * {@link SQLiteConnection}. Job may provide a return value, which will be treated as the job result.
    * <p/>
    * The queue must be started in order for jobs to start executing. Jobs may be added to the queue before or after
    * the queue is started. However, if the queue is already stopped, the job will be immediately cancelled. (It will
    * receive {@link SQLiteJob#jobCancelled} and {@link SQLiteJob#jobFinished} callbacks before this method finishes.)
    * <p/>
    * Because this method returns the argument, you can chain this method with other methods in SQLiteJob or in its
    * subclass:
    * <pre>
    * MyResult r = myQueue.execute(new SQLiteJob&lt;MyResult&gt;() { ... }).complete();
    * </pre>
    *
    * @param job the job to be executed on this queue's database connection, must not be null
    * @param [T] class of the job's result; use Object or Void if no result is needed
    * @param [J] job class
    * @return job
    * @see SQLiteJob
    */
  //---//def execute[T, J <: SQLiteJob[T]](job: J): J = {
  def execute[J <: SQLiteJob[_]](job: J): J = {
    require(job != null, "job is null")

    var cancel = false
    myLock synchronized {
      if (myStopRequested) {
        if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(),s"job not executed: $job"))
        cancel = true
      }
      else {
        if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(),s"queueing $job"))
        addJob(job)
        myLock.notify()
      }
    }

    if (cancel) job.cancel(true)

    job
  }

  /**
    * Waits until all jobs in the queue are executed.
    *
    * @return this instance
    * @throws InterruptedException if the current thread is interrupted
    */
  @throws[InterruptedException]
  def flush(): SQLiteQueue = {

    myLock synchronized {
      while (!isJobQueueEmpty() || myCurrentJob != null) {
        myLock.wait(1000)
        myLock.notify()
      }
    }

    this
  }

  /**
    * Checks if the queue is stopped.
    *
    * @return true if the queue was requested to stop or has stopped
    */
  def isStopped(): Boolean = myLock synchronized myStopRequested

  /**
    * Checks if the current thread is the thread that runs the queue's database connection.
    *
    * @return true if the current thread is the database thread
    */
  def isDatabaseThread(): Boolean = Thread.currentThread == myThread

  /**
    * Adds a job to the job collection. Override to change the logic or order of jobs.
    * <p/>
    * This method is called under synchronized lock and must not call any listeners or alien code.
    *
    * @param job the job to be added to myJobs, the latter possible being null
    */
  protected def addJob(job: SQLiteJob[_]): Unit = {
    assert(Thread.holdsLock(myLock), job)
    var jobs = myJobs
    if (jobs == null) {
      jobs = createJobCollection
      myJobs = jobs
    }
    jobs.add(job)
  }

  /**
    * Creates a new collection for storing pending jobs. Override to change the queue logic.
    * <p/>
    * This method is called under synchronized lock and must not call any listeners or alien code.
    *
    * @return an instance of collection for jobs
    */
  protected def createJobCollection() = new java.util.ArrayList[SQLiteJob[_]]

  /**
    * Checks if there are no more pending jobs. Override to change the queue logic.
    * <p/>
    * This method is called under synchronized lock and must not call any listeners or alien code.
    *
    * @return true if there are no pending jobs
    */
  protected def isJobQueueEmpty(): Boolean = {
    assert(Thread.holdsLock(myLock))
    myJobs == null || myJobs.isEmpty
  }

  /**
    * Clears the queue and returned removed jobs. Override to change the queue logic.
    * </p>
    * After this method is called, {@link #isJobQueueEmpty} must return true.
    * <p/>
    * This method is called under synchronized lock and must not call any listeners or alien code.
    *
    * @return non-null list of removed jobs
    */
  protected def removeJobsClearQueue(): java.util.List[SQLiteJob[_]] = {
    assert(Thread.holdsLock(myLock))
    if (myJobs == null) return Collections.emptyList[SQLiteJob[_]]()
    val r = new java.util.ArrayList[SQLiteJob[_]](myJobs)
    myJobs.clear()
    r
  }

  /**
    * Selects the next job from pending jobs to be executed. Override to change the queue logic.
    * <p/>
    * This method is called under synchronized lock and must not call any listeners or alien code.
    *
    * @return null if there are no pending jobs, or the job for execution
    */
  protected def selectJob(): SQLiteJob[_] = {
    assert(Thread.holdsLock(myLock))
    val jobs = myJobs
    if (jobs == null || jobs.isEmpty) return null
    val ii = jobs.iterator
    val r = ii.next()
    ii.remove()
    r
  }

  /**
    * Creates and opens a connection to the database. Override to change how database connection is opened.
    * <p/>
    * If this method throws an exception, the queue thread will terminate and possible reincarnate to try again.
    *
    * @return a new connection, not null, that can be used in the current thread
    * @throws SQLiteException if connection cannot be created
    * @see #initConnection
    */
  @throws[SQLiteException]
  protected def openConnection(): SQLiteConnection = {
    val connection = new SQLiteConnection(myDatabaseFile)
    if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), s"opening $connection"))
    try
      connection.open()
    catch {
      case e: Exception =>
        logger.warn(s"cannot open $connection", e)
        throw e
    }
    connection
  }

  /**
    * Initialize a new connection. Override to provide additional initialization code, for example executing
    * initializing SQL.
    * <p/>
    * If this method throws an exception, the queue thread will terminate and possible reincarnate to try again.
    *
    * @param connection freshly opened database connection
    * @throws SQLiteException if any initialization code fails
    */
  @throws[SQLiteException]
  protected def initConnection(connection: SQLiteConnection): Unit = {}

  /**
    * Disposes the connection. Override to change how connection is disposed.
    *
    * @param connection database connection no longer in use by the queue
    */
  protected def disposeConnection(connection: SQLiteConnection): Unit = {
    try {
      if (connection != null) {
        if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), s"disposing $connection"))
        connection.dispose()
      }
    }
    catch {
      case e: Exception =>
        logger.error(Internal.mkLogMessage(this.toString(), "error disposing connection"), e)
    }
  }

  /**
    * Rolls back current transaction. This method is called after exception is caught from a job, or after
    * job is cancelled. Override to change how to handle these two situations.
    */
  protected def rollback(): Unit = {
    if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), "rolling back transaction"))
    try
      myConnection.exec("ROLLBACK")
    catch {
      case e: Exception =>
        logger.error(Internal.mkLogMessage(this.toString(), "exception during rollback"), e)
    }
  }

  /**
    * Runs the job with the current connection.
    *
    * @param job next job from the queue
    * @throws Throwable any kind of problem
    */
  @throws[Throwable]
  protected def executeJob(job: SQLiteJob[_]): Unit = {
    if (job == null) return
    val connection = myConnection
    if (connection == null) throw new IllegalStateException(this + ": executeJob: no connection")
    try {
      if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), s"executing $job"))
      job.execute(connection, this)
      afterExecute(job)
      if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), s"finished executing $job"))
    } catch {
      case e: Throwable =>
        handleJobException(job, e)
    }
  }

  /**
    * Do some work after job.execute() finished. By default, performs rollback after a cancelled job.
    *
    * @param job finished job
    * @throws Throwable any kind of problem
    */
  @throws[Throwable]
  protected def afterExecute(job: SQLiteJob[_]): Unit = {
    assert(job.isDone, job)
    if (job.isCancelled) rollback()
  }

  /**
    * Do some work if job threw an exception. By default, rolls back and ignores the exception.
    *
    * @param job erred job
    * @param e   exception thrown by the job
    * @throws Throwable any kind of problem
    */
  @throws[Throwable]
  protected def handleJobException(job: SQLiteJob[_], e: Throwable): Unit = {
    rollback()
    e match {
      case death: ThreadDeath => throw death
      case _ => {}
    }
  }

  /**
    * Provides reincarnation timeout (the period to wait before reincarnating abnormally stopped queue thread).
    *
    * @return reincarnation timeout
    */
  protected def getReincarnationTimeout: Long = SQLiteQueue.DEFAULT_REINCARNATE_TIMEOUT

  /**
    * Checks if reincarnation should be attempted after queue thread terminates abnormally.
    *
    * @return true if reincarnation should be attempted
    */
  protected def isReincarnationPossible: Boolean = myDatabaseFile != null && getReincarnationTimeout >= 0

  /**
    * Reincarnates the queue. This implementation starts a new thread, which waits for some time and then restarts
    * database thread.
    *
    * @param reincarnateTimeout time to wait
    */
  protected def reincarnate(reincarnateTimeout: Long): Unit = {
    logger.warn(Internal.mkLogMessage(this.toString(), s"stopped abnormally, reincarnating in $reincarnateTimeout ms"))

    val me = this

    val reincarnator = myThreadFactory.newThread(new Runnable() {
      def run(): Unit = {
        try {
          myLock synchronized {
            var now = System.currentTimeMillis
            val wake = now + reincarnateTimeout
            while (now < wake) {
              myLock.wait(wake - now)
              if (myStopRequested) {
                logger.warn(Internal.mkLogMessage(this.toString, "stopped, will not reincarnate"))
                return
              }
              now = System.currentTimeMillis
            }
          }

          me.start()
        } catch {
          case e: InterruptedException =>
            logger.error(Internal.mkLogMessage(me.toString(),"not reincarnated"), e)
        }
      }
    })

    reincarnator.setName(s"reincarnate $this in $reincarnateTimeout ms")

    reincarnator.start()
  }

  private def runQueue(): Unit = {
    try
      queueFunction()
    catch {
      case e: InterruptedException =>
        Thread.currentThread.interrupt()
        logger.error(Internal.mkLogMessage(this.toString() + " interrupted"), e)
      case e: Throwable =>
        logger.error(Internal.mkLogMessage(this.toString(), "error running job queue"), e)
        e match {
          case value: ThreadDeath => throw value
          case _ => {}
        }
    }
    finally threadStopped()
  }

  @throws[Throwable]
  private def queueFunction(): Unit = {
    if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), "started"))

    disposeConnection(myConnection)
    myConnection = null
    myConnection = openConnection()
    initConnection(myConnection)

    while (true) {
      if (Thread.interrupted) throw new InterruptedException

      var job: SQLiteJob[_] = null
      myLock synchronized {
        myCurrentJob = null
        myLock.notify()

        while (job == null) {
          if (myStopRequested && (myStopRequired || isJobQueueEmpty)) {
            if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), "thread exiting"))
            return
          }

          job = selectJob()

          if (job != null) {
            myCurrentJob = job
          } else {
            myLock.wait(1000)
            myLock.notify()
          }
        }
      }

      executeJob(job)
    }
  }

  private def cancelJobs(jobs: java.util.List[SQLiteJob[_]]): Unit = {
    if (jobs != null) {
      import scala.collection.JavaConverters._
      for (job <- jobs.iterator.asScala) {
        job.cancel(true)
      }
    }
  }

  private def threadStopped(): Unit = {
    assert(Thread.currentThread == myThread, Thread.currentThread + " " + myThread)
    disposeConnection(myConnection)
    myConnection = null
    var reincarnate = false
    var droppedJobs: java.util.List[SQLiteJob[_]] = null
    myLock synchronized {
      reincarnate = !myStopRequested

      if (reincarnate && !isReincarnationPossible) {
        logger.error(Internal.mkLogMessage(this.toString(), "stopped abnormally, reincarnation is not possible for in-memory database"))
        reincarnate = false
        myStopRequested = true
      }

      if (!reincarnate) droppedJobs = removeJobsClearQueue()
      myThread = null
    }

    if (!reincarnate) {
      cancelJobs(droppedJobs)

      if (canLogTrace) logger.trace(Internal.mkLogMessage(this.toString(), "stopped"))
    }
    else this.reincarnate(getReincarnationTimeout)
  }
}

