package cn.gridx.scala.spray.cors

import spray.http.HttpHeaders
import spray.http.HttpHeaders.`Access-Control-Allow-Credentials`
import spray.http.StatusCodes._
import spray.routing._

/**
  * Created by tao on 3/9/16.
  */
class SecurityService {

}


trait CorsDirectives {

  this: HttpService => def respondWithCORSHeaders(origin: String) =
    respondWithHeaders(
      HttpHeaders.RawHeader("Access-Control-Allow-Origin", origin),
      `Access-Control-Allow-Credentials`(true))


  def corsFilter(origin: List[String])(route: Route) = {
    options {
      route //let inside to handle
    } ~ {
      val originEmpty = (origin.isEmpty || origin.head == "*")
      optionalHeaderValueByName("Origin") {
        case None =>
          println("no origin, reject")
          complete(Forbidden, Nil, "Invalid origin")
        case Some(clientOrigin) =>
          if (originEmpty) {
            println(s"\nclientOrigin = $clientOrigin \n")
            respondWithCORSHeaders(clientOrigin)(route)
          }
          else if (clientOrigin != "*" && clientOrigin.trim != "" && origin.contains(clientOrigin)) {
            println("origin pass:" + clientOrigin)
            respondWithCORSHeaders(clientOrigin)(route)
          }
          else {
            println("invalid origin, reject")
            complete(Forbidden, Nil, "Invalid origin") // Maybe, a Rejection will fit better
          }
      }
    }
  }
}
