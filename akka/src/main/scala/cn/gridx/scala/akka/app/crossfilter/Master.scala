package cn.gridx.scala.akka.app.crossfilter

import java.io.File

import akka.actor.{Actor, ActorPath, ActorRef, ActorSystem, Props, RootActorPath}
import akka.util.Timeout
import cn.gridx.scala.akka.app.crossfilter.Master.{MSG_MS_TEST, MSG_MW_START_ANALYSIS, MSG_MW_START_LOAD, MasterAnalysisResult, Master_PopHistoResult}
import cn.gridx.scala.akka.app.crossfilter.ServiceActor.MSG_SM_START_ANALYSIS
import cn.gridx.scala.akka.app.crossfilter.SortedPopHist.{MSG_MW_QUERY_POPHIST, MSG_SM_QUERY_POPHIST, PopHistParam}
import cn.gridx.scala.akka.app.crossfilter.Worker._
import com.google.gson.{Gson, GsonBuilder, JsonObject}

import scala.concurrent.duration._
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

import scala.collection.immutable.TreeMap
import scala.collection.{Set, mutable}
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
  * Created by tao on 6/19/16.
  */
class Master(dataPath: String, maxWorkerNum: Int) extends Actor {
  val logger = LoggerFactory.getLogger(this.getClass)

  // 存储所有的worker actors
  val workers = ArrayBuffer[ActorRef]()       //  将所有的worker actors存储在这里
  var numLoadingFinished  : Int     = 0       //  多少个worker加载完成了
  var numAnalysisFinished : Int     = 0       //  多少个worker完成了对自己数据的分析
  var numSampleWorkerFinished: Int  = 0       //  多少个worker完成了对自己数据中的samples的计算
  var loadingStarted      : Boolean = false   //  是否已经开始load数据
  var loadingFinished     : Boolean = false   //  是否所有的worker已经加载完自己的数据了
  var ts_startLoading     : Long    = _       //  对加载数据开始计时
  var ts_startAnalysis    : Long    = _       //  对每次的分析结果开始计时
  var ts_startPopHist     : Long    = _       //  对每次计算sorted population histogram进行计时
  var queryingActor: Option[ActorRef] = None  //  当前是否有某个spray发起了请求, 但是还未被处理完

  // 在一次cross filter查询中, 将每一个worker返回的结果记录在这里
  val analysisResultSet: mutable.ArrayBuffer[WorkerAnalysisResult] = mutable.ArrayBuffer[WorkerAnalysisResult]()

  // 在一次quantiles查询中, 将每一个worker返回的结果记录在这里
  val sampleResultArray: mutable.ArrayBuffer[Double] =  mutable.ArrayBuffer[Double]()

  override def receive = {
    // Spray -> Master : 开始分析数据
    case MSG_SM_START_ANALYSIS(analysisParam) =>
      onMsgSmStartAnalysis(sender(), analysisParam)

    // spray -> master , 要求计算sorted population histogram
    case MSG_SM_QUERY_POPHIST(popHistParam) =>
      onMsgSmStartPopHist(sender(), popHistParam)

    // Worker -> Master : 向master注册自己
    case MSG_WM_REGISTER =>
      onMsgWmRegister(sender())

    // Worker -> Master : 某个worker完成了对自己数据子集的加载
    case MSG_WM_LOAD_FINISHED(elapsed) =>
      onMsgWmLoadFinish(sender(), elapsed)

    // worker ->  worker 计算完了自己的数据
    case MSG_WM_ANALYSIS_FINISHED(actorPath, elapsed, analysisResult) =>
      onMsgWmAnalysisFinished(actorPath, elapsed, analysisResult)

    case MSG_WM_SAMPLES_GENERATED(actorPath, elapsed, samples) =>
      onMsgWmSamplesGenerated(actorPath, elapsed, samples)

    // spray -> master 测试消息
    case MSG_WM_SM_TEST =>
      logger.info("收到了消息 WM_SM_TEST")
      sender() ! MSG_MS_TEST
  }


