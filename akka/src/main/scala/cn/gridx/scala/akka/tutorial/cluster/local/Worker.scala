package cn.gridx.scala.akka.tutorial.cluster.local

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by tao on 6/19/16.
  */
class Worker extends  Actor {

  override def receive: Receive = {
    case AckMsg(addr) =>
      println(s"\n我收到了来自Master的确认消息 (addr = $addr)\n")
  }
}

object Worker extends App {
  // 这里可以让每一个worker选择各自的port来运行
  val port = if (args.isEmpty) "0" else args(0)

  val config =
    ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.load())

  val system = ActorSystem("XtAkkaClusterSystem", config)

  val clusterStateListener = system.actorOf(Props[Worker])
}
