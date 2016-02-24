package cn.gridx.scala.lang.test

import java.util
import java.util.Map.Entry

/**
  * Created by tao on 2/23/16.
  */
object Test extends App {

  var A = new Array[Int](5)
  for (i <- 0 until A.size)
    A(i) = i*100
  println(A.mkString(", "))

  A = A.dropWhile(_ <= 100)
  println(A.mkString(" -> "))
}
