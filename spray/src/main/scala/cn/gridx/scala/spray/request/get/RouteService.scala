package cn.gridx.scala.spray.request.get

import spray.http.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import spray.routing.{HttpService, Route}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by tao on 11/29/16.
  */
trait RouteService extends HttpService {
  this: Processor =>

  def route: Route = routeReceiveGetRequest

  def routeReceiveGetRequest = (path( "get" / "request") & get) { ctx =>
    // log.info("收到GET请求，等待3秒")
    Future { utils.blockingOp() }
    val result = "已完成， 返回！"
    // log.info("即将返回响应")
    ctx.complete(HttpResponse(StatusCodes.OK,
      HttpEntity(ContentTypes.`application/json`, result.getBytes())))
  }
}
