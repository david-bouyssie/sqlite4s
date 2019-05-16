package com.github.sqlite4s

import utest._

object LoadAllTests extends SQLiteConnectionFixture {

  val tests = Tests {
    'testInts - testInts
    'testLongs - testLongs
  }

  @throws[SQLiteException]
  def testInts(): Unit = {
    val COUNT = 1000
    val sqlite = fileDb().open()
    sqlite.exec("create table x (id integer not null primary key)")
    sqlite.exec("begin")
    var st = sqlite.prepare("insert into x values(?)")

    var i = 0
    while (i < COUNT) {
      st.bind(1, i)
      st.step()
      st.reset()

      i += 1
    }
    st.dispose()
    sqlite.exec("commit")

    st = sqlite.prepare("select id from x order by (500-id)*(250-id)")

    val buffer = new Array[Int](249)
    var loaded = 0
    var count = 0
    var lastv = Integer.MIN_VALUE
    while ( {loaded = st.loadInts(0, buffer, 0, buffer.length); loaded > 0} ) {
      var i = 0
      while (i < loaded) {
        val id = buffer(i)
        val v = ((500 - id).toLong * (250 - id).toLong).toInt
        if (v < lastv) Predef.assert(v >= lastv, lastv + " " + v)
        lastv = v
        count += 1

        i += 1
      }
    }

    assert(COUNT == count)
  }

  @throws[SQLiteException]
  def testLongs(): Unit = {
    val COUNT = 1000
    val sqlite = fileDb().open()

    sqlite.exec("create table x (id integer not null primary key)")
    sqlite.exec("begin")

    var st = sqlite.prepare("insert into x values(?)")

    var i = 0
    while (i < COUNT) {
      st.bind(1, i.toLong)
      st.step()
      st.reset()

      i += 1
    }
    st.dispose()
    sqlite.exec("commit")
    st = sqlite.prepare("select id from x order by (500-id)*(250-id)")

    val buffer = new Array[Long](249)
    var loaded = 0
    var count = 0
    var lastv = Integer.MIN_VALUE

    while ( {loaded = st.loadLongs(0, buffer, 0, buffer.length); loaded > 0}) {
      var i = 0
      while (i < loaded) {
        val id = buffer(i)
        val v = ((500 - id) * (250 - id)).toInt
        if (v < lastv) Predef.assert(v >= lastv, lastv + " " + v)
        lastv = v
        count += 1

        i += 1
      }
    }

    assert(COUNT == count)
  }
}
