package com.github.sqlite4s

import utest._

object SQLPartsTests extends TestSuite {

  val tests = Tests {
    'testAppendParams - testAppendParams
  }

  def testAppendParams(): Unit = {
    println(createParams(0))
    assert(createParams(0) == "")
    assert(createParams(-1) == "")
    assert(createParams(1) == "?")
    assert(createParams(2) == "?,?")

    var i = 3
    while (i < 1000) {
      check(i)
      i += 1
    }
  }

  private def check(count: Int): Unit = {
    val params = createParams(count)
    val b = new StringBuilder

    var i = 0
    while (i < count) {
      if (i > 0) b.append(',')
      b.append('?')
      i += 1
    }
  }

  private def createParams(count: Int) = new SQLParts().appendParams(count).toString()
}
