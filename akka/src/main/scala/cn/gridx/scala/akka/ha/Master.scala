package cn.gridx.scala.akka.ha

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, ClusterEvent, Member, MemberStatus}
import akka.event.Logging.LoggerInitialized
import akka.event.LoggingAdapter
import cn.gridx.scala.akka.ha.Messages._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await

/**
  * Created by tao on 9/20/16.
  */
class Master extends Actor with ActorLogging{
  log.info("Master starting up ...")

  val cluster = Cluster(context.system)
  var worker: ActorRef  = _
  var heartBeatCount = 0

  override def preStart(): Unit = {
    cluster.subscribe(self, ClusterEvent.initialStateAsEvents,
      classOf[MemberUp],
      classOf[UnreachableMember],
      classOf[ReachableMember],
      classOf[MemberExited],
      classOf[MemberRemoved],
      classOf[LeaderChanged])
    log.info(s"I am ${self}")

    context.system.scheduler.schedule(
      initialDelay = 2 seconds,
      interval = 5 seconds,
      receiver = self,
      message = SyncUp()
    )

    super.preStart()
  }


  override def postStop(): Unit = {
    super.postStop()
    cluster.unsubscribe(self)
  }

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info(s"【MemberUp】 : member = $member")

    case UnreachableMember(member) =>
      log.info(s"【UnreachableMember】 : member = $member")

    case ReachableMember(member) =>
      log.info(s"【ReachableMember】 : member = $member")

    case MemberExited(member) =>
      log.info(s"【MemberExited】 : member = $member")

    case MemberRemoved(member, previousStatus) =>
      log.info(s"【MemberRemoved】 : member = $member, previousStatus = $previousStatus")

    case LeaderChanged(leader) =>
      log.info(s"【LeaderChanged】 : leader = $leader")

    case ClusterShuttingDown =>
      log.info(s"[ClusterShuttingDown]: ")

    case msg: Register =>
      worker = sender()
      log.info(s"【Register】${worker} is registered")

    case msg: SyncUp =>
      // log.info("[SyncUp]")
      if (worker != null) {
        worker ! HeartBeat(heartBeatCount)
        heartBeatCount += 1
      }

    case msg: PoolingMaster =>
      log.info(s"[PoolingMaster]: #${msg.no}")

    case msg: HeartBeat =>
      log.info(s"[HeartBeat]: #${msg.no}")

    case _ =>
      log.info(s"unrecognizable")
  }
}