  /***
    * 当Spray要求master开始分析数据
    *
    * @param analysisParam - spray传来的参数
    */
  def onMsgSmStartAnalysis(sender: ActorRef, analysisParam: AnalysisParam): Unit = {
    ts_startAnalysis = System.currentTimeMillis()
    if (queryingActor.isDefined) {
      val msg = s"当前有请求还未被处理完毕, 请稍后再试!"
      logger.info(msg)
      sender ! MasterAnalysisResult(false, msg, null)
    } else {
      queryingActor = Some(sender)
      val errorMsg = prepareAnalysis(analysisParam)
      if (null != errorMsg) {
        queryingActor.get ! MasterAnalysisResult(false, errorMsg, null)
        queryingActor = None
        logger.info("将把queryingActor重置为None")
      } else {
        logger.info("正在指令各个worker开始分析自己的数据")
        doAnalysis(analysisParam)
      }
    }
  }


  /**
    * 要计算`targetDimName`在数据中(这里的数据可以是全体数据, 也可以是已经被cross filter过滤后的数据)是怎样分布的,
    * 返回结果是目标维度`targetDimName`的n个quantiles
    * */
  private def onMsgSmStartPopHist(sender: ActorRef, param: PopHistParam): Unit = {
    ts_startPopHist = System.currentTimeMillis()
    if (queryingActor.isDefined) {
      val msg = s"当前有请求还未被处理完毕, 请稍后再试!"
      logger.info(msg)
      sender ! SortedPopHist.MasterResult(false, msg, 0, null)
    } else {
      queryingActor = Some(sender)
      doSortedPopHist(param)
    }
  }


  /**
    * 为开始分析进行准备, 主要是检查:
    *   1) 当前的workers数量是否足够
    *   2) 数据加载是否完成
    *
    * 如果检查通过, 则返回null
    * 否则, 返回一条错误消息
    * */
  def prepareAnalysis(analysisParam: AnalysisParam): String  = {
    // 首先检查workers的情况
    if (workers.size != maxWorkerNum) {
      val msg = s"无法开始计算! 目前的worker actor数量为${workers.size}, 还未到达预定的数量${maxWorkerNum}"
      logger.info(msg)
      return msg
    }

    // 再检查数完毕据是否已经加载
    if (!loadingFinished) {
      if (!loadingStarted) {  // 如果还没开始加载数据, 则首先加载数据
        startLoadData()
        loadingStarted = true
      }
      val msg = s"数据加载尚未完成, 请稍后再试 !"
      logger.info(msg)
      return msg
    }

    return null
  }


  /**
    * 某个worker向master发送的消息, 要求向master注册自己
    * */
  private def onMsgWmRegister(worker: ActorRef): Unit = {
    logger.info(s"消息[MSG_WM_REGISTER], sender = ${worker.path}")

    if (workers.size < maxWorkerNum) {
      workers.append(worker)
      logger.info(s"已接受注册该worker, 现在有${workers.size}个worker在册")
    }
    else
      logger.info("注册workers的名额已满, 不再接受worker注册")
  }


  /**
    * 指令各个workers去加载自己的数据
    * 这里假设各个worker已经就绪
    * */
  private def startLoadData(): Unit = {
    logger.info("现在指令各个workers加载自己的数据 ... ")
    ts_startLoading = System.currentTimeMillis()

    val assignments: mutable.HashMap[Int, (Int, Int)] = assignData(dataPath, workers.size)
    for (i <- 0 until workers.size) {
      val (start, range) = assignments.get(i).get
      workers(i) ! MSG_MW_START_LOAD(dataPath, start, range)
    }
  }


