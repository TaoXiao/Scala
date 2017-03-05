package cn.gridx.scala.lang.classes.overload

/**
  * Created by tao on 2/19/17.
  */
class SampleClass {
  def f(a: Int): Unit = {
    println(s"a = $a")
  }

  def f(a: Int, b: Int) = {
    println(s"a = $a, b = $b")
  }
}

object SampleClass extends App {
  val c = new SampleClass()
  c.f(1)
  c.f(1, 2)
}
