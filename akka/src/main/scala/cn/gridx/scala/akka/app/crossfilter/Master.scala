package cn.gridx.scala.akka.app.crossfilter

import java.io.File
import java.util

import akka.pattern.ask
import akka.actor.{Actor, ActorPath, ActorRef, ActorSystem, Props, RootActorPath}
import akka.util.Timeout
import com.google.gson.{Gson, GsonBuilder}

import scala.concurrent.duration._
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Await, Future}
import scala.io.Source

/**
  * Created by tao on 6/19/16.
  */
class Master(dataPath: String, maxWorkerNum: Int) extends Actor {
  val logger = LoggerFactory.getLogger(this.getClass)
  logger.info("正在启动master\n")

  // 存储所有的worker actors
  val workers = ArrayBuffer[ActorRef]()
  var numLoadingFinished      = 0       //  多少个worker加载完成了
  var numAnalysisFinished     = 0       //  多少个worker完成了对自己数据的分析
  var loadingStarted          = false   //  是否已经开始load数据
  var loadingFinished         = false   //  是否所有的worker已经加载完自己的数据了
  var ts_startLoading : Long  = _
  var ts_startAnalysis: Long  = _
  var queryingActor: Option[ActorRef] = None

  // 在一次查询中, 将每一个worker返回的结果记录在这里
  val analysisResultSet: mutable.HashMap[ActorRef, AnalysisResult] = mutable.HashMap[ActorRef, AnalysisResult]()

  logger.info(s"master已启动, path = ${self.path}\n")

  override def receive = {
    case MSG_START_CALCULATION =>
      if (queryingActor.isDefined) {
        val msg = s"当前有请求还未被处理, 请稍后再试!"
        logger.info(msg)
        sender ! CalcResult(false, msg, null)
      } else {
        queryingActor = Some(sender())
        val resp = onMsgStartCalculation()
        // 如果当前无法开始分析, 则onMsgStartCalculation会立即返回一个包含错误信息的结果
        // 我们需要直接将该结果返回给sender, 并将queryingActor重置为None
        if (null != resp) {
          queryingActor.get ! resp
          queryingActor = None
          logger.info("将把queryingActor重置为None")
        }
      }

    // worker要求向master注册了自己
    case MSG_WM_REGISTER =>
      onMsgWmRegister(sender())

    // 某个worker加载完了自己的数据
    case MSG_WM_LOADFINISH(elapsed) =>
      onMsgWmLoadFinish(sender(), elapsed)

    // worker计算完了自己的数据
    case MSG_WM_ANALYSISFINISH(actorPath, elapsed, dimDistribution, optDistributions) =>
      onMsgWmAnalysisFinish(actorPath, elapsed, dimDistribution, optDistributions)

  }


  def onMsgStartCalculation(): CalcResult = {
    logger.info("收到消息: MSG_START_CALCULATION\n")

    // 首先检查workers的情况
    if (workers.size != maxWorkerNum) {
      val msg = s"无法开始计算! 目前的worker actor数量为${workers.size}, 还未到达预定的数量${maxWorkerNum}"
      logger.info(msg)
      return CalcResult(false, msg, null)
    }

    // 再检查数完毕据是否已经加载
    if (!loadingFinished) {
      if (!loadingStarted) {  // 如果还没开始加载数据, 则首先加载数据
        startLoadData()
        loadingStarted = true
      }
      val msg = s"数据加载尚未完成, 请稍后再试 !"
      logger.info("msg")
      return CalcResult(false, msg, null)
    }

    // 如果各个workers已经准备就绪, 且它们都已经加载完数据, 则可以开始计算
    logger.info("正在指令各个worker开始分析自己的数据")
    startAnalyze(constrcuctQuery())

    return null
  }


  /**
    * 某个worker向master发送的消息, 要求向master注册自己
    * */
  private def onMsgWmRegister(worker: ActorRef): Unit = {
    logger.info(s"收到了消息[MSG_WM_REGISTER], sender = ${worker.path}\n")

    if (workers.size < maxWorkerNum)
      workers.append(worker)
    else
      logger.info("注册workers的名额已满, 不再接受worker注册\n")
  }


