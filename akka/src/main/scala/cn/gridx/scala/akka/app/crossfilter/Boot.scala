package cn.gridx.scala.akka.app.crossfilter

import akka.actor.{ActorSystem, Props}
import akka.io.IO

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http

/**
  * Created by tao on 7/17/16.
  */
object Boot extends App {
  implicit val system = ActorSystem("Spray")
  implicit val timeout = Timeout(30 seconds)

  val service = system.actorOf(Props[ServiceActor], "SprayService")
  IO(Http) ? Http.Bind(service, "localhost", 8100)
}
