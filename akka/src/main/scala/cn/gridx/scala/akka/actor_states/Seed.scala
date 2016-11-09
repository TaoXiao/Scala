package cn.gridx.scala.akka.actor_states

import akka.actor.{Actor, ActorSystem, Props}
import akka.cluster.ClusterEvent.{MemberExited, MemberRemoved, MemberUp, UnreachableMember}
import akka.cluster.{Cluster, ClusterEvent}

/**
  * Created by tao on 7/31/16.
  */
class Seed extends Actor {
  val cluster = Cluster(context.system)

  /**
    * 订阅系统的消息
    * */
  override def preStart(): Unit = {
    cluster.subscribe(self, ClusterEvent.initialStateAsEvents,
      classOf[MemberUp],
      classOf[UnreachableMember],
      classOf[MemberExited],
      classOf[MemberRemoved])
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
      println(s"[MemberExited] 《《《《《 member = $member")
    case MemberRemoved(member, previousStatus) =>
      println(s"[MemberRemoved] 《《《《《 member = $member, previousStatus = $previousStatus")
  }
}

object Seed {
  def main(args: Array[String]): Unit = {
  }
}
