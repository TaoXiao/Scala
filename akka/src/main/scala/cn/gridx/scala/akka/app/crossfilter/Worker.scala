package cn.gridx.scala.akka.app.crossfilter

import scala.concurrent.duration._
import akka.actor.{Actor, ActorPath, ActorRef, ActorSystem, Address, Props, RootActorPath}
import akka.cluster.ClusterEvent.{MemberUp, UnreachableMember}
import akka.cluster.{Cluster, ClusterEvent, Member}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import scopt.OptionParser
import scala.collection.mutable
import scala.concurrent.{Await, Future}


/**
  * Created by tao on 6/19/16.
  */
class Worker extends Actor {
  val logger = LoggerFactory.getLogger(this.getClass)
  val cluster = Cluster(context.system)
  var master: ActorRef = _
  val handler = new CrossFilter()


  /**
    * 订阅系统的消息
    * */
  override def preStart(): Unit = {
    cluster.subscribe(self, ClusterEvent.initialStateAsEvents, classOf[MemberUp], classOf[UnreachableMember])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }


  override def receive: Receive = {
    case MemberUp(member) =>
      onMemberUp(member)

    case MSG_MW_STARTLOAD(path, start, range) =>
      import scala.concurrent.ExecutionContext.Implicits.global
      Future{ loadData(path, start, range) }
      logger.info("数据加载正在进行中 ...")

    case MSG_MW_STARTANALYSIS(optFilter, dimFilter, targetOptions, dimIntervals) =>
      analyze(optFilter, dimFilter, targetOptions, dimIntervals)
  }


  // 寻找master, 并向master注册自己
  private def lookupMaster(address: Address): Unit = {
    // import scala.concurrent.ExecutionContext.Implicits.global
    implicit val timeout = Timeout(10 seconds)

    val f: Future[ActorRef] = context.system.actorSelection(RootActorPath(address)/"user"/"master").resolveOne()
    master = Await.result(f, timeout.duration)
    logger.info("找到了Master: " + master.path.toString)

    registerSelf()
  }


  /**
    * 收到master启动的消息后, 记录下master, 并向它汇报自己
    * */
  private def registerSelf(): Unit = {
      logger.info(s"worker将向master(${master.toString()})注册自己\n")
      master ! MSG_WM_REGISTER
  }


  private def onMemberUp(member: Member): Unit = {
    logger.info(s"消息[MemberUp], member = ${RootActorPath(member.address)}")

    // 如果是maste, 则要找到并记住这个master
    if (member.hasRole("MASTER"))
      lookupMaster(member.address)
  }

  /**
    * 每个worker加载自己的数据
    * */
  private def loadData(path: String, start: Int, range: Int): Unit = {
    logger.info(s"收到了加载数据的指令")
    val ts = System.currentTimeMillis()
    handler.LoadSourceData(path, start, range)
    val elapsed = System.currentTimeMillis() - ts
    logger.info(s"数据加载完成 - 耗时 ${elapsed/1000}秒")
    master ! MSG_WM_LOAD_FINISHED(elapsed)
  }



  /**
    * 每个worker开始分析自己持有的数据
    * */
  private def analyze(optFilter: scala.collection.mutable.HashMap[String, String],
              dimFilter: mutable.HashMap[String, mutable.HashMap[String, Double]],
              targetOptions: Array[String],
              dimIntervals: mutable.HashMap[String, Array[Double]]): Unit = {
    logger.info(s"${self.path} 开始分析数据")
    val ts = System.currentTimeMillis()
    val (dimDistribution, optDistributions) = handler.doFiltering(optFilter, dimFilter, targetOptions, dimIntervals)

    logger.info(s"${self.path} 数据分析完成, 结果为: \ndimDistribution = $dimDistribution\noptDistributions = $optDistributions\n")
    val elapsed = System.currentTimeMillis() - ts

    master ! MSG_WM_ANALYSIS_FINISHED(self.path, elapsed, AnalysisResult(dimDistribution, optDistributions))
  }
}



object Worker {
  val parser: OptionParser[CmdParams] =
    new scopt.OptionParser[CmdParams]("输入节点类型, 以及节点数量 ") {
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
    parser.parse(args, CmdParams()) match {
      case Some(params) =>
        if (params.actorType.equals("seed"))
          startSeedActors()
        else if (params.actorType.equals("nonSeed")) {
          for (i <- 0 until params.actorNumber)
            createWorkerActor(port = 0, actorName = s"Worker_$i")
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
    val seedPorts = List[Int](2551)
    for (port <- seedPorts)
      createWorkerActor(port = port, actorName = s"Worker_$port")
  }



  /**
    * 创建普通的worker actor
    * */
  def createWorkerActor(port: Int = 0, actorName: String): Unit = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString(s"akk.cluster.roles=[${Roles.MASTER}]"))
      .withFallback(ConfigFactory.load())
    val system = ActorSystem("CrossFilterSystem", config)
    system.actorOf(Props[Worker], name = actorName)
  }
}