  /**
    * 指令各个workers去加载自己的数据
    * 这里假设各个worker已经就绪
    * */
  private def startLoadData(): Unit = {
    logger.info("现在指令各个workers加载自己的数据 ... ")
    ts_startLoading = System.currentTimeMillis()

    val assignments = assignData(dataPath, workers.size)
    for (i <- 0 until workers.size) {
      val (start, range) = assignments.get(i).get
      workers(i) ! MSG_MASTER_LOAD(dataPath, start, range)
    }
  }


  private def countLines(path: String): Int = {
    val lines = Source.fromFile(new File(path)).getLines()
    lines.size
  }


  /**
    * 当某个worker加载完自己的数据之后
    * */
  private def onMsgWmLoadFinish(actor: ActorRef, elapsed: Long): Unit = {
    numLoadingFinished += 1
    logger.info(s"Actor [${actor.path}] 完成了数据加载, 耗时 ${elapsed/1000} 秒\n")

    // 全部的workers都加载完毕自己的数据了
    if (numLoadingFinished == workers.size) {
      loadingFinished = true
      val totalElapsed = System.currentTimeMillis() - ts_startLoading
      logger.info(s"全部workers完成了数据加载, 整体耗时 ${totalElapsed/1000} 秒")
    } else
      logger.error(s"有异常: numLoadingFinished = $numLoadingFinished, workers.size = ${workers.size}")
  }


  /**
    * 某个worker完成了对自己数据的分析
    * */
  private def onMsgWmAnalysisFinish(actorPath: ActorPath, elapsed: Long,
                            dimDistribution: util.HashMap[String, util.TreeMap[Int, Int]],
                            optDistributions: util.HashMap[String, util.HashMap[String, Int]]): Unit = {
    logger.info(s"Worker[${RootActorPath(actorPath.address)}] 完成计算, 耗时 ${elapsed/1000} 秒, 计算结果为: \n" +
      s"dimDistribution = $dimDistribution\noptDistributions = $optDistributions\n")

    numAnalysisFinished += 1
    if (numAnalysisFinished == workers.size) {
      val totalElapsed = System.currentTimeMillis() - ts_startAnalysis
      logger.info(s"全部的worker分析完成, 总体耗时 ${totalElapsed/1000} 秒\n")
      forwardAnalysisResultsQueryingActor()
    }
  }


  /**
    * 当所有的worker都完成了对自己数据的分析后, 每个worker会算出一个结果子集
    * 需要把所有worker的结果子集merge成最终的结果
    * */
  private def mergerAnalysisResults(): CalcResult = {
    val msg = s"结果子集融合完毕"
    logger.info(msg)
    return CalcResult(true, msg, null)
  }


  private def forwardAnalysisResultsQueryingActor(): Unit = {
    if (queryingActor.isEmpty) {
      logger.error("异常, queryingActor不应该为None\n")
      return
    }

    queryingActor.get ! mergerAnalysisResults()
    queryingActor = None
  }




  /**
    * 为每个worker分配任务(要计算的数据范围)
    * */
  private def assignData(path: String,  workers: Int): mutable.HashMap[Int, (Int, Int)] = {
    val lines = countLines(path)
    val x = lines / workers // 每个worker要分到多少行数据
    val y = lines % workers // 最后一个worker可能会多分到几行数据

    // 我们给worker从0开始编号, 第i个worker应该计算的数据范围从第x*i行开始, 包含后面的x行
    // 行数从0开始编号
    val assignment = mutable.HashMap[Int, (Int, Int)]()
    for (i <- 0 until workers - 1)
      assignment.put(i, (x*i, x))
    assignment.put(workers - 1, (x*(workers - 1), x + y))

    println("\n各个worker数据分配的情况是: ")
    for ((workerIdx, (start, range)) <- assignment)
      println(s"\tworker $workerIdx: ($start, $range) ")

    assignment
  }


