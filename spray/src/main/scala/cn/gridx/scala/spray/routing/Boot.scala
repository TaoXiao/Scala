package cn.gridx.scala.spray.routing

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import scala.concurrent.duration._
import akka.pattern.ask

/**
  * Created by tao on 2/29/16.
  *
  * 直接运行Boot即可
  *
  */
object Boot extends App {
  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start out service actor
  val basicService = system.actorOf(Props[BasicServiceActor], "basic-service")
  val paramService = system.actorOf(Props[ParamServiceActor], "param-service")

  implicit val timeout = Timeout(2.seconds)  // 引入包 scala.concurrent.duration._ 才能使用seconds

  /**
    * 使用问号 ? 需要引入包 akka.pattern.ask
    *
    * start a new HTTP server on port 8881 with our service actor as the handler
    * */
  IO(Http) ? Http.Bind(basicService, interface = "localhost", port = 8881)
  IO(Http) ? Http.Bind(paramService, interface = "localhost", port = 8882)

}
