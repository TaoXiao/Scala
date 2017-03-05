package cn.gridx.scala.lang.test

/**
  * Created by tao on 2/14/17.
  */
object TestYield extends App {
  val L = List.empty[String]
  val nL: List[String] = for (l <- L) yield
    l + "hi"

}
