package cn.gridx.scala.akka.tutorial.cluster.local

import akka.actor.{Actor, ActorLogging, ActorSystem, Address, Props}
import akka.cluster.{Cluster, ClusterEvent}
import com.typesafe.config.ConfigFactory

/**
  * Created by tao on 6/19/16.
  */
class Master extends Actor with ActorLogging {
  import ClusterEvent._

  private var addresses = Set.empty[Address]

  // master actor 将订阅系统的消息
  Cluster(context.system).subscribe(self, InitialStateAsEvents, classOf[MemberEvent])

  override def receive: Receive = {
    case MemberUp(member) =>
      addresses += member.address
      println(s"\n收到了MemberUp($member)消息, 现在所有成员的Address为: \n${addresses.mkString("\n")} \n")
      sender() ! AckMsg(member.address)

    case MemberExited(member) =>
      addresses -= member.address
      println(s"\n收到了MemberExited($member)消息, 现在所有成员的Address为: \n${addresses.mkString("\n")} \n")
  }
}


case object Master extends App {
  val config = ConfigFactory.load() //

  val system = ActorSystem("XtAkkaClusterSystem", config)

  val clusterStateListener = system.actorOf(Props[Master])
}


// 用于向worker确认已经上线的回复消息
case class AckMsg(addr: Address)
