package cn.gridx.scala.lang.concurrency.synchronization

import scala.collection.immutable.IndexedSeq
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

/**
  * Created by tao on 12/25/16.
  */
object TestSynchronize extends App {
  val factory = new Factory()

  val f: IndexedSeq[Future[Unit]] =
    for (i <- 0 until 10) yield Future {
      factory.m1(i)
    }

  val sequence: Future[IndexedSeq[Unit]] = Future.sequence(f)
  Await.result(sequence, 30 seconds)
  println("全部结束")
}

/**
  * 要求, 方法Factory.produce不能同时被多个线程访问
  * 换句话说,它必须是thread-safety method
  * */
class Factory {
  def m1(x: Int): Unit = this.synchronized {
      println(s"进入: $x")
      Thread.sleep(Math.abs(Random.nextInt()) % 2 * 1000)
      println(s"离开: $x\n")
  }

  def m2(x: Int): Unit = {
    println(s"进入: $x")
    Thread.sleep(Math.abs(Random.nextInt()) % 2 * 1000)
    println(s"离开: $x\n")
  }
}
