package cn.gridx.scala.akka.app.crossfilter

import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.cluster.ClusterEvent.{MemberEvent, MemberRemoved, MemberUp, UnreachableMember}
import akka.cluster.{Cluster, ClusterEvent}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.concurrent.{Await, Future}

/**
  * Created by tao on 7/18/16.
  */
class Listener extends Actor {
  val logger = LoggerFactory.getLogger(this.getClass)
  val cluster = Cluster(context.system)

  logger.info(s"listener已启动, path = ${self.path}\n")

  override def preStart(): Unit = {
    cluster.subscribe(self, ClusterEvent.initialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  override def receive = {
    case MemberUp(member) =>
      logger.info(s"消息 [MemberUp]  member = ${member.toString()}\n")

    case UnreachableMember(member) =>
      logger.info(s"消息 [UnreachableMember] member = ${member.toString()}\n")

    case MemberRemoved(member, previousStatus) =>
      logger.info(s"消息 [MemberRemoved] member = ${member.toString()}\n")

    case _ =>
      logger.info(s"未知的消息\n")
  }

  private def lookupMaster(): Unit = {
    // import scala.concurrent.ExecutionContext.Implicits.global
    implicit val timeout = Timeout(10 seconds)

    val masterHost = context.system.settings.config.getString("cluster.master")
    logger.info(s"master host = $masterHost")

    val f: Future[ActorRef] = context.system.actorSelection("akka.tcp://CrossFilterSystem@127.0.0.1:3000/user/master").resolveOne()
    val r: ActorRef = Await.result(f, timeout.duration)
    logger.info(r.path.toString)
  }
}

object Listener extends App {
  val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=3001")
    .withFallback(ConfigFactory.parseString(s"akk.cluster.roles=[${Roles.LISTENER}]"))
    .withFallback(ConfigFactory.load())
  val system = ActorSystem("CrossFilterSystem", config)
  system.actorOf(Props[Listener], name = "listener")
}
