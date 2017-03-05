package cn.gridx.scala.akka.cluster

import akka.actor.{Props, ActorSystem, Actor, ActorLogging}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

/**
  * Created by tao on 1/16/17.
  */
class Master extends Actor with ActorLogging {
  override def receive: Receive = {
    case Hello(msg) =>
      log.info(s"收到来自${sender}的消息: msg = $msg")
    case Bye(msg)   =>
      log.info(s"收到来自${sender}的消息: msg = $msg")
    case msg =>
      log.info(s"收到来自${sender}的消息: $msg")
  }
}



object LaunchMaster {
  val logger = LoggerFactory.getLogger(getClass)

  val LocalIP = "127.0.0.1"

  def main(args: Array[String]): Unit = {
    val systemConfigs = ConfigFactory.load()
      .withFallback(ConfigFactory.parseString(s"""akka.remote.netty.tcp.hostname = "$LocalIP" """))
      .withFallback(ConfigFactory.parseString(s""" akka.remote.netty.tcp.port = 2553 """))
      .resolve()

    logger.info(s"akka.remote.netty.tcp.port = ${systemConfigs.getInt("akka.remote.netty.tcp.port")}")

    val systemName = systemConfigs.getString("akka.cluster.cluster-name")

    val system = ActorSystem(systemName, systemConfigs)
    val master = system.actorOf(Props(classOf[Master]), "master")

    println("Master actor is started ....")
  }
}


