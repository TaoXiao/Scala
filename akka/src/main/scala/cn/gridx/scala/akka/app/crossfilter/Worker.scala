package cn.gridx.scala.akka.app.crossfilter

import java.util


import scala.concurrent.duration._
import akka.actor.{Actor, ActorPath, ActorRef, ActorSelection, ActorSystem, Props, RootActorPath}
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.{Cluster, Member}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import scopt.OptionParser

import scala.collection.mutable


/**
  * Created by tao on 6/19/16.
  */
class Worker extends Actor {
  val logger = LoggerFactory.getLogger(this.getClass)
  val cluster = Cluster(context.system)
  var master: ActorSelection = _
  var masterRef: ActorRef = _
  val handler = new CrossFilter()

  logger.info("正在启动worker ...\n")

  lookupMaster()

  logger.info("worker已启动\n")

  /**
    * 订阅系统的[MemberUp]消息
    * */
  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberUp])
  }

  override def receive = {
    /*case MemberUp(member) =>
      logger.info(s"收到了消息[MemberUp], member = ${RootActorPath(member.address)}\n")
      registerSelf(member)*/

    case MSG_MASTER_LOAD(path, start, range) =>
      loadData(path, start, range)

    case MSG_MASTER_ANALYSIS(optFilter, dimFilter, targetOptions, dimIntervals) =>
      analyze(optFilter, dimFilter, targetOptions, dimIntervals)
  }


  private def lookupMaster(): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val timeout = Timeout(10 seconds)

    context.actorSelection("akka://CrossFilterSystem/user/master")
      .resolveOne().onComplete {
      case scala.util.Success(actorRef) =>
        masterRef = actorRef.asInstanceOf[ActorRef]
        logger.info(s"找到了master: ${masterRef.path}")
      case scala.util.Failure(ex) =>
        logger.info(s"未能找到master, 异常为 ${ex.toString}")
    }
  }


  /**
    * 收到MASTER启动的消息后, 记录下MASTER, 并向它汇报自己
    * */
  def registerSelf(member: Member): Unit = {
    if (member.hasRole("MASTER")) {
      logger.info(s"将向master注册自己, member = $member")
      master = context.actorSelection(RootActorPath(member.address)/"user"/"master")
      master ! MSG_WM_REGISTER
    }
  }


  /**
    * 每个worker加载自己的数据
    * */
  def loadData(path: String, start: Int, range: Int): Unit = {
    val ts = System.currentTimeMillis()
    handler.LoadSourceData(path, start, range)
    sender ! MSG_WM_LOADFINISH(System.currentTimeMillis() - ts)
  }



  /**
    * 每个worker开始分析自己持有的数据
    * */
  def analyze(optFilter: scala.collection.mutable.HashMap[String, String],
              dimFilter: mutable.HashMap[String, mutable.HashMap[String, Float]],
              targetOptions: Array[String],
              dimIntervals: mutable.HashMap[String, Array[Float]]): Unit = {
    println(s"${self.path} 开始分析自己的数据了")
    val ts = System.currentTimeMillis()
    val (dimDistribution, optDistributions) = handler.doFiltering(optFilter, dimFilter, targetOptions, dimIntervals)
    println(s"${self.path} 分析数据完成")
    val elapsed = System.currentTimeMillis() - ts

    master ! MSG_WM_ANALYSISFINISH(self.path, elapsed, dimDistribution, optDistributions)
  }
}

case class Params(actorType: String = "unknown", actorNumber: Int = -1)


object Worker {
  val parser: OptionParser[Params] =
    new scopt.OptionParser[Params]("输入节点类型, 以及节点数量 ") {
      head("")

      opt[String]("actorType").required().valueName("<actor type>").action{
        (x, c) => c.copy(actorType = x)
      } text("actor类型: seed 或者 nonSeed")

      opt[Int]("actorNumber").action {
        (x, c) => c.copy(actorNumber = x)
      } text("actor类型: 只适用于非seed节点")
    }


  /**
    * seed本身也是worker,参与计算工作
    * 与普通的worker不同之处在于: seed的端口是2551
    * */
  def main(args: Array[String]): Unit = {
    parser.parse(args, Params()) match {
      case Some(params) =>
        if (params.actorType.equals("seed"))
          startSeedActors()
        else if (params.actorType.equals("nonSeed")) {
          for (i <- 0 until params.actorNumber)
            createWorkerActor(port = "0", actorName = s"Worker_$i")
        } else
          throw new RuntimeException("非法的`actorType`参数")
      case _ =>
        println("参数错误, 请重新输入")
        parser.showUsage
    }

  }


  /***
    * 创建seed actor, 在一台机器上只创建一个seed actor
    * 实际上这里的创建的seed actor自身也是一个worker actor
    */
  def startSeedActors(): Unit = {
    val seedPorts = List[String]("2551")
    for (port <- seedPorts)
      createWorkerActor(port = port, actorName = s"Worker_$port")
  }



  /**
    * 创建普通的worker actor
    * */
  def createWorkerActor(port: String, actorName: String): Unit = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akk.cluster.roles=[WORKER]"))
      .withFallback(ConfigFactory.load())
    val system = ActorSystem("CrossFilterSystem", config)
    val worker: ActorRef = system.actorOf(Props[Worker], name = actorName)
  }
}


case class MSG_WM_REGISTER()

case class MSG_WM_LOADFINISH(elapsed: Long)

case class MSG_WM_ANALYSISFINISH(actorPath: ActorPath, elapsed: Long,
                                 dimDistribution: util.HashMap[String, util.TreeMap[Int, Int]],
                                 optDistributions: util.HashMap[String, util.HashMap[String, Int]])


