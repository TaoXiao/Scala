package cn.gridx.scala.akka.ha

import java.io._

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection}
import akka.cluster.{Cluster, ClusterEvent}
import akka.cluster.ClusterEvent._
import akka.util.Timeout

import scala.concurrent.duration._
import cn.gridx.scala.akka.ha.Messages._
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import org.joda.time.DateTime

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Created by tao on 9/20/16.
  */
class Worker extends Actor with ActorLogging {
  log.info("Worker starting up ...")

  val AkkaName    = context.system.settings.config.getString("realtime.akkaname")
  val MasterHost  = context.system.settings.config.getString("akka.master.host")
  val MasterPort  = context.system.settings.config.getString("akka.master.port")
  val MasterName  = context.system.settings.config.getString("akka.master.actorname")
  val masterUrl   = s"akka.tcp://$AkkaName@$MasterHost:$MasterPort/user/$MasterName"
  val cluster     = Cluster(context.system)
  val masterSel   = context.actorSelection(masterUrl)
  var masterRef:ActorRef   = _
  var poolings = 1000

  override def preStart(): Unit = {
    cluster.subscribe(self, ClusterEvent.initialStateAsEvents,
      classOf[MemberUp],
      classOf[UnreachableMember],
      classOf[ReachableMember],
      classOf[MemberExited],
      classOf[MemberRemoved],
      classOf[LeaderChanged])

    context.system.scheduler.schedule(
      initialDelay = 2 seconds,
      interval = 5 seconds,
      receiver = self,
      message = SyncUp()
    )

    masterSel ! Register()

    super.preStart()
  }


  override def postStop(): Unit = {
    super.postStop()
    cluster.unsubscribe(self)
  }


  override def receive: Receive = {
    case MemberUp(member) =>
      log.info(s"【MemberUp】 : member = $member")
      if (member.hasRole("MASTER"))
        masterSel ! Register()

    case UnreachableMember(member) =>
      log.info(s"【UnreachableMember】 : member = $member")

    case ReachableMember(member) =>
      log.info(s"【ReachableMember】 : member = $member")

    case MemberExited(member) =>
      log.info(s"【MemberExited】 : member = $member")

    case MemberRemoved(member, previousStatus) =>
      log.info(s"【MemberRemoved】 : member = $member, previousStatus = $previousStatus")
      if (member.hasRole("MASTER"))
        log.info("master is removed")

    case LeaderChanged(leader) =>
      log.info(s"【LeaderChanged】 : leader = $leader")

    case msg: SyncUp =>
      masterSel ! PoolingMaster(poolings)
      poolings += 1

    case msg: HeartBeat =>
      log.info(s"[HeartBeat]: #${msg.no}")

    case _ =>
      log.info(s"【unrecognizable】")
  }


  private def ResolveMaster(url: String): ActorRef = {
    val masterSel = context.actorSelection(masterUrl)
    log.info("resolving master ... ")

    implicit val timeout = Timeout(5 seconds)
    var masterRef: Option[ActorRef] = None

    // poll master actor every 5 seconds
    while (masterRef.isEmpty) {
      val f = masterSel.resolveOne()
      f.onComplete {
        case Success(ref) =>
          masterRef = Some(ref)
        case _ =>
          log.info("master is not found yet!")
      }

      Thread.sleep(5000)
    }

    log.info(s"master ref is found: ${masterRef}")

    masterRef.get
  }


  private def DoHeavyJob(src: String, dst: String): Unit = {
    log.info("starting ...")

    val map = mutable.HashMap[Int, String]()
    var count = 0

    for (i <- Range(0, 2)) {
      log.info(s"#$i")

      val reader = new BufferedReader(new FileReader(src))
      val writer = new PrintWriter(new FileWriter(dst, true))
      var line: String = reader.readLine()

      while (line != null) {
        for (token <- line.split(",")) {
          map.put(count, token)
          count += 1
        }
        writer.println(line)
        line = reader.readLine()
      }

      reader.close()
      writer.close()
    }

    log.info("done ...")
  }


  private def Download(): Unit = {
    def Bucket = "gridx-realtime"
    def Key = "crossfilter/tariffs_huge/4M-prod-2016-08-02.4538689"

    val s3client = new AmazonS3Client(new ProfileCredentialsProvider())
    val s3Obj = s3client.getObject(
      new GetObjectRequest(Bucket, Key))

    val f = new File("/Users/tao/Downloads/store.dat." + new DateTime())

    if (!f.exists()) {
      println("downloading ...")
      val reader = new BufferedReader(new InputStreamReader(s3Obj.getObjectContent()))
      val writer = new PrintWriter(f)
      var line: String = reader.readLine()
      var count = 0

      do  {
        writer.println(line)
        count += 1
        if (count % 500 == 0)
          println(s"#$count")
        line = reader.readLine()
      } while (null != line )

      writer.close()
      println(s"写入了 $count 行")
    } else {
      println("already exists !")
    }
  }
}
