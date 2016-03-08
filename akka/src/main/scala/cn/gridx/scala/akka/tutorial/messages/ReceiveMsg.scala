package cn.gridx.scala.akka.tutorial.messages

import java.util.Date
import akka.actor.{Props, ActorSystem, ReceiveTimeout, Actor}
import scala.concurrent.duration._

/**
  * Created by tao on 3/7/16.
      输出为

      [Mon Mar 07 21:36:14 CST 2016] Main: 启动了actor, sleep 10秒 ...
      [Mon Mar 07 21:36:15 CST 2016] Actor A :  #1 ReceiveTimeout exception
      [Mon Mar 07 21:36:16 CST 2016] Actor A :  #2 ReceiveTimeout exception
      [Mon Mar 07 21:36:17 CST 2016] Actor A :  #3 ReceiveTimeout exception
      [Mon Mar 07 21:36:24 CST 2016] Main: 醒来, 现在shutdown system

  */
object ReceiveMsg {
  def main(args: Array[String]): Unit = {
    /**
      * 创建并启动一个actor, 但是不向它发送任何消息
      * */
    val system = ActorSystem("Receive-Timeout-Example")
    system.actorOf(Props[ActorA])

    // 主线程睡眠10秒钟
    println(s"[${new Date}] Main: 启动了actor, sleep 10秒 ...")
    Thread.sleep(10000)
    println(s"[${new Date}] Main: 醒来, 现在shutdown system")

    // 关闭 actor system
    system.shutdown
  }

  trait Msg
  case class RequestMsg(msg: String) {}

  class ActorA extends Actor {
    // 计数: 第几次收到`ReceiveTimeout`
    var i = 1

    // 为该actor设置超时时间
    context.setReceiveTimeout(1 seconds)

    /**
      * `receive`是一个偏函数,它的类型是PartialFunction[Any, Unit]
      * Scala中的"match/case clause"就是一个偏函数
      *
      * 如果该actor在设定的超时时间内没有收到任何消息,
      * 则系统会自动向它发送一个`ReceiveTimeout`消息
      * */
    def receive: PartialFunction[Any, Unit] = {
      case RequestMsg(msg) =>
        println(s"[${new Date}] Actor A : received a message [$msg]")
        sender ! "Hello"
      case ReceiveTimeout =>
        println(s"[${new Date}] Actor A :  #$i ReceiveTimeout exception")
        if (i >= 3) // 传入`Duration.Undefined`可以取消接收消息超时的机制
          context.setReceiveTimeout(Duration.Undefined)
        else
          i += 1
    }
  }

}
