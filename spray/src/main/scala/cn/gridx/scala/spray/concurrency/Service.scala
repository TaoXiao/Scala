package cn.gridx.scala.spray.concurrency

import java.util.Date

import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.Actor
import spray.routing.HttpService

import scala.concurrent.Future

/**
  * Created by tao on 7/12/16.
  */

class ServiceActor extends Actor with Service {
  override def actorRefFactory = context
  override def receive = runRoute(route)
}

trait Service extends HttpService {
  var count = 0
  val route = path("spray" / "test" / "concurrency") {
    get {
      complete {
        Future {
          println(s"${new Date()}  收到请求")
          Thread.sleep(10000)
          "Hello"
        }
      }
    }
  }
}
