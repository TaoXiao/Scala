package cn.gridx.scala.akka.ha

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

/**
  * Created by tao on 9/20/16.
  */
object WorkerLauncher {
  val log = LoggerFactory.getLogger("WorkerLauncher")

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load("worker.conf")
    val akkaname = config.getString("realtime.akkaname")
    log.info(s"akkaname = $akkaname")

    implicit val system = ActorSystem(akkaname, config)
    system.actorOf(Props[Worker])

    log.info("Worker is launched ")
  }
}
