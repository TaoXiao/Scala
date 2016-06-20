package cn.gridx.scala.akka.app.crossfilter

import java.util

import akka.actor.{Actor, ActorPath, ActorRef, ActorSystem, Props, RootActorPath}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by tao on 6/19/16.
  */
class Master(dataPath: String, lineCount: Int, workingActors: Int) extends Actor {
  // 存储所有的worker actors
  val workers = ArrayBuffer[ActorRef]()
  var numLoadingFinished = 0  //  多少个worker加载完成了
  var numAnalysisFinished = 0
  var ts_startLoading: Long = _
  var ts_startAnalysis: Long = _


  override def receive = {
    case MSG_REGISTER_WORKER =>
      registerWorker(sender())

    case MSG_WORKER_LOAD_FINISH(elapsed) =>
      onLoadFinished(sender(), elapsed)

    case MSG_WORKER_ANALYSIS_FINISH(actorPath, elapsed, dimDistribution, optDistributions) =>
      onAnalysisFinished(actorPath, elapsed, dimDistribution, optDistributions)

  }


  /**
    * 注册一个worker actor
    * */
  def registerWorker(worker: ActorRef): Unit = {
    println(s"\n收到了消息[MSG_REGISTER_WORKER], sender = ${worker.path}\n")
    if (workers.size < workingActors)
      workers.append(worker)
    else
      println("注册workers的名额已满, 不再接受worker注册")

    if (workers.size == workingActors) {
      println(s"已经注册了${workers.size}个worker actors, 开始让各个worker加载数据")
      loadData()
    }
  }


  /**
    * 指令各个workers去加载自己的数据
    * */
  def loadData(): Unit = {
    ts_startLoading = System.currentTimeMillis()
    val assignments = assignData(dataPath, lineCount, workers.size)
    for (i <- 0 until workers.size) {
      val (start, range) = assignments.get(i).get
      workers(i) ! MSG_MASTER_LOAD(dataPath, start, range)
    }
  }


  /**
    * 当某个worker加载完自己的数据之后
    * */
  def onLoadFinished(actor: ActorRef, elapsed: Long): Unit = {
    numLoadingFinished += 1
    println(s"\nActor [${actor.path}] 完成了数据加载, 耗时 ${elapsed/1000} 秒\n")

    // 当全部的workers记载完毕后, 开始分析数据
    if (numLoadingFinished == workers.size) {
      val totalElapsed = System.currentTimeMillis() - ts_startLoading
      println(s"\n全部workers完成了数据加载, 整体耗时 ${totalElapsed/1000} 秒, 下面开始分析数据\n")

      // 指令各个workers开始分析自己的数据
      ts_startAnalysis = System.currentTimeMillis()
      analyze()
    }
  }


  def onAnalysisFinished(actorPath: ActorPath, elapsed: Long,
                         dimDistribution: util.HashMap[String, util.TreeMap[Int, Int]],
                         optDistributions: util.HashMap[String, util.HashMap[String, Int]]): Unit = {
    println(s"Worker[${RootActorPath(actorPath.address)}] 完成计算, 耗时 ${elapsed/1000} 秒, 计算结果为: \n" +
      s"dimDistribution = $dimDistribution\noptDistributions = $optDistributions\n")

    numAnalysisFinished += 1
    if (numAnalysisFinished == workers.size) {
      val totalElapsed = System.currentTimeMillis() - ts_startAnalysis
      println(s"\n全部的worker分析完成, 总体耗时 ${totalElapsed/1000} 秒\n")
    }

  }



  /**
    * 为每个worker分配任务(要计算的数据范围)
    * */
  def assignData(path: String, lines: Int, workers: Int): mutable.HashMap[Int, (Int, Int)] = {
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
  def analyze(): Unit = {
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
}

object Master extends App {
  val config = ConfigFactory.parseString("akka.cluster.roles=[MASTER]")
    .withFallback(ConfigFactory.load())
  val system = ActorSystem("CrossFilterSystem", config)
  system.actorOf(Props(classOf[Master], args(0), args(1).toInt, args(2).toInt), name = "master")
}


case class MSG_MASTER_LOAD(path: String, start: Int, range: Int)

case class MSG_MASTER_ANALYSIS(optFilter: mutable.HashMap[String, String],
                               dimFilter: mutable.HashMap[String, mutable.HashMap[String, Float]],
                               targetOptions: Array[String],
                               dimIntervals: mutable.HashMap[String, Array[Float]])
