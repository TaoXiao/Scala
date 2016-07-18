package cn.gridx.scala.akka.app.crossfilter

import akka.actor.Actor
import spray.routing.HttpService

/**
  * Created by tao on 7/17/16.
  */

class ServiceActor extends Actor with Service {
  override def actorRefFactory = context
  override def receive = runRoute(route)
}

trait Service extends HttpService {
  val route = pathPrefix("crossfilter") {
    path ("start") {
      get {
        complete {
          Master.StartMaster("/disk2/app/crossfilter/data/sample.csv", 4)
          "master已启动"
        }
      }
    } ~ path ("calc") {
      get {
        complete {
          Master.StartCalculation()
        }
      }
    }
  }
}
