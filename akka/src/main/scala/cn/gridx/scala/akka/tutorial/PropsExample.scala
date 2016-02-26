package cn.gridx.scala.akka.tutorial

import akka.actor.Props
import akka.event.Logging

/**
  * Created by tao on 2/26/16.
  */
object PropsExample {
  def main(args: Array[String]): Unit = {
    val prop1: Props = Props[MyAkkaActor]
    val prop2: Props = Props(new MyAkkaActor(100, "One")) // 不推荐,不要在一个actor中创建另一个actor
    val prop3: Props = Props(classOf[MyAkkaActor], 100, "One")
  }


  class MyAkkaActor(data: Int, msg: String) extends akka.actor.Actor {
    val log = Logging(context.system, this)

    def receive = {
      case x: String =>
        println(s"String -> $x")
      case y: Boolean =>
        println(s"Boolean -> $y")
      case _ =>
        println("Unknown msg")
    }
  }
}
