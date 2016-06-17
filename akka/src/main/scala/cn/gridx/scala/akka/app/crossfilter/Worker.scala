package cn.gridx.scala.akka.app.crossfilter

import akka.actor.Actor

/**
  * Created by tao on 6/17/16.
  */
class Worker extends Actor {
  val handler = new CrossFilter()

  def receive = {
    case MSG_LOAD(path, workerIdx, start, range) =>
      println(s"Worker #$workerIdx received msg MSG_LOAD")
      load(path, workerIdx, start, range)
  }

  /**
    * 每个worker加载自己的数据
    * */
  def load(path: String, workerIdx: Int, start: Int, range: Int): Unit = {
    val ts = System.currentTimeMillis()

    handler.LoadSourceData(path, start, range)

    sender ! MSG_WORKER_LOAD_FINISH(workerIdx, System.currentTimeMillis() - ts)
  }
}

