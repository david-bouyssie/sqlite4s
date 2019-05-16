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