package cn.gridx.scala.lang.annotations.benchmark

import scala.annotation.StaticAnnotation

/**
  * Created by tao on 1/3/17.
  */
class BenchMark extends StaticAnnotation {
}

object BenchMark {
  println("hello")

  def f(): Unit = {
    println("f")
  }
}
