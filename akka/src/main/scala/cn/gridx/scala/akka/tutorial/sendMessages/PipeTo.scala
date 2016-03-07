package cn.gridx.scala.akka.tutorial.sendMessages

import java.util.Date

import akka.actor.{Props, Actor, ActorSystem}
import akka.util.Timeout
import akka.pattern.{ask, pipe}

import scala.concurrent.Future
import scala.util.{Failure, Success, Random}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by tao on 3/7/16.
  *
  运行结果为
    [Mon Mar 07 10:23:48 CST 2016] Actor A : I received a message - [世界是你们的, 也是我们的]
    [Mon Mar 07 10:23:48 CST 2016] 我还在main中
    [Mon Mar 07 10:23:48 CST 2016] 你们也许会先看到我,再看到各个actors返回的结果
    [Mon Mar 07 10:23:54 CST 2016] Actor A : Replying to the sender
    [Mon Mar 07 10:23:54 CST 2016] Actor B : I received a message - [但归根结底还是你们的]
    [Mon Mar 07 10:23:57 CST 2016] Actor B : Replying to the sender
    [Mon Mar 07 10:23:57 CST 2016] Actor C 成功地收到了消息
    [Mon Mar 07 10:24:07 CST 2016] Actor C : I received a message - a = [大家好, 我是 Actor A], b = [10010]

  注意观察最后两行的输出顺序
  */
object PipeTo {
  implicit val timeout = Timeout(10 seconds)

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Pipe-To-Example")
    val actorA = system.actorOf(Props[ActorA])
    val actorB = system.actorOf(Props[ActorB])
    val actorC = system.actorOf(Props[ActorC])

    val f: Future[Result] =
      for {
        a <- ask(actorA, RequestMsg("世界是你们的, 也是我们的")).mapTo[String]
        b <- (actorB ? RequestMsg("但归根结底还是你们的")).mapTo[Int]
      } yield Result(a, b)

    /**
      *
      * 当f代表的计算完成后,将f发送给actorC
      * 一旦actorC收到了f(而不是等actorC运行完成), 则会触发`onComplete``
      */
    (f pipeTo actorC).onComplete {
        case Success(value) =>
          println(s"[${new Date}] Actor C 成功地收到了消息")
          system.shutdown
        case Failure(ex) =>
          println(s"[${new Date}] Actor C 发生了异常")
          system.shutdown
    }

    println(s"[${new Date}] 我还在main中")
    println(s"[${new Date}] 你们也许会先看到我,再看到各个actors返回的结果")
  }



  trait Msg
  case class RequestMsg(resp: String) extends Msg {}
  case class Result(a: String, b: Int) extends Msg {}

  class ActorA extends Actor {
    def receive = {
      case RequestMsg(req) =>
        println(s"[${new Date}] Actor A : I received a message - [$req]")
        Thread.sleep(Random.nextInt(10000))
        println(s"[${new Date}] Actor A : Replying to the sender")
        sender ! "大家好, 我是 Actor A"
      case _ =>
        println(s"[${new Date}] Actor A : unexpected message received !")
    }
  }

  class ActorB extends Actor {
    def receive = {
      case RequestMsg(req) =>
        println(s"[${new Date}] Actor B : I received a message - [$req]")
        Thread.sleep(Random.nextInt(10000))
        println(s"[${new Date}] Actor B : Replying to the sender")
        sender ! 10010
      case _ =>
        println(s"[${new Date}] Actor B : unexpected message received !")
    }
  }

  class ActorC extends Actor {
    def receive = {
      case Result(a, b) =>
        Thread.sleep(10000)
        println(s"[${new Date}] Actor C : I received a message - a = [$a], b = [$b]")
      case _ =>
        println(s"[${new Date}] Actor C : unexpected message received !")
    }
  }
}
