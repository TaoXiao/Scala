package cn.gridx.scala.lang.traits.This

/**
  * Created by tao on 3/10/16.
  */
object ThisTypeAnnotation {
  def main(args: Array[String]): Unit = {
    //  val a = new Object with Prompter // 编译错误
    val b = new Object with GreetingProvider  // 可以OK

    val p = new Prompter with GreetingProvider

    p.printGreeting()
    println(p.getClass.getCanonicalName)
  }
}

trait Prompter {
  this: GreetingProvider =>

  val prompt = "> "

  def printGreeting() = {
    println(s"${this.prompt} ${this.greeting}")
  }
}

trait GreetingProvider {
  val greeting = "大家好!"
}
