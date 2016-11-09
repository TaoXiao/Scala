package cn.gridx.scala.akka.app.crossfilter

import scala.concurrent.duration._
import akka.actor.{Actor, ActorPath, ActorRef, ActorSystem, Address, Props, RootActorPath}
import akka.cluster.ClusterEvent.{MemberUp, UnreachableMember}
import akka.cluster.{Cluster, ClusterEvent, Member}
import akka.util.Timeout
import cn.gridx.scala.akka.app.crossfilter.Master.{MSG_MW_START_ANALYSIS, MSG_MW_START_LOAD}
import cn.gridx.scala.akka.app.crossfilter.SortedPopHist.{MSG_MW_QUERY_POPHIST, PopHistDataSource}
import cn.gridx.scala.akka.app.crossfilter.Worker.{MSG_WM_ANALYSIS_FINISHED, MSG_WM_LOAD_FINISHED, MSG_WM_REGISTER, MSG_WM_SAMPLES_GENERATED}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import scopt.OptionParser

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Await, Future}


/**
  * Created by tao on 6/19/16.
  */
class Worker extends Actor {
  val logger = LoggerFactory.getLogger(this.getClass)
  val cluster = Cluster(context.system)
  var master: ActorRef = _
  val handle = new CrossFilter()
  var filteredDimensionData: Option[mutable.HashMap[String, ArrayBuffer[Double]]] = None


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

    case MSG_MW_START_LOAD(path, start, range) =>
      import scala.concurrent.ExecutionContext.Implicits.global
      Future{ loadData(path, start, range) }
      logger.info("数据加载正在进行中 ...")

    case MSG_MW_START_ANALYSIS(optFilter, dimFilter, targetOptions, dimIntervals) =>
      onMsgMwStartAnalysis(optFilter, dimFilter, targetOptions, dimIntervals)

    case MSG_MW_QUERY_POPHIST(param) =>
      onMsgMwQueryPopHist(param)
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
    handle.LoadSourceData(path, start, range)
    val elapsed = System.currentTimeMillis() - ts
    logger.info(s"数据加载完成 - 耗时 ${elapsed/1000}秒")
    master ! MSG_WM_LOAD_FINISHED(elapsed)
  }



  /**
    * 每个worker开始分析自己持有的数据
    * */
  private def onMsgMwStartAnalysis(optFilter: scala.collection.mutable.HashMap[String, String],
              dimFilter: mutable.HashMap[String, mutable.HashMap[String, Double]],
              targetOptions: Array[String],
              dimIntervals: mutable.HashMap[String, Array[Double]]): Unit = {
    logger.info(s"${self.path} 开始分析数据")
    val ts = System.currentTimeMillis()
    val (dimDistribution, optDistributions, filteredDimensionData) = handle.doFiltering(optFilter, dimFilter, targetOptions, dimIntervals)

    this.filteredDimensionData = Some(filteredDimensionData)

    logger.info(s"${self.path} 数据分析完成, 结果为: \ndimDistribution = $dimDistribution\noptDistributions = $optDistributions\n")
    val elapsed = System.currentTimeMillis() - ts

    master ! MSG_WM_ANALYSIS_FINISHED(self.path, elapsed, WorkerAnalysisResult(dimDistribution, optDistributions))
  }



  /**
    * 当master要求本worker参与计算sorted population histogram时, 会触发本方法
    *
    * 本方法可以用两种方式来计算:
    * 1) 将本worker中的数据按照预定条件过滤后,将结果中的目标维度排序后直接返回给master,这是最精确的方式,但是费时
    * 2) 利用OPAQ算法为本worker中的数据按照预定条件过滤后,为结果中的目标维度算出local samples, 然后再返回给master,这是近似算法, 但是快
    *
    * 本方法目前只用第一种方法, 因为:
    *    首先, 数据量目前还不是太大,最多四百万, 排序的话还是挺快的;
    *    其次, OPAQ要求每个run中的数据数量相同, 这里经过过滤后的结果不一定满足这个前提条件
    *
    * */
  private def onMsgMwQueryPopHist(param: SortedPopHist.PopHistParam) = {
    var localSource: Array[Double] = null
    val ts = System.currentTimeMillis()

    // 从本worker的全部原始数据中获取samples
    param.dataSource match {
      // 使用全部的原始数据
      case PopHistDataSource.AllData =>
        localSource = handle.GetSourceData().map(_.dims.get(param.targetDimName).get)

      // 使用已有的cross filter结果
      case PopHistDataSource.UseExistingFilter =>
        // 如果cross filter结果已经存在了, 就直接使用
        if (filteredDimensionData.isDefined && filteredDimensionData.get.contains(param.targetDimName))
          localSource = filteredDimensionData.get.get(param.targetDimName).get.toArray
        else // 否则, 返回错误信息, 实际上这个判断应该放在master中进行
          throw new RuntimeException("当前还未进行过cross filter! (请将对本error的拦截放在master端实现 、)")

      // 使用提供的过滤条件
      case PopHistDataSource.ProvidedFilter =>
        localSource = queryDimension(param.targetDimName, param.optFilter, param.dimFilter)

      // 不该出现的情况
      case _ =>
        throw new RuntimeException("非法的data source")
    }

    val localSamples: List[Double] = calcLocalSamples(localSource)
    logger.info(s"${self.path} local samples 计算完成 !")
    val elapsed = System.currentTimeMillis() - ts

    master ! MSG_WM_SAMPLES_GENERATED(self.path, elapsed, localSamples)
  }


  /**
    * 从原始的数据集中根据给定的过滤条件计算出目标dimension的数据点(抽样数据或者是全部数据)
    *  */
  private def queryDimension(targetDimName: String,
                             optFilter: mutable.HashMap[String, String],
                             dimFilter: mutable.HashMap[String, mutable.HashMap[String, Double]]): Array[Double] = {
    // todo: 待实现
    null
  }


  /**
    * 从localSource中计算出使用哪些样本
    * 可以使用 OPAQ进行部分采样, 也可以直接返回全部的样本
    *
    * */
  private def calcLocalSamples(localSource: Array[Double]): List[Double] = {
    localSource.toList
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



  case class MSG_WM_REGISTER()

  case class MSG_WM_LOAD_FINISHED(elapsed: Long)

  case class MSG_WM_ANALYSIS_FINISHED(actorPath: ActorPath, elapsed: Long, analysisResult: WorkerAnalysisResult)

  case class MSG_WM_SAMPLES_GENERATED(actorPath: ActorPath, elapsed: Long, samples: List[Double])

  // 当每个worker计算完了自己的数据的samples, 就将其发送给master
  case class MSG_WM_LOCAL_SAMPLES(samples: WorkerSampleList)

  case class MSG_WM_SM_TEST()

  case class CmdParams(actorType: String = "unknown", actorNumber: Int = -1)

}