package cn.gridx.scala.akka.configurtions

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by tao on 8/3/16.
  */
object Test {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Hello_Akka_System", ConfigFactory.load("HelloAkkaConfigs.conf"))
    val actor = system.actorOf(Props[HelloActor].withMailbox("prio-mailbox"))
    Thread.sleep(1000)

    actor ! MsgTest("我来了")
  }
}
