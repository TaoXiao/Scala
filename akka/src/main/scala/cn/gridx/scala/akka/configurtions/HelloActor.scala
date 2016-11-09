package cn.gridx.scala.akka.configurtions

import akka.actor.{Actor, ActorLogging}

/**
  * Created by tao on 8/3/16.
  */
class HelloActor extends Actor with ActorLogging {
  override def receive = {
    case MsgTest(msg) =>
      log.info(s"收到消息[MsgTest], msg = ${msg} !")
    case _ =>
      log.error("未知的消息类型 !")
  }
}

final case class MsgTest(msg: String)

