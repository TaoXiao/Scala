package cn.gridx.scala.akka.cluster

import akka.actor.{Props, ActorSystem, ActorLogging, Actor}
import com.typesafe.config.ConfigFactory

/**
  * Created by tao on 1/16/17.
  */
class Worker extends Actor with ActorLogging {
  override def receive: Receive = {
    case Hello(msg) =>
      log.info(s"收到来自${sender}的消息: msg = $msg")
    case Bye(msg)   =>
      log.info(s"收到来自${sender}的消息: msg = $msg")
    case msg =>
      log.info(s"收到来自${sender}的消息: $msg")
  }
}


object LaunchWorker {

  val LocalIP = "127.0.0.1"

  def main(args: Array[String]): Unit = {
    val systemConfigs = ConfigFactory.load()
      .withFallback(ConfigFactory.parseString(s"""akka.remote.netty.tcp.hostname = "$LocalIP" """))
      .withFallback(ConfigFactory.parseString(s""" akka.remote.netty.tcp.port = 0 """))
      .resolve()

    val systemName = systemConfigs.getString("akka.cluster.cluster-name")

    val system = ActorSystem(systemName, systemConfigs)
    val master = system.actorOf(Props(classOf[Master]), "worker")

    println("Worker actor is started ....")
  }
}
