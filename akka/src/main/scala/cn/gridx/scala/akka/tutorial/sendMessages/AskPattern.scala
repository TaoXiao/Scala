package cn.gridx.scala.akka.tutorial.sendMessages

import java.util.Date

import akka.actor.{Props, ActorSystem, Actor}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Random, Failure, Success}
import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by tao on 3/6/16.
  */
object AskPattern {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Ask-Pattner-System")
    val actorA = system.actorOf(Props[ActorA])
    val actorB = system.actorOf(Props[ActorB])
    val actorC = system.actorOf(Props[ActorC])

    /**
      * 以ask模式来向一个actor发送消息时,需要一个 implicit `akka.util.Timeout`
      * */
    implicit val timeout = Timeout(20 seconds)

    /**
      * 向三个actor以ask的方式发送消息,并且等待它们的回复消息
      *
      * 由于f的类型是`Future`,所以在`for`循环中只能用ask而不能用tell
      * 因为tell不会返回Future
      *
      * 从运行结果来看, 这几个actor收到消息的顺序是 actorA -> actorB -> actorC
      * 为什么 ?? 这是由 for 结构决定的
      * */
    val f: Future[Result] =
      for {
        a <- ask(actorA, RequestMsg("你好!")).mapTo[String]
        b <- (actorB ? RequestMsg("既来之, 则安之.")).mapTo[Int]
        c <- (actorC ? RequestMsg("再见.")).mapTo[String]
      } yield Result(a, b, c)

    /**
      * 等待Future运行完成后, Success/Failure才能被调用
      * */
    f onComplete {
      case Success(value) =>
        println(s"[${new Date}] Success - 结果是 ${value}")
        system.shutdown
      case Failure(ex) =>
        println(s"[${new Date}] Failure - 异常为 $ex")
        system.shutdown
    }

    println(s"[${new Date}] 我还在main中")
    println(s"[${new Date}] 你们应该会先看到我,再看到各个actors返回的结果")
  }

  trait Msg
  case class RequestMsg(info: String) extends Msg {}
  case class Result(a: String, b: Int, c: String) extends Msg {}


  class ActorA extends Actor {
    def receive = {
      case RequestMsg(info) =>
        println(s"[${new Date}] I'm Actor A ")
        Thread.sleep(Random.nextInt(6000))
        sender ! s"[Actor A] $info"
    }
  }

  class ActorB extends Actor {
    def receive = {
      case RequestMsg(info) =>
        println(s"[${new Date}] I'm Actor B ")
        Thread.sleep(Random.nextInt(6000))
        sender ! 10086
    }
  }

  class ActorC extends Actor {
    def receive = {
      case RequestMsg(info) =>
        println(s"[${new Date}] I'm Actor C ")
        Thread.sleep(Random.nextInt(6000))
        sender ! s"[Actor C] $info"
    }
  }

}




