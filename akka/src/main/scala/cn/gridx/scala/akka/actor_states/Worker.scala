package cn.gridx.scala.akka.actor_states

import akka.actor.{Actor, ActorSystem, Props}
import akka.cluster.{Cluster, ClusterEvent}
import akka.cluster.ClusterEvent.{MemberExited, MemberRemoved, MemberUp, UnreachableMember}
import cn.gridx.scala.akka.actor_states.Master.MSG_CALC



/**
  * Created by tao on 7/31/16.
  */
class Worker extends Actor {
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
      println(s"[MemberUp] 《《《《《 member = $member, roles = ${member.getRoles} ")

    case UnreachableMember(member) =>
      println(s"[UnreachableMember] 《《《《《 member = $member")

    case MemberExited(member) =>
      println(s"[MemberExited] 《《《《《 member = $member")

    case MemberRemoved(member, previousStatus) =>
      println(s"[MemberRemoved] 《《《《《 member = $member, previousStatus = $previousStatus")

  }
}


object Worker {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("MonitorSystem")
    system.actorOf(Props(classOf[Worker]))

    println("worker 开始了")
  }
}


