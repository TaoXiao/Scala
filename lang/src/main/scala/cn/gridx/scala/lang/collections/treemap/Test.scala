package cn.gridx.scala.lang.collections.treemap

import scala.collection.immutable.TreeMap

/**
  * Created by tao on 7/19/16.
  */
object Test extends App {
  var m =  TreeMap[Int, Int]()
  m += (1 -> 10)
  m += (2 -> 20)
  m += (3 -> 30)
  m += (0 -> 0)

  m += (2 -> 21)
  m += (2 -> 19)

  println(m)

}
