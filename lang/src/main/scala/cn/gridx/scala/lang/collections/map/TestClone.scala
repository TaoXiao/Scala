package cn.gridx.scala.lang.collections.map

import scala.collection.mutable

/**
  * Created by tao on 2/6/17.
  */
object TestClone extends App {
  val map = mutable.HashMap[String, Job]()
  map.put("A", Job("job-A", 100))
  val j = map.get("A").get
  j.setV(200)

  println(map)
}

case class Job(id: String, var v: Int) {
  def setV(nv: Int): Unit = {
    v = nv
  }
}
