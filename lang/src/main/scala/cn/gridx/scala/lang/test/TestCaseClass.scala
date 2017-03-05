package cn.gridx.scala.lang.test

/**
  * Created by tao on 2/15/17.
  */
object TestCaseClass extends App {
  val t = T("hello", 2008)
  t.productIterator.foreach(println)
}

case class T(a: String, b: Int)