  /**
    * 目标数据文件中有多少行
    * */
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
      logger.info(s"Actor($actor)加载完了自己的数据")
  }




  /**
    * 某个worker完成了对自己数据的分析
    * */
  private def onMsgWmAnalysisFinished(actorPath: ActorPath, elapsed: Long, result: WorkerAnalysisResult): Unit = {
    logger.info(s"Worker[${RootActorPath(actorPath.address)}] 完成计算, 耗时 ${elapsed/1000} 秒, 计算结果为: \n" +
      s"dimDistribution = ${result.dimDistributions}\noptDistributions = ${result.optDistributions}\n")

    if (workers.size <= numAnalysisFinished) {
      logger.error(s"异常: 不应该再收到来自worker($actorPath)的计算结果, 因为现在master已经有了来自${numAnalysisFinished}个worker的计算结果")
      return
    }

    // 将这个worker的计算结果存储起来
    analysisResultSet.append(result)
    numAnalysisFinished += 1

    if (numAnalysisFinished == workers.size) {
      val totalElapsed = System.currentTimeMillis() - ts_startAnalysis
      logger.info(s"全部的worker分析完成, 总体耗时 ${totalElapsed/1000} 秒\n")
      forwardAnalysisResult2QueryingActor(totalElapsed) // 把所有的结果融合起来, 然后将结果返回给spray actor
    }
  }


  /**
    * 当某个worker产生了它的local samples, 并将包含结果的消息送到master时, 会触发此方法
    *
    * */
  private def onMsgWmSamplesGenerated(actorPath: ActorPath, elapsed: Long, samples: List[Double]): Unit = {
    sampleResultArray.appendAll(samples)
    numSampleWorkerFinished += 1

    logger.info(s"收到了第${numSampleWorkerFinished}个worker的local sample")

    // 全部的workers都算完了自己的local samples
    if (numSampleWorkerFinished == workers.size) {
      logger.info(s"全部的Local samples已经收齐了, 共有${sampleResultArray}个local samples")
      queryingActor.get ! Master_PopHistoResult("共有${sampleResultArray}个local samples")
    }
  }





  /**
    * 当所有的worker都完成了对自己数据的分析后, 每个worker会算出一个结果子集
    * 需要把所有worker的结果子集merge成最终的结果
    *
    * 例如: M1 = ({E6={-1=0, 0=0, 1=0, 2=0}, E1={-1=0, 0=0, 1=4, 2=5}}, {A={Y=27}, B={N=27}, C={N=27}, D={N=27}, E={Y=2, N=25}} )
    *      M2 = ({E6={-1=0, 0=0, 1=0, 2=0}, E1={-1=0, 0=0, 1=4, 2=5}}, {A={Y=27}, B={N=27}, C={N=27}, D={N=27}, E={Y=2, N=25}} )
    *      M3 = ({E6={-1=0, 0=0, 1=0, 2=0}, E1={-1=0, 0=0, 1=4, 2=5}}, {A={Y=27}, B={N=27}, C={N=27}, D={N=27}, E={Y=2, N=25}} )
    *      M = ArrayBuffer(M1, M2, M3)
    * 则   mergerAnalysisResults(M) =
    *         Map(E1 -> Map(-1 -> 0, 0 -> 0, 1 -> 12, 2 -> 15), TOUA -> Map(-1 -> 30, 0 -> 27, 1 -> 24, 2 -> 21), E6 -> Map(-1 -> 0, 0 -> 0, 1 -> 0, 2 -> 0))
    *Map(D -> Map(N -> 81), A -> Map(Y -> 81), C -> Map(N -> 81), E -> Map(Y -> 6, N -> 75), B -> Map(N -> 81))
    * */
  private def mergeAnalysisResults(analysisResults: mutable.ArrayBuffer[WorkerAnalysisResult]): WorkerAnalysisResult = {
    val finalDimDistri = mutable.HashMap[String, TreeMap[Int, Int]]() // key - dimName,   value: dimDistri
    val finalOptDistri = mutable.HashMap[String, mutable.HashMap[String, Int]]()

    // originDimDistr = Array[Map[dimName, dimDistri]]
    val originDimDistrArr: ArrayBuffer[mutable.HashMap[String, TreeMap[Int, Int]]] = analysisResults.map(_.dimDistributions)
    for (originDimDistr <- originDimDistrArr) {
      for ((name, distri) <- originDimDistr) {
        if (!finalDimDistri.contains(name))
          finalDimDistri.put(name, distri)
        else
          finalDimDistri.put(name, mergeDimDistri(distri, finalDimDistri.get(name).get))
      }
    }

    val originOptDistriArr: ArrayBuffer[mutable.HashMap[String, mutable.HashMap[String, Int]]] = analysisResults.map(_.optDistributions)
    for (originOptDistri <- originOptDistriArr) {
      for ((name, distri) <- originOptDistri) {
        if (!finalOptDistri.contains(name))
          finalOptDistri.put(name, distri)
        else
          finalOptDistri.put(name, mergeOptDistri(distri, finalOptDistri.get(name).get))
      }
    }

    WorkerAnalysisResult(finalDimDistri, finalOptDistri)
  }


  /**
    * 将两个dimension distribution累加起来
    *
    * m1和m2中的key一定是相同的,这与`mergeOptDistri`方法面临的情况不同
    * */
  private def mergeDimDistri(m1: TreeMap[Int, Int], m2: TreeMap[Int, Int]): TreeMap[Int, Int] = {
    var result = TreeMap[Int, Int]()

    for (key <- m1.keys) {
      val sum = m1.get(key).get + m2.get(key).get
      result += (key -> sum)
    }

    result
  }


  /**
    * 将两个option distribution累加起来
    *
    * m1和m2中的key有可能不同, 例如对于option "CARE"
    *   m1 = Map("Y" -> 5), m2 = Map("N" -> 10)
    * 这是因为, m1和m2如果来自于两个actor, 那么可能actor1中的数据的该属性的值全部为"Y", 而actor2中的数据的该属性的值全部为"N"
    * 所以对于这种情况, 应该把它们合并起来, 形成Map("Y" -> 5, "N" -> 10)
    * */
  private def mergeOptDistri(m1: mutable.HashMap[String, Int], m2: mutable.HashMap[String, Int]): mutable.HashMap[String, Int] = {
    val result = mutable.HashMap[String, Int]()
    val totalKeys: Set[String] = m1.keySet union m2.keySet

    for (key <- totalKeys) {
      val sum = m1.getOrElse(key, 0) + m2.getOrElse(key, 0)
      result.put(key, sum)
    }

    result
  }



  /**
    * 汇总所有worker的计算结果, 然后将最终结果发送到spray
    * */
  private def forwardAnalysisResult2QueryingActor(elapsedMillis: Long): Unit = {
    if (queryingActor.isEmpty) {
      logger.error("异常, QueryingActor不应该为None\n")
      return
    }

    // 向spray actor回送经过merge后的结果
    queryingActor.get ! MasterAnalysisResult(true, s"计算成功, 耗时${elapsedMillis/1000.0}秒", mergeAnalysisResults(analysisResultSet))
    queryingActor = None
    analysisResultSet.clear()
    numAnalysisFinished = 0
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
  private def doAnalysis(param: AnalysisParam): Unit = {
    logger.info(s"查询参数为 : \n$param")

    for (worker <- workers)
      worker ! MSG_MW_START_ANALYSIS(param.optFilter, param.dimFilter, param.targetOptions, param.targetDimensions)
  }


  /**
    * 指令各个worker去开始计算本actor上数据的sorted population histogram
    * 即对自己的数据计算出s个sample points
    * */
  private def doSortedPopHist(param: PopHistParam): Unit = {
    logger.info(s"查询参数为 : \n$param")

    for (worker <- workers) {
      worker ! MSG_MW_QUERY_POPHIST(param)
    }
  }
}




