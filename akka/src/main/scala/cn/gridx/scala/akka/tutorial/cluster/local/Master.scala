package cn.gridx.scala.akka.tutorial.cluster.local

import akka.actor.{Actor, ActorLogging, ActorPath, ActorRef, ActorSystem, Address, Props}
import akka.cluster.{Cluster, ClusterEvent}
import com.typesafe.config.ConfigFactory

/**
  * Created by tao on 6/19/16.
  */
class Master extends Actor with ActorLogging {
  import ClusterEvent._

  private var addresses = Set.empty[Address]
  private var workers = IndexedSeq.empty[ActorRef] //  记录下所有的workers

  // master actor 将订阅系统的消息
  Cluster(context.system).subscribe(self, InitialStateAsEvents, classOf[MemberEvent])

  override def receive: Receive = {
    case RegisterWorker =>
      val actor: ActorRef = sender()
      // 输出  actorPath = akka.tcp://XtAkkaClusterSystem@127.0.0.1:2551/user/worker
      //      actorPath = akka.tcp://XtAkkaClusterSystem@127.0.0.1:1999/user/worker   等等
      println(s"\n收到了RegisterWorker的消息, actorPath = ${actor.path} \n")
      registerWorker(actor)
      actor ! AckMsg(actor.path)

    case MemberUp(member) =>
      addresses += member.address
      println(s"\n收到了MemberUp($member)消息, 现在所有成员的Address为: \n${addresses.mkString("\n")} \n")

    case MemberExited(member) =>
      addresses -= member.address
      println(s"\n收到了MemberExited($member)消息, 现在所有成员的Address为: \n${addresses.mkString("\n")} \n")
  }


  def registerWorker(actor: ActorRef): Unit = {
    if (!workers.contains(actor)) {
      context watch actor // watch 有什么用 ??
      workers = workers :+ actor
      // 输出  添加了新的worker :  它的PATH = akka.tcp://XtAkkaClusterSystem@127.0.0.1:2551/user/worker
      // 注意: 一个actor的
      println(s"\n添加了新的worker : 它的PATH = ${actor.path}\n")
    }
  }
}


case object Master extends App {
  val config = ConfigFactory.parseString("akka.cluster.roles=[MASTER]")
        .withFallback(ConfigFactory.load()) //

  val system = ActorSystem("XtAkkaClusterSystem", config)

  system.actorOf(Props[Master], name = "master")
}


// 用于向worker确认已经上线的回复消息
case class AckMsg(addr: ActorPath)
