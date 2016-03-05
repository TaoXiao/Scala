package cn.gridx.scala.akka.tutorial

import java.util.Date

import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import scala.concurrent.duration._
import scala.util.Random


/**
  * Created by tao on 3/4/16.
  */
object WordCount {
  def main(args: Array[String]): Unit = {
    implicit val timeout = Timeout(3 seconds)
    val system: ActorSystem = ActorSystem("WordCount-Akka-System")
    val master: ActorRef = system.actorOf(Props[Master], "Master-Actor")

    println(s"[${new Date}] 开始运行 ...")

    /**
      * 注意 ? method 的使用
      * ? 代表 ask 模式, caller会立即返回,但是它会返回一个`Future`
      * */
    val result: Future[Any] = master ? StartMsg("/Users/tao/IdeaProjects/spray-template/src/test/scala/com/example/MyServiceSpec.scala", " ")

    println(s"[${new Date}] Result虽然已经返回,但是还在等待结果")
    result.map(x => {
      println(s"[${new Date}]  最终结果=${x.asInstanceOf[TotalCountMsg].total}")
      system.shutdown
    })

    println()
  }

  case class StartMsg(path: String, separator: String) { }
  case class CountOneLineMsg(line: String, spliter: String) {}
  case class PartialCountMsg(count: Int) { }
  case class TotalCountMsg(total: Int) { }


  class Master extends Actor {
    var totalCount: Int = 0
    var totalLines: Int = 0
    var processedLines: Int = 0
    var commander: ActorRef = _

    override def receive = {
      // 这是由sysyem actor发来的消息
      case StartMsg(path, separator) =>
        println(s"[${new Date()}] Master - 开始处理文件 $path")
        commander = sender // 保存system actor

        Source.fromFile(path).getLines
          .foreach(line => { // 为每行内容创建一个worker
            context.actorOf(Props[Worker]) ! CountOneLineMsg(line, separator)
            totalLines += 1
          })

      // 这是由worker发来的消息
      case PartialCountMsg(count) =>
        totalCount += count
        processedLines += 1
        if (totalLines == processedLines) {
          println(s"[${new Date}] Master - 所有的workers已经完成计算")
          commander ! TotalCountMsg(totalCount) // 向system actor发送消息
        }

    }
  }

  class Worker extends Actor {
    override def receive = {
      case CountOneLineMsg(line, spliter) =>
        Thread.sleep(Random.nextInt(1000))
        sender ! PartialCountMsg(line.split(spliter).size)
      case _ =>
        println("[Worker] - Invalid Message Received !")
    }
  }



}
