package cn.gridx.scala.akka.ha

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import org.apache.log4j.{Level, Logger}
import org.slf4j.LoggerFactory

/**
  * Created by tao on 9/20/16.
  */
object MasterLauncher {
  val log = LoggerFactory.getLogger("MasterLauncher")

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load("master.conf")
    val akkaname = config.getString("realtime.akkaname")
    val mastername = config.getString("akka.master.actorname")
    log.info(s"akkaname = $akkaname, master actor name = $mastername")



    implicit val system = ActorSystem(akkaname, config)
    system.actorOf(Props[Master], mastername)

    log.info("Master is launched ")
  }
}
