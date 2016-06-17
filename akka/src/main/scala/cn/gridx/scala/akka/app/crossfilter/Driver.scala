package cn.gridx.scala.akka.app.crossfilter

import akka.actor.{ActorSystem, Props}

/**
  * Created by tao on 6/17/16.
  */

case class DriverConfig(path: String = "unknown",  // 本地的资源文件路径
                        lines: Int = -1,  // 本地数据文件的总行数
                        workers: Int = -1)  // 要启动的worker总数

object Driver {

  val parser = new scopt.OptionParser[DriverConfig]("请输入参数") {
    head("Driver的参数解析器", "版本 0.1")

    opt[String]("path") required() valueName("<源数据文件的本地路径>") action {
      (x, c) => c.copy(path = x)
    } text ("源数据文件的本地路径")

    opt[Int]("lines") required() valueName("<源数据文件的总行数>") action {
      (x, c) => c.copy(lines = x)
    } text ("源数据文件的总行数")

    opt[Int]("workers") required() valueName("<启动的worker actors数量>") action {
      (x, c) => c.copy(workers = x)
    } text ("启动的worker actors数量")
  }



  def main(args: Array[String]): Unit = {
    run(DriverConfig("/Users/tao/IdeaProjects/Scala/lang/data_1.txt", 3651707, 3))
    /*
    parser.parse(args, DriverConfig()) match {
      case Some(config) =>
        run(config)
      case _ =>
        println("错误的输入参数")
    }
    */

  }


  def run(config: DriverConfig): Unit = {
    val system = ActorSystem("CrossFilterSystem")
    val master = system.actorOf(Props(classOf[Master]), "master")
    println("启动master")
    master ! MSG_START(config)
  }
}

sealed trait Msg
case class MSG_START(config: DriverConfig) extends Msg
case class MSG_LOAD(path: String, workerIdx: Int, start: Int, range: Int)
case class MSG_WORKER_LOAD_FINISH(workerIdx: Int, elapsed: Long) extends Msg
