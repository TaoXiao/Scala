package cn.gridx.scala.lang.concurrency.actors

import scala.actors.Actor

/**
  * Created by tao on 2/25/16.
  */
object Driver {
  def main(args: Array[String]): Unit = {
    println("+++++++++ main 开始 ... +++++++++")

    MultiplierActor.start

    MultiplierActor ! 100
    //MultiplierActor ! ("你好", self)

    println("+++++++++ main 结束 ... +++++++++")
  }
}


object MultiplierActor extends Actor {
  override def act(): Unit = {
    react {
      case (s: String, actor: Actor) =>
        println(s"Type: (String, Actor)")
        actor ! s
        act
      case n: Int =>
        println(s"Type: Int, value: $n")
      case msg: String =>
        println("Type: String")
        act
    }
  }
}
