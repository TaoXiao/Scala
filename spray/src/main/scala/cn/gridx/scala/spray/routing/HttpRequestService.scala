package cn.gridx.scala.spray.routing

import akka.actor.{Actor, ActorLogging}
import spray.can.Http
import spray.http.{HttpEntity, HttpRequest, HttpResponse}
import spray.http.HttpMethods.GET
import spray.http.MediaTypes._

/**
  * Created by tao on 9/5/16.
  */
class HttpRequestService extends Actor with ActorLogging {
  def actorRefFactory = context

  override def receive = {
    // when a new connection comes in we register ourselves as the connection handler
    // 一定需要这一步
    case _: Http.Connected =>
      log.info("收到 `Http.Connected` ")
      sender() ! Http.Register(self)

    // 收到请求后构造一个HttpResponse
    case HttpRequest(GET, path, headers, entity, protocol) =>
      val msg = s"收到GET请求, \n\theaders = ${headers}, entity = ${entity}, protocol = ${protocol}"
      log.info(msg)
      sender() ! GenHttpResp(msg)
  }


  def GenHttpResp(msg: String) = HttpResponse(
    entity = HttpEntity(`text/html`,
      <html>
        <body>
          <h1>Header 1</h1>
          <h2>$msg</h2>
        </body>
      </html>
        .toString()
    )
  )
}
