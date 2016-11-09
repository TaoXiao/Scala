package cn.gridx.scala.lang.implicits.paramaters

/**
  * Created by tao on 4/21/16.
  */
object Example {
  implicit def X: Int = 100
  implicit def Y: (Int, Int) = (300, 400)
  implicit val Z: Double = 3.1415926

  def main(args: Array[String]): Unit = {
    f1("hello")
    f2("bye")
    f3("again")
  }

  def f1(s: String)(implicit x: Int): Unit = {
    println(s"$s, x is $x")
  }

  def f2(s: String)(implicit y: (Int, Int)) = {
    println(s"$s, y is $y")
  }

  // a和b都是implicit
  def f3(s: String)(implicit a: Int, b: Double) = {
    println(s"s = $s, a = $a, b = $b")
  }
}
