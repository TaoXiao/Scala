package cn.gridx.scala.spray.routing

import akka.actor.Actor
import spray.routing.HttpService

/**
  * Created by tao on 2/29/16.
  */
class BasicServiceActor extends Actor with BasicService {

  // the `HttpService` trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  override def actorRefFactory = context

  // this actor runs our route
  override def receive = runRoute(myRoute)
}


// this trait defines our service behavior independently from the service actor
trait BasicService extends HttpService {
  // 关于 routing
  // 参考[The PathMatcher DSL](http://spray.io/documentation/1.1.3/spray-routing/path-directives/pathmatcher-dsl/)
  val myRoute =
    path("") {    // 浏览器访问 http://localhost:8881
      get {       // 或者发送HTTP GET请求  curl -X GET -i http://localhost:8881
        complete {
          println("收到GET请求")
          "Hello, 大家好, 我是jj林俊杰" // 直接返回一个字符串
        }
      }
    } ~
    path("hello") { // 浏览器访问 http://localhost:8881/hello
      get {
        complete {
          "Hello大家好,我们又见面了!"
        }
      }
    } ~
    path("hello" / "bye") { // 浏览器访问  http://localhost:8881/hello/bye
      get {  // 路径不能直接写成 path("hello/bye")  否则无法访问: The requested resource could not be found.
        complete {
          "你好, 再见."
        }
      }
    } ~
    pathPrefix("aloha" / IntNumber / "nice" / HexIntNumber ) { // URL 前缀
      (dec, hex) => {
        path("path1") { // URL:  http://localhost:8881/aloha/100/nice/ab/path1
          get {
            complete {
              s"路径1: aloha/$dec/nice/$hex/path1"
            }
          }
        } ~
        path("path2") { // URL: http://localhost:8881/aloha/100/nice/abCD/path2
          get {
            complete {
              s"路径2: aloha/$dec/nice/$hex/path2"
            }
          }
        }
      }
    }
}
