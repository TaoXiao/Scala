package cn.gridx.scala.spray.request.get

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.io.IO
import com.typesafe.config.ConfigFactory
import spray.can.Http

/**
  * Created by tao on 11/29/16.
  */
class Processor extends Actor with RouteService with ActorLogging {
  override def receive: Receive = runRoute(route)
  override def actorRefFactory  = context
}



object Launcher extends App {
  val configs = ConfigFactory.parseString("akka.remote.netty.tcp.port = 9030")
    .withFallback(ConfigFactory.load("application.conf"))
  implicit val system = ActorSystem("get_system", configs)
  val actor = system.actorOf(Props(classOf[Processor]), "Get_Processor")

  IO(Http) ! Http.Bind(actor, "127.0.0.1", 9040)
}
