package cn.gridx.scala.akka.tutorial

import java.util.Date

import akka.actor.{ActorSystem, ActorRef, Props, Actor}
import akka.routing.RoundRobinRouter

import scala.collection.mutable
import scala.util.Random

/**
  * Created by tao on 2/26/16.
  *
  * 系统中有1个Master和1个Listener。
  * 用户向Master发送开始的指令，Master收到后启动3个Workers并向它们每人发送一条消息。
  * 每个Worker收到消息后，向Master回应一条新的消息。
  * Master集齐了5个Worker的消息后，就这些消息集中起来并发送给Listener。
  * Listener收到消息后将它打印出来。
  */
object SimpleSystem {

  def main(args: Array[String]) {
    println("[main] 创建简单的Akka系统")

    val system = ActorSystem("Simple-Akka-System")
    val listener = system.actorOf(Props[Listener], "listener")
    val master = system.actorOf(Props(new Master(listener)), "master")

    println("[main] 开始启动Master")
    master ! Msg_Start(3)
  }

  sealed trait Msg
  case class Msg_Start(num: Int) extends Msg
  case class Msg_Finished(result: mutable.HashMap[Int, String]) extends Msg
  case class Msg_Req(index: Int) extends Msg
  case class Msg_Resp(index: Int, resp: String) extends Msg

  class Master(listener: ActorRef) extends Actor {
    val result = new mutable.HashMap[Int, String]()
    var numWorkers = 0

    def receive = {
      /** 收到listener的消息, 开始启动`num`个workers */
      case Msg_Start(num) =>
        println(s"[master] 收到`Msg_Start`消息,将创建 $num 个workers")
        numWorkers = num

        /** we create a round-robin router to make it easier to spread out the work evenly between the workers  */
        val workerRouter = context.actorOf(
          Props[Worker].withRouter(RoundRobinRouter(num)), "Worker-Router")

        for (i <- 0 until num) {
          println(s"[master] 向worker发送消息`Msg_Req($i)`")
          workerRouter ! Msg_Req(i)
        }

      /** 收到 worker 的响应消息 */
      case Msg_Resp(index, resp) =>
        println(s"[master] 收到`Msg_Resp`消息, index = $index, resp = $resp")
        result.put(index, resp)

        // 如果收到了全部worker的响应消息,则把最终结果发送给listener
        if (result.size == numWorkers) {
          println(s"[master] 来自 $numWorkers 个workers的消息接收完毕, 将最终结果发送给listener")
          listener ! Msg_Finished(result)

          /** stop itself and all its supervised actors (e.g., workers)  */
          println("[master] 即将关闭master自身,以及master管理的所有workers")
          context.stop(self)
        }
    }
  }


  class Worker extends Actor {
    def receive = {
      // 收到Master发来的消息, 处理消息, 然后向Master回应一条消息
      case Msg_Req(index) =>
        println(s"[worker $index]: 收到来自master的消息")
        Thread.sleep(Random.nextInt(10000))
        sender ! Msg_Resp(index, s"我是Worker[$index], 现在时间是 ${new Date()}")
    }
  }


  class Listener extends Actor {
    def receive = {
      case Msg_Finished(result) =>
        println("[listener] 收到来自Master的消息")
        println(s"[listener] 结果为\n\t" +
          s"${result.mkString(", \n\t")}")

        println("[listener] 即将关闭Akka System")
        context.system.shutdown
    }
  }

}

