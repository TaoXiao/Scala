package cn.gridx.scala.akka.tutorial

import akka.actor.{Props, ActorSystem}
import akka.event.Logging


/**
  * Created by tao on 2/26/16.
  */
object ActorsExample {

  def main(args: Array[String]): Unit = {
    testScalaActor
  }

  def testAkkaActor(): Unit = {
    val system = ActorSystem("test-akka-actor")
    val actor = system.actorOf(Props[MyAkkaActor], "xiaotao-actor")
    actor ! "你好"
    actor ! true
    actor ! 100
    system.shutdown
  }

  class MyAkkaActor extends akka.actor.Actor {
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


  def testScalaActor(): Unit = {
    val actor = new MyScalaActor()
    actor.start

    actor ! "你好"
    actor ! true
    actor ! 100
  }

  class MyScalaActor extends scala.actors.Actor {
    override def act() = {
      while (true) receive {  // 需要while才能让receive被循环调用
          case x: String =>
            println(s"String -> $x")
          case y: Boolean =>
            println(s"Boolean -> $y")
          case _ =>
            println("Unknown msg")
        }
    }
  }

}
