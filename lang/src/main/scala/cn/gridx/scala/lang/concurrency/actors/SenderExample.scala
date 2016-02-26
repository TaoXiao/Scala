package cn.gridx.scala.lang.concurrency.actors

import scala.actors.Actor

/**
  * Created by tao on 2/25/16.
  *
  * 输出为
  *
      Master -> 执行命令: 向Worker发送消息: [今天天气不错,挺风和日丽的]
      Worker -> 收到了来自Master的消息 [今天天气不错,挺风和日丽的]
      Master -> 收到Worker的回复: [你好Master, 我是Worker]
  */
object SenderExample {
  def main(args: Array[String]): Unit = {
    val master = new Master
    val worker = new Worker

    master.start
    worker.start

    master ! ("cmd:send", "今天天气不错,挺风和日丽的", worker)
  }
}

class Master extends Actor {
  override def act() = {
    while (true) {
      receive {
        case ("cmd:send", msg, actor: Actor) =>
          println(s"Master -> 执行命令: 向Worker发送消息: [$msg]")
          actor ! msg
        case ("resp", msg) =>
          println(s"Master -> 收到Worker的回复: [$msg] ")
        case _ =>
          println(s"Master -> 无法识别")
      }
    }
  }
}

class Worker extends Actor {
  override def act(): Unit = {
    while (true) {
      receive {
        case msg: String =>
          println(s"Worker -> 收到了来自Master的消息 [$msg]")
          sender ! ("resp", "你好Master, 我是Worker")
      }
    }
  }
}