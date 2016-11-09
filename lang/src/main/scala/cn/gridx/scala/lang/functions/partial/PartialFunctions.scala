package cn.gridx.scala.lang.functions.partial

/**
  * Created by tao on 3/4/16.
  */
object PartialFunctions {
  def RealProcessor: PartialFunction[Any, Unit] = F

  val f0: PartialFunction[Any, Unit] = {
    case _: Placeholder =>
      println("Error: Placeholder is not supported")
  }

  val f1: PartialFunction[Any, Unit] = {
    case x: String =>
      println(s"String: $x")
  }

  val f2: PartialFunction[Any, Unit] = {
    case x: Int =>
      println(s"Integer: $x")
  }

  val f3: PartialFunction[Any, Unit] = {
    case x: Double =>
      println(s"Double: $x")
  }

  val f4: PartialFunction[Any, Unit] = {
    case x: Boolean =>
      println(s"Boolean: $x")
  }

  def addFunc(f: PartialFunction[Any, Unit])(implicit F: PartialFunction[Any, Unit]): PartialFunction[Any, Unit] = {
    F orElse f
  }

  // 将多个partial function累加到F上
  implicit var F: PartialFunction[Any, Unit] = f0

  def main(args: Array[String]): Unit = {
    F = addFunc(f1)
    F = addFunc(f2)
    F = addFunc(f3)
    F = addFunc(f4)
    RealProcessor("hello")
    RealProcessor(100)
    RealProcessor(100.0001)
    RealProcessor(true)
    RealProcessor(Placeholder())
  }
}


case class Placeholder()