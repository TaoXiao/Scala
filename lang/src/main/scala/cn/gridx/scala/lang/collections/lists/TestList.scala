package cn.gridx.scala.lang.collections.lists

/**
  * Created by tao on 10/18/16.
  */
object TestList {
  def main(args: Array[String]): Unit = {
    testDrop()
  }

  def testConcat() {
    var L = List(1)
    for (i <- Range(5, 2, -1))
      L ::= i

    println(L(1))
  }

  def testDrop(): Unit = {
    val L = List("A","B","C","D", "E")
    println(L.drop(1))
    println("X" :: L.drop(3))
  }
}
