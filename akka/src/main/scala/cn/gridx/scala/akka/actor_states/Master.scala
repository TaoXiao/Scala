package cn.gridx.scala.akka.actor_states

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.cluster.{Cluster, ClusterEvent}
import akka.cluster.ClusterEvent.{MemberExited, MemberRemoved, MemberUp, UnreachableMember}


/**
  * Created by tao on 7/31/16.
  */
class Master extends Actor{
  val cluster = Cluster(context.system)
  var seed: ActorRef = null


  /**
    * 订阅系统的消息
    * */
  override def preStart(): Unit = {
    cluster.subscribe(self, ClusterEvent.initialStateAsEvents,
      classOf[MemberUp],
      classOf[UnreachableMember],
      classOf[MemberExited],
      classOf[MemberRemoved])
    super.preStart()
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }


  override def receive: Receive = {
    case MemberUp(member) =>
      println(s"[MemberUp] 《《《《《 member = $member, roles = ${member.getRoles}  ")

    case UnreachableMember(member) =>
      println(s"[UnreachableMember] 《《《《《 member = $member")

    case MemberExited(member) =>
      println(s"[MemberExited]《《《《《 member = $member")

    case MemberRemoved(member, previousStatus) =>
      println(s"[MemberRemoved]《《《《《 member = $member, previousStatus = $previousStatus")
  }
}



object Master {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem
  }
}