object Master  {
  /** 这是启动Master时的命令参数  */
  final case class CmdParam(dataPath: String = null, minWorkerActors: Int = -1)

  val logger = LoggerFactory.getLogger(this.getClass())

  val parser = new scopt.OptionParser[CmdParam]("请输入数据文件的路径, 以及要求的acror数量") {
    head("")

    opt[String]("dataPath") required() valueName("<data file path>") action {
      (x, c) => c.copy(dataPath = x)
    } text("数据文件的路径")

    opt[Int]("minWorkerActors") required() valueName("<minimum worker actor number>") action {
      (x, c) => c.copy(minWorkerActors = x)
    } text("要求的worker actors数量的下限")
  }

  val gson: Gson = new GsonBuilder().serializeNulls().create()
  implicit val timeout: Timeout = Timeout(60 second)
  var config: Config        = _
  var system: ActorSystem   = _
  var master: ActorRef      = _


  def main(args: Array[String]): Unit = {
    logger.info(s"args = ${args.mkString("[", "|", "]")}")

    config = ConfigFactory.parseString(s"akka.cluster.roles=[${Roles.MASTER}]")
      .withFallback(ConfigFactory.load())
    system = ActorSystem("CrossFilterSystem", config)

    parser.parse(args, CmdParam()) match {
      case Some(param) =>
        master = system.actorOf(Props(classOf[Master], param.dataPath, param.minWorkerActors), name = "master")
      case _ =>
        logger.error("错误的输入参数!")
        System.exit(1)
    }
  }



