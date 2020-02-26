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

import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object SQLiteJob { // internal state constants
  private val PENDING = 0
  private val RUNNING = 1
  private val SUCCEEDED = 2
  private val ERROR = 3
  private val CANCELLED = 4
}

/**
  * SQLiteJob is a unit of work accepted by {@link SQLiteQueue}. You can
  * implement {@link #job} method and add the job to the queue with {@link SQLiteQueue#execute} method.
  * <p/>
  * A job can optionally have a result. Type parameter <code>&lt;T&gt;</code> defines the type of the result, and the
  * value of the result is returned by the <code>job()</code> method. If job finishes unsuccessfully or is cancelled,
  * the result is always null. If you don't need the job to have a result, define it as
  * <code>SQLiteJob&lt;Object&gt;</code> or <code>SQLiteJob&lt;Void&gt;</code> and return null from the <code>job()</code>
  * method.
  * <p/>
  * Job implements {@link Future} interface and can be used along with different types of future results.
  * <p/>
  * Also, you can override methods {@link #jobStarted}, {@link #jobFinished}, {@link #jobCancelled} and
  * {@link #jobError} to implement callbacks during the job's lifecycle.
  * <p/>
  * SQLiteJob is a one-time object. Once the job is finished, it cannot be executed again.
  * <p/>
  * Public methods of SQLiteJob are thread-safe unless specified otherwise. Protected methods are mostly called
  * from the database thread and must be overridden carefully.
  * <p/>
  * When programming a job, it's a good practice to keep transaction boundaries within single job. That is, if you
  * BEGIN TRANSACTION in the job, make sure you COMMIT or ROLLBACK in the end. Otherwise, your transaction will
  * remain unfinished, locks held, and you possible wouldn't know which job will execute next in the context of
  * this unfinished transaction.
  *
  * @tparam T type of the result
  * @see SQLiteQueue
  * @author Igor Sereda
  */
abstract class SQLiteJob[T] extends Future[T] with Logging { // <: AnyRef

  /**
    * Protection for fields
    */
  final private val myLock = new Object()
  /**
    * Current state. Protected by myLock.
    */
  private var myState = SQLiteJob.PENDING
  /**
    * Error thrown by job()
    */
  private var myError: Throwable = _
  /**
    * Keeps connection while the job is being executed (in order to interrupt SQL)
    */
  private var myConnection: SQLiteConnection = _
  /**
    * Keeps a reference to the queue while the job is being executed. May be null.
    */
  private var myQueue: SQLiteQueue = _
  /**
    * The result of the job
    */
  private var myResult: T = _

  /**
    * Performs work on the SQLite database.
    * <p/>
    * This method is called only once from the database thread, when the job is selected and executed
    * by the queue. After job is completed, it is removed from the queue and next job is executed.
    * <p/>
    * If job method throws any exception, it's recorded, logged, but otherwise it does not affect other jobs (except
    * for side-effects of unfinished SQL work). This may be changed by overriding job's or queue's related methods.
    *
    * @param connection an open connection to the database, not null
    * @return the result, or null
    * @throws Throwable on any problem
    * @see SQLiteQueue#execute
    */
  @throws[Throwable]
  protected def job(connection: SQLiteConnection): T

  /**
    * This method is called when the job is about to be executed, before call to {@link #job} method.
    * <p/>
    * This method may not be called at all if a job is cancelled before execution starts.
    *
    * @param connection an open connection to the database, not null
    * @throws Throwable on any problem
    */
  @throws[Throwable]
  protected def jobStarted(connection: SQLiteConnection): Unit = {
  }

  /**
    * This method is called when the job is no longer in the queue.
    * Overriding this method is the best way to asynchronously process the result of the job.
    * <p/>
    * This method is called <strong>always</strong>, regardless
    * of the job execution result, and even if the job is cancelled before execution. More strictly, it is called
    * once between the time {@link SQLiteQueue#execute} is called and the time when this job is no longer in the queue
    * nor being executed.
    * <p/>
    * The result of the job is passed as a parameter.
    *
    * @param result the result of the job, or null if the job was cancelled or has thrown exception
    * @throws Throwable on any problem
    */
  @throws[Throwable]
  protected def jobFinished(result: T): Unit = {
  }

  /**
    * This method is called after {@link #job} method has thrown an exception. The exception is passed
    * as a parameter.
    *
    * @param error exception thrown by the job
    * @throws Throwable on any problem, or the rethrown exception
    */
  @throws[Throwable]
  protected def jobError(error: Throwable): Unit = {
  }

  /**
    * This method is called after job has been cancelled, either due to call to the {@link #cancel} method,
    * or because queue has stopped, or for any other reason.
    *
    * @throws Throwable on any problem
    */
  @throws[Throwable]
  protected def jobCancelled(): Unit = {
  }

