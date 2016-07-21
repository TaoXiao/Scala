package cn.gridx.scala.akka.app.crossfilter

import akka.actor.{ActorSystem, Props}
import akka.io.IO

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http

/**
  * Created by tao on 7/17/16.
  */
object Boot extends App {
  val config = ConfigFactory.parseString("akka.cluster.roles=[SPRAY]")
    .withFallback(ConfigFactory.load())
  implicit val system = ActorSystem("CrossFilterSystem")
  implicit val timeout = Timeout(30 seconds)

  val service = system.actorOf(Props[ServiceActor], "spray")
  IO(Http) ? Http.Bind(service, "localhost", 8101)
}
