package cn.gridx.scala.lang.collections.treemap

import scala.collection.immutable.TreeMap

/**
  * Created by tao on 7/19/16.
  */
object Test extends App {
  var m =  TreeMap[Int, Int]()
  m += (1 -> 100)
  m += (2 -> 200)
  m += (3 -> 300)
  println(m)

  m -= 3
  println(m)

  m += (4 -> 400)
  println(m)
}
