package cn.gridx.scala.akka.tutorial.cluster.local

import java.util.Currency

import akka.actor.{Actor, ActorSystem, Props, RootActorPath}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.{Cluster, Member}
import com.typesafe.config.ConfigFactory

/**
  * Created by tao on 6/19/16.
  */
class Worker extends  Actor {
  val cluster = Cluster(context.system)
  var master: Option[Member] = None

  // 订阅系统的`MemberUp`事件, 以便于找到Master
  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberUp])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  override def receive: Receive = {
    // case 1
    case MemberUp(node) =>
      println(s"\n收到了MemberUp的消息\n")
      registerSelf(node)

    // case 2
    case state: CurrentClusterState =>
      println(s"\n收到了CurrentClusterStated的消息, state = $state\n")
      state.members.filter(_.status == MemberUp).foreach(registerSelf)

    case AckMsg(actorPath) =>
      println(s"\n收到了AckMsg的消息 (actorPath = $actorPath)\n")
  }


  /**
    * Worker向Master注册自己
    * */
  def registerSelf(member: Member): Unit = {
    println(s"\nRole = ${member.roles.mkString(",")} \n")
    if (member.hasRole("MASTER")) { // 根据role来判断它是不是master
      master = Some(member)
      // 输出 Master的actor path = akka.tcp://XtAkkaClusterSystem@127.0.0.1:3000/
      println(s"\nMaster的actor path = ${RootActorPath(member.address)}\n")
      // 找到master的ActorRef, 并向它注册本worker
      // 注意master的actor path写法, 要求用`system.actorOf(Props[Master], name = "master")`创建master
      context.actorSelection(RootActorPath(member.address)/"user"/"master") ! RegisterWorker
    }
  }

}

object Worker extends App {
  // 这里可以让每一个worker选择各自的port来运行
  val port = if (args.isEmpty) "0" else args(0)

  val config =
    ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles=[WORKER]"))
      .withFallback(ConfigFactory.load())

  val system = ActorSystem("XtAkkaClusterSystem", config)

  val clusterStateListener = system.actorOf(Props[Worker], name = "worker")
}

case class RegisterWorker()
