package cn.gridx.scala.lang.collections.map

import scala.collection.mutable.{HashMap => MMap}

/**
  * Created by tao on 2/25/17.
  */
object TestKey {
  def main(args: Array[String]): Unit = {
    val m = MMap[String, Int]()

    m.put("A", 100)
    m.put("B", 200)

    val x = m.remove("C")

    println(x)
    println(m)
  }
}
