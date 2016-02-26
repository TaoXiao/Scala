package cn.gridx.scala.lang.concurrency.actors


import java.util.Date

import scala.actors._
import scala.actors.Actor._
import scala.util.Random

/**
  * Created by tao on 2/25/16.
  */
object Example {
  def main(args: Array[String]): Unit = {
    println("Starting ...")

    SillyActor.start
    SeriousActor.start

    val myActor = actor {
      for (i <- 0 until 5) {
        println(s"[${new Date()}] myActor $i")
        Thread.sleep(Random.nextInt(5000))
      }
    }

    val echoActor = actor {
      while (true) {
        receive {
          case msg => println(s"消息为 [$msg]")
        }
      }
    }

    // 以上4个Actor是异步运行的,且与main的主线程也是异步运行的

    println("Done ...")

    // 向 echoActor 发送消息
    var j = 0
    while (j < 5) {
      echoActor ! s"你好, Actor. j = $j, time = ${new Date()}"
      j += 1
    }
  }
}


object SillyActor extends Actor {
  override def act(): Unit = {
    for (i <- 0 until 5) {
      println(s"[${new Date()}] Silly Actor $i")
      Thread.sleep(Random.nextInt(5000))
    }
  }
}

object SeriousActor extends Actor {
  override
  def act(): Unit = {
    for (i <- 0 until 5) {
      println(s"[${new Date()}] Serious Actor $i")
      Thread.sleep(Random.nextInt(5000))
    }
  }
}
