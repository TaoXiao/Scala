package cn.gridx.scala.lang.collections.queue

import scala.collection.mutable.Queue
/**
  * Created by tao on 2/16/17.
  */
object TestQueue extends App {
  val queue = Queue.empty[Job]
  println(queue.size)

  queue.enqueue(Job("1", 100))
  queue.enqueue(Job("2", 200))
  queue.enqueue(Job("3", 300))
  queue.enqueue(Job("4", 400))
  queue.enqueue(Job("5", 500))
  queue.enqueue(Job("6", 600))
  val j = queue.dequeue()
  println(s"j = $j")

  println(s"-----------")
  for (job <- queue)
    println(job)


  println(s"-----------")
  val x = queue.dequeueFirst(_.id == "4")
  println(x)


  println(s"-----------")
  for (job <- queue)
    println(job)

}


case class Job(id: String, num: Int)