  /**
    * Returns the instance of the queue that is currently running the job. May return null.
    *
    * @return the queue that is currently running this job, if available.
    */
  final protected def getQueue(): SQLiteQueue = myLock synchronized myQueue

  /**
    * Returns the error thrown by the job.
    *
    * @return the error thrown by the { @link #job} method, or null.
    */
  def getError(): Throwable = myLock synchronized myError

  /**
    * Returns <tt>true</tt> if this job completed.
    *
    * Completion may be due to normal termination, an exception, or
    * cancellation -- in all of these cases, this method will return
    * <tt>true</tt>.
    *
    * @return <tt>true</tt> if this task completed
    */
  def isDone(): Boolean = {
    myLock synchronized {
      myState == SQLiteJob.SUCCEEDED || myState == SQLiteJob.CANCELLED || myState == SQLiteJob.ERROR
    }
  }

  /**
    * Attempts to cancel execution of this job.  This attempt will
    * fail if the job has already completed, has already been cancelled,
    * or could not be cancelled for some other reason. If successful,
    * and this job has not started when <tt>cancel</tt> is called,
    * this job should never run.  If the job has already started,
    * then the <tt>mayInterruptIfRunning</tt> parameter determines
    * whether the thread executing this task should be interrupted in
    * an attempt to stop the task.
    * <p/>
    * When an active job is being cancelled with <tt>mayInterruptIfRunning</tt> parameter,
    * {@link SQLiteConnection#interrupt} method is called to cancel a potentially long-running SQL. If there's
    * no SQL running, it will have no effect. The running job may check {@link #isCancelled} method and finish
    * prematurely. There are no other means to cancel a running job.
    * <p/>
    * If the job is still pending, then {@link #jobCancelled} and {@link #jobFinished} callbacks are called during
    * the execution of this method.
    *
    * @param mayInterruptIfRunning <tt>true</tt> if the thread executing this
    *                              task should be interrupted; otherwise, in-progress tasks are allowed
    *                              to complete
    * @return <tt>false</tt> if the task could not be cancelled,
    *         typically because it has already completed normally;
    *         <tt>true</tt> otherwise
    */
  def cancel(mayInterruptIfRunning: Boolean): Boolean = {
    var connection: SQLiteConnection = null

    myLock synchronized {
      if (isDone()) return false
      if (myState == SQLiteJob.RUNNING && !mayInterruptIfRunning) return false
      assert(myConnection == null || myState == SQLiteJob.RUNNING, s"$myState $myConnection" )
      myState = SQLiteJob.CANCELLED
      connection = myConnection
    }

    if (connection != null) {

      logger.trace(Internal.mkLogMessage(this.toString,"interrupting"))

      try
        connection.interrupt()
      catch {
        case e: SQLiteException =>
          logger.error(Internal.mkLogMessage(this.toString(), "exception when interrupting"), e)
      }
    }
    else {
      logger.trace(Internal.mkLogMessage(this.toString(),"cancelling"))

      // job never ran
      finishJob(null.asInstanceOf[T])
    }

    true
  }

  /**
    * Cancels this job. Convenience method to call <code>cancel(true)</code>.
    *
    * @see #cancel(boolean)
    */
  def cancel(): Unit = {
    cancel(true)
  }

  /**
    * Returns <tt>true</tt> if this job was cancelled before it completed
    * normally.
    *
    * @return <tt>true</tt> if this job was cancelled before it completed
    */
  def isCancelled: Boolean = {
    myLock synchronized
      myState == SQLiteJob.CANCELLED

  }

  /**
    * Waits if necessary for the job to complete, and then
    * retrieves its result.
    * <p/>
    * Calling this method, as well as convenience method {@link #complete}, is a way to block the current thread
    * and wait for the result.
    *
    * @return the result
    * @throws java.util.concurrent.CancellationException if the job was cancelled
    * @throws ExecutionException                         if the job threw an  exception
    * @throws InterruptedException                       if the current thread was interrupted while waiting
    */
  @throws[InterruptedException]
  @throws[ExecutionException]
  def get(): T = {
    try
      get(java.lang.Long.MAX_VALUE, TimeUnit.MILLISECONDS)
    catch {
      case e: TimeoutException =>
        throw new AssertionError(e + " cannot happen")
    }
  }

