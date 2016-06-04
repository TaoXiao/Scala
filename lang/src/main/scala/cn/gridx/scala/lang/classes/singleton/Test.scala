package cn.gridx.scala.lang.classes.singleton

import scala.collection.mutable.ListBuffer

/**
  * Created by tao on 5/7/16.
  */
object Test extends App {
  /*val m1 = SingleObject

  println("---\n----")
  println(s"m1 = ${m1.msg}")
  println(s"m2 = ${m1.instance1.msg}")
  println(s"m2 = ${m1.instance1.msg}")
  println(s"m2 = ${m1.instance1.msg}")
  println(s"m2 = ${m1.instance1.msg}")
  */

  val L1 = ListBuffer[String]("1", "2", "3")
  val L2 = ListBuffer[String]("a", "b", "c")

  println((L1 ++ L2).mkString(", "))

}