  /**
    * 将各个worker actors计算出的结果汇总后, 整合后装配成一个最终的结果返回给外界的查询者
    * 由于外界往往需要将返回结果组织成json格式,所以这里加了一个`toJson`的方法
    * */
  final case class MasterAnalysisResult(succeed: Boolean, msg: String, analysisResult: WorkerAnalysisResult) {
    def toJson(): String = {
      val gson: Gson = new GsonBuilder().serializeNulls().create()
      val jObj = new JsonObject()
      jObj.addProperty("succeed", succeed)
      jObj.addProperty("message", msg)

      if (succeed) {
        val jDim = new JsonObject()
        for ((k, v) <- analysisResult.dimDistributions) {
          jDim.add(k, Map2Json(v))
        }

        val jOpt = new JsonObject()
        for ((k, v) <- analysisResult.optDistributions) {
          jOpt.add(k, Map2Json(v))
        }

        jObj.add("dimensions", jDim)
        jObj.add("options", jOpt)
      }

      jObj.toString
    }

    private def Map2Json[A, B](map: Map[A, B]): JsonObject = {
      val jMap = new JsonObject()
      for ((k, v) <- map)
        jMap.addProperty(k.toString, v.toString)
      jMap
    }

    private def Map2Json[A, B](map: mutable.Map[A, B]): JsonObject = {
      val jMap = new JsonObject()
      for ((k, v) <- map)
        jMap.addProperty(k.toString, v.toString)
      jMap
    }
  }


  /**
    * master算出的最终的结果
    * */
  final case class Master_PopHistoResult(msg: String) {

  }



  final case class MSG_MW_START_LOAD(path: String, start: Int, range: Int)

  final case class MSG_MW_START_ANALYSIS(optFilter: mutable.HashMap[String, String],
                                         dimFilter: mutable.HashMap[String, mutable.HashMap[String, Double]],
                                         targetOptions: Array[String],
                                         dimIntervals: mutable.HashMap[String, Array[Double]])


  final case class MSG_MS_TEST()

  /**
    * 仅仅作测试用
    * 验证 方法 `mergerAnalysisResults` 的计算正确性
    * */
  def Validate_mergerAnalysisResults(master: Master): Unit = {
    val M1 = WorkerAnalysisResult(
      mutable.HashMap("E6"    -> TreeMap(-1 -> 0, 0 -> 0, 1 -> 0, 2 -> 0),
        "E1"    -> TreeMap(-1 -> 0, 0 -> 0, 1 -> 4, 2 -> 5),
        "TOUA"  -> TreeMap(-1 -> 10, 0 -> 9, 1 -> 8, 2 -> 7)),
      mutable.HashMap("A"   -> mutable.HashMap("Y" -> 27),
        "B"   -> mutable.HashMap("N" -> 27),
        "C"   -> mutable.HashMap("N" -> 27),
        "D"   -> mutable.HashMap("N" -> 27),
        "E"   -> mutable.HashMap("Y" -> 2, "N" -> 25))
    )

    val M2: WorkerAnalysisResult = WorkerAnalysisResult(
      mutable.HashMap("E6" -> TreeMap(-1 -> 0, 0 -> 0, 1 -> 0, 2 -> 0),
        "E1" -> TreeMap(-1 -> 0, 0 -> 0, 1 -> 4, 2 -> 5),
        "TOUA" -> TreeMap(-1 -> 10, 0 -> 9, 1 -> 8, 2 -> 7)),
      mutable.HashMap("A" -> mutable.HashMap("Y" -> 27),
        "B" -> mutable.HashMap("N" -> 27),
        "C" -> mutable.HashMap("N" -> 27),
        "D" -> mutable.HashMap("N" -> 27),
        "E" -> mutable.HashMap("Y" -> 2, "N" -> 25))
    )


    val M3: WorkerAnalysisResult = WorkerAnalysisResult(
      mutable.HashMap("E6" -> TreeMap(-1 -> 0, 0 -> 0, 1 -> 0, 2 -> 0),
        "E1" -> TreeMap(-1 -> 0, 0 -> 0, 1 -> 4, 2 -> 5),
        "TOUA" -> TreeMap(-1 -> 10, 0 -> 9, 1 -> 8, 2 -> 7)),
      mutable.HashMap("A" -> mutable.HashMap("Y" -> 27),
        "B" -> mutable.HashMap("N" -> 27),
        "C" -> mutable.HashMap("N" -> 27),
        "D" -> mutable.HashMap("N" -> 27),
        "E" -> mutable.HashMap("Y" -> 2, "N" -> 25))
    )

    println(master.mergeAnalysisResults(mutable.ArrayBuffer(M1, M2, M3)).dimDistributions)
    println(master.mergeAnalysisResults(mutable.ArrayBuffer(M1, M2, M3)).optDistributions)
  }
}