  /**
    * Waits if necessary for at most the given time for the job
    * to complete, and then retrieves its result, if available.
    *
    * @param timeout the maximum time to wait
    * @param unit    the time unit of the timeout argument
    * @return the result
    * @throws java.util.concurrent.CancellationException if the job was cancelled
    * @throws ExecutionException                         if the job threw an exception
    * @throws InterruptedException                       if the current thread was interrupted while waiting
    * @throws TimeoutException                           if the wait timed out
    */
  @throws[InterruptedException]
  @throws[ExecutionException]
  @throws[TimeoutException]
  def get(timeout: Long, unit: TimeUnit): T = {
    myLock synchronized {
      if (!isDone) {
        val queue = myQueue
        if (queue != null && queue.isDatabaseThread)
          throw new IllegalStateException("called from the database thread, would block forever")

        var now = System.currentTimeMillis
        var stop = 0L
        if (timeout <= 0) stop = now - 1
        else {
          stop = now + unit.toMillis(timeout)
          if (stop < now) { // overflow
            stop = java.lang.Long.MAX_VALUE
          }
        }
        while (now < stop  && !isDone) {
          if (Thread.interrupted) throw new InterruptedException()
          myLock.wait(Math.min(1000L, stop - now))
          now = System.currentTimeMillis
        }
      }

      if (isDone()) {
        if (myState == SQLiteJob.ERROR) throw new ExecutionException(myError)
        return myResult
      }
    }

    throw new TimeoutException()
  }

  /**
    * Wait if necessary for the job to complete and return the result.
    * <p/>
    * This is a convenience method for calling {@link #get()} without having to catch exceptions.
    *
    * @return the result of the job, or null if it has been cancelled or has erred
    */
  def complete(): T =  {
    val result = try
      get(java.lang.Long.MAX_VALUE, TimeUnit.MILLISECONDS)
    catch {
      case e: InterruptedException =>
        logger.error(Internal.mkLogMessage(this.toString(),"complete() consumed exception"), e)
        Thread.currentThread.interrupt()
        null
      case e: ExecutionException =>
        logger.error(Internal.mkLogMessage(this.toString(),"complete() consumed exception"), e)
        null
      case e: TimeoutException =>
        logger.error(Internal.mkLogMessage(this.toString(),"complete() timeout?"), e)
        null
    }

    result.asInstanceOf[T]
  }

  override def toString(): String = {
    var r = super.toString
    val k = r.lastIndexOf('.')
    if (k >= 0) r = r.substring(k + 1)
    r
  }


  @throws[Throwable]
  private[sqlite4s] def execute(connection: SQLiteConnection, queue: SQLiteQueue): Unit = {
    if (!startJob(connection, queue)) return ()
    var result: T = null.asInstanceOf[T]
    try result = job(connection)
    catch {
      case e: Throwable => processJobError(e)
    } finally finishJob(result)
    ()
  }

  private def startJob(connection: SQLiteConnection, queue: SQLiteQueue): Boolean = {
    myLock synchronized {
      if (myState != SQLiteJob.PENDING) {
        if (myState != SQLiteJob.CANCELLED)
          logger.warn(Internal.mkLogMessage(this.toString(),"was already executed"))
        return false
      }
      myState = SQLiteJob.RUNNING
      myConnection = connection
      myQueue = queue
    }
    logger.trace(Internal.mkLogMessage(this.toString(),"started"))

    try jobStarted(connection)
    catch {
      case e: Throwable => logger.error(Internal.mkLogMessage(this.toString(),"callback exception"), e)
    }

    true
  }

  @throws[Throwable]
  private def processJobError(e: Throwable): Unit = {
    myLock synchronized {
      if (e.isInstanceOf[SQLiteInterruptedException]) {
        myState = SQLiteJob.CANCELLED
        logger.trace(Internal.mkLogMessage(this.toString(),"cancelled"), e)
      }
      else {
        logger.warn(Internal.mkLogMessage(this.toString(),"job exception"), e)
        myError = e
        myState = SQLiteJob.ERROR
        throw e
      }
    }
  }

  private def finishJob(result: T): Unit = {
    var state = 0
    var error: Throwable = null

    myLock synchronized {
      myConnection = null
      if (myState == SQLiteJob.RUNNING) {
        myState = SQLiteJob.SUCCEEDED
        myResult = result
      }
      state = myState
      error = myError
    }

    try {
      if (state == SQLiteJob.CANCELLED) jobCancelled()
      else if (state == SQLiteJob.ERROR) jobError(error)
    }
    catch {
      case e: Throwable =>
        logger.error(Internal.mkLogMessage(this.toString(),"callback exception"), e)
        e match {
          case death: ThreadDeath => throw death
          case _ => {}
        }
    }

    try jobFinished(result)
    catch {
      case e: Throwable =>
        logger.error(Internal.mkLogMessage(this.toString(),"callback exception"), e)
        e match {
          case death: ThreadDeath => throw death
          case _ => {}
        }
    }

    myLock synchronized {
      myQueue = null
      myLock.notifyAll()
    }

    logger.trace(Internal.mkLogMessage(this.toString(),"finished"))
  }
}