  /**
    * 指令各个workers分析自己的数据
    * */
  private def startAnalyze(param: AnalysisParam): Unit = {
    // options的限制条件
    val optFilter =  mutable.HashMap[String, String]()
    optFilter.put("care", "Y")
    // optFilter.put("fera", "Y")

    // dimension的限制条件
    val dimFilter = mutable.HashMap[String, mutable.HashMap[String, Float]]()
    dimFilter.put("E1", mutable.HashMap[String, Float]("min" -> 100f, "max" -> 1400f))

    // 目标dimension的阈值区间
    val dimIntervals = mutable.HashMap[String, Array[Float]]()
    val interval_E1 = new Array[Float](10)
    for (i <- 0 until 10)
      interval_E1(i) = 150 * i
    dimIntervals.put("E1", interval_E1)

    val interval_E6 = new Array[Float](10)
    for (i <- 0 until 10)
      interval_E6(i) = 150 * i
    dimIntervals.put("E6", interval_E6)

    // 目标options
    val targetOptions = Array[String]("care", "mb", "da", "cca", "tbs")
    for (worker <- workers)
      worker ! MSG_MASTER_ANALYSIS(optFilter, dimFilter, targetOptions, dimIntervals)
  }


  // 测试, 构造一个查询参数
  private def constrcuctQuery(): AnalysisParam = {
    // options的限制条件
    val optFilter =  mutable.HashMap[String, String]()
    optFilter.put("care", "Y")
    // optFilter.put("fera", "Y")

    // dimension的限制条件
    val dimFilter = mutable.HashMap[String, mutable.HashMap[String, Float]]()
    dimFilter.put("E1", mutable.HashMap[String, Float]("min" -> 100f, "max" -> 1400f))

    // 目标dimension的阈值区间
    val dimIntervals = mutable.HashMap[String, Array[Float]]()
    val interval_E1 = new Array[Float](10)
    for (i <- 0 until 10)
      interval_E1(i) = 150 * i
    dimIntervals.put("E1", interval_E1)

    val interval_E6 = new Array[Float](10)
    for (i <- 0 until 10)
      interval_E6(i) = 150 * i
    dimIntervals.put("E6", interval_E6)

    // 目标options
    val targetOptions = Array[String]("care", "mb", "da", "cca", "tbs")

    AnalysisParam(optFilter, dimFilter, targetOptions, dimIntervals)
  }
}


object Master  {
  val gson: Gson = new GsonBuilder().serializeNulls().create()
  implicit val timeout: Timeout = Timeout(30 seconds)
  var config: Config        = _
  var system: ActorSystem   = _
  var master: ActorRef      = _

  def StartMaster(dataPath: String, maxWorkerNum: Int): Unit = {
    config = ConfigFactory.parseString("akka.cluster.roles=[MASTER]")
      .withFallback(ConfigFactory.load())
    system = ActorSystem("CrossFilterSystem", config)
    master = system.actorOf(Props(classOf[Master], dataPath, 5), name = "master")
  }


  /**
    * 当收到来自Spray的指令时, 本方法将被调用
    * 本方法将
    * */
  def StartCalculation(): String = {
    val future: Future[Any] = master ? MSG_START_CALCULATION
    val resp: CalcResult = Await.result(future, timeout.duration).asInstanceOf[CalcResult]
    gson.toJson(resp)
  }

}

case class MSG_START_CALCULATION()

case class MSG_MASTER_LOAD(path: String, start: Int, range: Int)

case class MSG_MASTER_ANALYSIS(optFilter: mutable.HashMap[String, String],
                               dimFilter: mutable.HashMap[String, mutable.HashMap[String, Float]],
                               targetOptions: Array[String],
                               dimIntervals: mutable.HashMap[String, Array[Float]])

// 每一个worker对于自己数据的计算结果
case class AnalysisResult(dimDistributions: util.HashMap[String, util.TreeMap[Int, Int]],
                          optDistributions: util.HashMap[String, util.HashMap[String, Int]])


// 要求worker计算时的查询参数
case class AnalysisParam(optFilter: mutable.HashMap[String, String],
                      dimFilter: mutable.HashMap[String, mutable.HashMap[String, Float]],
                      targetOptions: Array[String],
                      targetDimIntervals: mutable.HashMap[String, Array[Float]])


case class CalcResult(succeed: Boolean, msg: String, analysisResult: AnalysisResult)
