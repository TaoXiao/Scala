package cn.gridx.scala.spray.routing

import akka.actor.Actor
import spray.http.{MediaTypes, MediaType}
import spray.routing.{RequestContext, Route, HttpService}

/**
  * Created by tao on 2/29/16.
  */
class ParamServiceActor extends Actor with ParamService{
  override def actorRefFactory = context
  override def receive = runRoute(myRoute)
}

trait ParamService extends HttpService {
  // url: /usa/gridx/10010/10086
  val paramMatcher1 =
    path("usa" / "gridx" / IntNumber / """\d+""".r) {
      (a, b) => {
        get {
          complete {
            // a = [10010], b = [10086], b的类型为[java.lang.String]
            s"a = [$a], b = [$b], b的类型为[${b.getClass.getCanonicalName}]"
          }
        }
      }
    }

  // url:  /usa/gridx/10010/foo10086
  val paramMatcher2 =
    path("usa" / "gridx" / IntNumber / """foo(\d+)""".r) {
      (a, b) => {
        get {
          complete {
            //a = [10010], b = [10086], b的类型为[java.lang.String]
            s"a = [$a], b = [$b], b的类型为[${b.getClass.getCanonicalName}]"
          }
        }
      }
    }

  // url:  /usa/gridx/i100/h200
  val paramMatcher3 =
    path("usa" / "gridx" / "i" ~ IntNumber / "h" ~ HexIntNumber) {
      (a, b) => {
        get {
          complete {
            // a = [100], b = [512], b的类型为[int]
            s"a = [$a], b = [$b], b的类型为[${b.getClass.getCanonicalName}]"
          }
        }
      }
    }

  /**
    * URL中带有参数,分别以 GET 和 POST 为例
    *
    * 如果想同时获取到POST请求中的payload,及其URL中的parameters,该怎么写?
    *
    * GET - http://localhost:8882/usa/gridx/param/i100/h200?from=3.1415926&to=false
    * POST -
    * */
  val paramMatcher4 =
    path("usa" / "gridx" / "param" / "i" ~ IntNumber / "h" ~ HexIntNumber) {
      (a, b) => {
        get { // http://localhost:8882/usa/gridx/param/i100/h200?from=3.1415926&to=false
          parameters('from.as[Float], 'to.as[Boolean]) {
            (from, to) => {
              complete {
                // a = [100], b = [512], from = [3.1415925], to = [false]
                // from的类型为[float], to的类型为[boolean]
                s"a = [$a], b = [$b], from = [$from], to = [$to]\n" +
                  s"from的类型为[${from.getClass.getCanonicalName}], to的类型为[${to.getClass.getCanonicalName}]"
              }
            }
          }
        } ~
        post {
          parameters('from.as[Float], 'to.as[Boolean]) {
            (from, to) => {
               {
                payload => {
                  ""
                }
              }
            }
          }
        }
      }
    }


  // 处理Post请求, 并取得它的payload
  // 发送HTTP POST请求的命令:  curl -X POST -d "hello" http://localhost:8882/usa/gridx/post-arg/x1234
  val postParamMatcher =
    path("usa" / "gridx" / "post-arg"  / "x" ~ IntNumber) {
      (x) => {
        get {
          complete { "收到了GET请求" }
        } ~
        post {
          entity(as[String]) {
            payload => {
              complete {
                s"收到了Post请求, URL参数 x = [$x], payload = [$payload]"
              }
            }
          }
        }
      }
    }


  def optionalParam: Route = (path("removelist") & get & parameters('fkey.as[String]?)) { fkey =>
      val realKey = fkey.getOrElse("xxx")
      println(realKey)
      complete(realKey)
  }


  def noCtxRoute: Route = (path("noCtx") & get & parameters('fkey.as[String]?)) { fkey =>
    val realKey = fkey.getOrElse("xxx")
    println(realKey)
    complete(realKey)
  }

  val myRoute =
    /* paramMatcher1 ~ paramMatcher2 ~ paramMatcher3 ~ paramMatcher4 ~
      postParamMatcher ~ */
      optionalParam ~ noCtxRoute


}
