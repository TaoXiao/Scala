package cn.gridx.scala.akka.tutorial.lifecycle


import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.LoggingReceive

/**
  * Created by tao on 6/18/16.
  *
  * 测试Actor的 `preStart`, `postStop`
  *
  * 输出
[INFO] [06/18/2016 08:44:27.149] [TestLifeCycle-akka.actor.default-dispatcher-2] [akka://TestLifeCycle/user/$a] 进入MyActor的构造函数
[INFO] [06/18/2016 08:44:27.150] [TestLifeCycle-akka.actor.default-dispatcher-2] [akka://TestLifeCycle/user/$a] Actor[akka://TestLifeCycle/user/$a#-571363052]
[INFO] [06/18/2016 08:44:27.151] [TestLifeCycle-akka.actor.default-dispatcher-2] [akka://TestLifeCycle/user/$a] Now preStart
[INFO] [06/18/2016 08:44:27.151] [TestLifeCycle-akka.actor.default-dispatcher-2] [akka://TestLifeCycle/user/$a] Received [喀什]
即将关闭Actor System
[INFO] [06/18/2016 08:44:30.163] [TestLifeCycle-akka.actor.default-dispatcher-2] [akka://TestLifeCycle/user/$a] Now postStop

  */
object Hooks extends App {
  val system = ActorSystem("TestLifeCycle")
  val actor = system.actorOf(Props[MyActor])
  actor ! "喀什"

  Thread.sleep(3000)

  println("即将关闭Actor System")

  system.shutdown() // 调用`system.shutdown`之后actor才会terminate
}


class MyActor extends Actor with ActorLogging {
  log.info("进入MyActor的构造函数")
  log.info(context.self.toString())

  override def receive = LoggingReceive {
    case x => log.info(s"Received [$x]")
  }

  override def preStart() = {
    log.info("Now preStart")
  }

  override def postStop() = {
    log.info("Now postStop")
  }
}
