package cn.gridx.scala.akka.tutorial

import akka.actor.{ActorSystem, Props, Actor}
import akka.event.Logging

/**
  * Created by tao on 3/5/16.
  */
object LoggingExample  {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("The-Actor-System")
    val actor = system.actorOf(Props[TheActor], "The-Actor")

    actor ! "你好!"
    actor ! 100

    system.shutdown
  }

  class TheActor extends Actor {
    val logger = Logging(context.system, this)

    def receive = {
      case s: String =>
        logger.info(s"string = $s")
      case _ =>
        logger.info(s"Unrecognized message")
    }

  }
}


