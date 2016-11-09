package cn.gridx.scala.lang.collections.lists

/**
  * Created by tao on 10/18/16.
  */
object TestList extends App {
  var L = List(1)
  for (i <- Range(5, 2, -1))
    L ::= i

  println(L)
}
