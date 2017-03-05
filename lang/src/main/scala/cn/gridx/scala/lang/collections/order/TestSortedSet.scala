package cn.gridx.scala.lang.collections.order

import java.util.Comparator

import scala.collection.JavaConversions._

/**
  * Created by tao on 2/6/17.
  */
object TestSortedSet extends App {
  val set = new java.util.TreeSet[Job]()
  set.add(Job("B", true ,  5,  0))
  set.add(Job("C", false , 10, 20))
  set.add(Job("A", false , 15, 10))
  set.add(Job("D", false , 20, 10))

  remove(set, "A")
  remove(set, "B")

  for (j <- set)
    println(j)



  def remove(set: java.util.TreeSet[Job], id: String): Unit = {
    val it = set.iterator()
    while (it.hasNext) {
      if (it.next().jobID == id) {
        it.remove()
        return
      }
    }
  }

}


case class Job(jobID: String, realtime: Boolean, prio: Int, createDate: Int) extends Comparable[Job] {
  override def compareTo(o2: Job): Int = {
    if (realtime && !o2.realtime)
      return -1
    else if (!realtime && o2.realtime)
      return 1

    if (prio < o2.prio)
      return -1
    else if (prio > o2.prio)
      return 1

    if (createDate < o2.createDate)
      return -1
    else if (createDate > o2.createDate)
      return 1
    else
      return 0
  }
}


class Comp extends Comparator[Job] {
  override def compare(o1: Job, o2: Job): Int = {
    if (o1.realtime && !o2.realtime)
      return -1
    else if (!o1.realtime && o2.realtime)
      return 1

    if (o1.prio < o2.prio)
      return -1
    else if (o1.prio > o2.prio)
      return 1

    if (o1.createDate < o2.createDate)
      return -1
    else if (o1.createDate > o2.createDate)
      return 1
    else
      return 0
  }
}
