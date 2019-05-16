package com.github.sqlite4s

import utest._

object RTreeTests extends SQLiteConnectionFixture {

  val tests = Tests {
    //'testRTree - testRTree
  }

  @throws[SQLiteException]
  def testRTree(): Unit = {
    val connection = memDb().open()
    connection.exec("create virtual table rtree_example using rtree(id, minX, maxX, minY, maxY)")
    val count = 1234
    val surroundingBox = new BBox(-100, 100, -100, 100)
    fillRTree(connection, count, surroundingBox)
    val realCount = countObjectsInBox(connection, surroundingBox)
    assert(realCount == count)
  }

  @throws[SQLiteException]
  private def fillRTree(connection: SQLiteConnection, count: Int, surroundingBox: BBox): Unit = {
    val fillStatement = connection.prepare("insert into rtree_example values (?,?,?,?,?)")

    for (i <- 0 until count) {
      val newNode = generateNode(surroundingBox)
      fillStatement.bind(1, i)
      fillStatement.bind(2, newNode.minX)
      fillStatement.bind(3, newNode.maxX)
      fillStatement.bind(4, newNode.minY)
      fillStatement.bind(5, newNode.maxY)
      fillStatement.step()
      fillStatement.reset()
    }

    fillStatement.dispose()
  }

  private def generateNode(surroundingBox: BBox): BBox = {
    val nodeHeight = surroundingBox.height / 1e6
    val nodeWidth = surroundingBox.width / 1e6
    val newCenterX = Math.random * surroundingBox.width + surroundingBox.minX
    val newCenterY = Math.random * surroundingBox.height + surroundingBox.minY
    BBox(newCenterX - nodeWidth, newCenterX + nodeWidth, newCenterY - nodeHeight, newCenterY + nodeHeight)
  }

  @throws[SQLiteException]
  private def countObjectsInBox(connection: SQLiteConnection, surroundingBox: BBox): Int = {
    val selectStatement = connection.prepare("select count(*) from rtree_example where minX >= ? and maxX <= ? and minY >=? and maxY <= ?")
    selectStatement.bind(1, surroundingBox.minX)
    selectStatement.bind(2, surroundingBox.maxX)
    selectStatement.bind(3, surroundingBox.minY)
    selectStatement.bind(4, surroundingBox.minY)
    selectStatement.step()

    val count = selectStatement.columnInt(0)
    selectStatement.dispose()

    count
  }

  case class BBox(minX: Double, maxX: Double, minY: Double, maxY: Double) {
    val width: Double = maxX - minX
    val height: Double = maxY - minY
  }

}
