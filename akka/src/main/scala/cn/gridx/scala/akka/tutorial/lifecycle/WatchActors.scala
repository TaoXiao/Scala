package cn.gridx.scala.akka.tutorial.lifecycle

import akka.actor._

/**
  * Created by tao on 3/8/16.
  *
  * 运行输出为:

    向kenny发送命令`PoisonPill`
    向jack发送命令`Stop`
    向lucy发送命令`Kill`
    [Child] Jack Chen will stop itself
    [Parent] OMG, kenny挂了
    [Parent] OMG, jack挂了
    [Parent] OMG, lucy挂了
    [ERROR] [03/08/2016 17:00:48.942] [Watch-Actors-Example-akka.actor.default-dispatcher-5] [akka://Watch-Actors-Example/user/tommy/lucy] Kill (akka.actor.ActorKilledException)


  */
object WatchActors {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Watch-Actors-Example")
    val parent = system.actorOf(Props[Parent], "tommy")

    // 找到各个child actors
    val kenny = system.actorSelection("/user/tommy/kenny")
    val jack  = system.actorSelection("/user/tommy/jack")
    val lucy  = system.actorSelection("/user/tommy/lucy")

    println("向kenny发送命令`PoisonPill`")
    kenny ! PoisonPill

    println("向jack发送命令`Stop`")
    jack ! "Stop"

    println("向lucy发送命令`Kill`")
    lucy ! Kill

    Thread.sleep(5000)
    system.shutdown
  }

  class Parent extends Actor {
    // parent生成一个child actor,然后监控它被stop或者kill的事件
    val kenny = context.actorOf(Props(classOf[Child], "Kenny Lee"), "kenny")
    val jack  = context.actorOf(Props(classOf[Child], "Jack Chen"), "jack")
    val lucy  = context.actorOf(Props(classOf[Child], "Lucy K"),    "lucy")
    context.watch(kenny)
    context.watch(jack)
    context.watch(lucy)

    def receive = {
      case Terminated(actor) =>
        println(s"[Parent] OMG, ${actor.path.name}挂了")
      case _ =>
        println("[Parent] Parent got an unknown message")
    }
  }

  class Child(name: String) extends Actor {
    def receive = {
      case "Stop" =>
        println(s"[Child] $name will stop itself")
        context stop self
      case PoisonPill =>
        println(s"""[Child] $name got a "PoisonPill" message """)
      case Kill =>
        println(s"""[Child] $name got a "Kill" message """)
      case _ =>
        println(s"[Child] $name got an unknown message")
    }
  }
}
