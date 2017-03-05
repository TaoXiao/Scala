package cn.gridx.scala.lang.concurrency.future

import java.util.concurrent.TimeoutException
import akka.util.Timeout
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Created by tao on 2/13/17.
  */
object TestTimeout extends App {

  val timeout = Timeout(2 seconds)

  val future: Future[Int] = Future {
    println("Starting ...")
    val x = 0
    Thread.sleep(10*1000)
    println("stop ")
    10001
  }

  try {
    val result: Int = Await.result(future, timeout.duration)
    println(result)
  } catch {
    case ex: TimeoutException =>
      println(s"超时了: $ex")
    case ex =>
      println(s"其他错误: $ex")
  }



  println("结束了")
}
