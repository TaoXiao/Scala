package cn.gridx.scala.akka.management

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.collection.mutable.ListBuffer

/**
  * Created by tao on 7/31/16.
  */
class ManagedMaster(name: String, list: List[(Class[_], Receive)]) extends Actor {
  assert(null != list && !list.isEmpty)

  override def receive: Receive = __receiver

  // 真正的message handler
  private var __receiver: Receive = {
    case _: UnsupportedMsg =>
      println("Received Unsupported Message `UnsupportedMsg`")
  }

  // 用户可以为master添加自己的message handler
  private def addReceiver(clz: Class[_], handler: Receive) = {
    __receiver = __receiver orElse {
      case msg: Class[clz] => handler(msg)
    }
  }

  // 配置用户的message handlers
  for (t <- list) {
    addReceiver(t._1, t._2)
  }

  println(s"receive = $receive")
}



object ManagedMaster {
  def main(args: Array[String]): Unit = {
    println("hello")

    val list = ListBuffer[(Class[_], Receive)]()
    list.append((classOf[MsgSucceed], onMsgSucceed))
    list.append((classOf[MsgFailed], PartialFunction.apply(onMsgFailed)))

    val system = ActorSystem("TestManagedAkkaSystem")
    val master: ActorRef = system.actorOf(Props(classOf[ManagedMaster], "clever_master", list.toList))
    Thread.sleep(1000)

    master ! MsgSucceed("成功", 520)
    master ! UnsupportedMsg()
    println("bye")

  }


  final case class MsgSucceed(info: String, value: Int) extends MSG
  final case class MsgFailed(info: String, reason: Boolean) extends MSG

  def onMsgSucceed: PartialFunction[Any, Unit] = {
    case msg: MsgSucceed =>
      println(s"收到了消息MsgSucceed, info = ${msg.info}, value = ${msg.value}")
  }


  def onMsgFailed(_msg: Any): Unit = {
    val msg = _msg.asInstanceOf[MsgFailed]
    println(s"收到了消息MsgFailed, info = ${msg.info}, value = ${msg.reason}")
  }
}






