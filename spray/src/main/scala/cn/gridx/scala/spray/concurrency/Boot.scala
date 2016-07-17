package cn.gridx.scala.spray.concurrency

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout
import spray.can.Http

import akka.pattern.ask
import scala.concurrent.duration._

/**
  * Created by tao on 7/12/16.
  */
object Boot extends App {
  implicit val system = ActorSystem("concurrency_test")

  val service = system.actorOf(Props[ServiceActor], "The-Service-Actor")

  implicit val timeout = Timeout(1 seconds)

  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8813)
}
