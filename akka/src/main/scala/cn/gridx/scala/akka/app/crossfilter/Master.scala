package cn.gridx.scala.akka.app.crossfilter

import akka.actor.{Actor, Props}
import akka.routing.{RoundRobinRouter}

import scala.collection.mutable

/**
  * Created by tao on 6/17/16.
  */
class Master extends Actor {
  var workerNum: Int = _
  val workerLog = mutable.HashSet[Int]()
  var ts_loadStart: Long = _

  def receive = {
    case MSG_START(config) =>
      scheduleWorkers(config.path, assignData(config.path, config.lines, config.workers))
    case MSG_WORKER_LOAD_FINISH(workerIdx, elapsed) =>
      onLoadFinished(workerIdx, elapsed)

  }

  /**
    * 为每个worker分配任务(要计算的数据范围)
    * */
  def assignData(path: String, lines: Int, workers: Int): mutable.HashMap[Int, (Int, Int)] = {
    workerNum = workers
    val x = lines / workers // 每个worker要分到多少行数据
    val y = lines % workers // 最后一个worker可能会多分到几行数据

    // 我们给worker从0开始编号, 第i个worker应该计算的数据范围从第x*i行开始, 包含后面的x行
    // 行数从0开始编号
    val assignment = mutable.HashMap[Int, (Int, Int)]()
    for (i <- 0 until workers - 1)
      assignment.put(i, (x*i, x))
    assignment.put(workers - 1, (x*(workers - 1), x + y))

    println("各个worker数据分配的情况是: ")
    for ((workerIdx, (start, range)) <- assignment)
      println(s"\tworker $workerIdx: ($start, $range) ")

    assignment
  }


  /**
    * 开始调度workers
    *
    * */
  def scheduleWorkers(path: String, assignment: mutable.HashMap[Int, (Int, Int)]): Unit = {
    val numWorkers = assignment.size
    val router = context.actorOf(
      Props[Worker].withRouter(RoundRobinRouter(numWorkers)), "Worker-Router")

    ts_loadStart = System.currentTimeMillis()
    // 让每个worker开始load自己的数据
    for ((workerIdx, (start, range)) <- assignment)
      router ! MSG_LOAD(path, workerIdx, start, range)
  }

  def onLoadFinished(workerIdx: Int, elapsed: Long): Unit = {
    println(s"worker #$workerIdx 加载数据完成, 耗时 ${elapsed/1000}秒")

    workerLog.add(workerIdx)
    if (workerLog.size == workerNum) {
      println(s"全部的worker已经完成加载数据, 总体耗时 ${(System.currentTimeMillis() - ts_loadStart)/1000}秒")
    }
  }

}
