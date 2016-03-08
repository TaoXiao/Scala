package cn.gridx.scala.akka.tutorial.lifecycle

import akka.actor.{Props, ActorRef, ActorSystem, Actor}

/**
  * Created by tao on 3/7/16.
  */
object StopActors {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Stop-Actors-Example")
    val actorA = system.actorOf(Props(classOf[ActorA], 5))

    Thread.sleep(1000)
    actorA ! "Spawn"

    Thread.sleep(2000)
    actorA ! "Halt"

    Thread.sleep(10000)
    system.shutdown
  }

  trait Msg
  case class RequestMsg() extends Msg

  class ActorA(childrenNum: Int) extends Actor {
    val children: Array[ActorRef] = new Array[ActorRef](childrenNum)

    def receive = {
      case "Spawn" =>
        println("[Actor A] 即将创建children actors")
        for (i <- 0 until children.size) // 为自己创建若干个child actors
          children(i) = context.actorOf(Props(classOf[ActorB], i))
        println(s"[Actor A] ${childrenNum}个children actors创建完毕")

      case "Halt" =>
        children.map(context.stop)  // 先把children都终止,这一步实际上是多余的,因为stop自身前会自动地stop所有的children
        context stop self           // 再终止自身
    }

    override def preStart = {
      println("I'm ActorA, I will be started !")
    }

    override def postStop = {
      println("I'm ActorA, I am stopped !")
    }
  }

  class ActorB(n: Int) extends Actor {
    def receive = {
      case _ => println("[Actor B] 这是啥消息,我也不知道")
    }

    override def preStart = {
      println(s"I'm ActorB-#$n, I will be started !")
    }

    override def postStop = {
      println(s"I'm ActorB-#$n, I am stopped !")
    }
  }
}
