package cn.gridx.scala.lang.collections.parallel

import java.util.Date

import scala.collection.parallel.immutable.ParSeq

/**
  * Created by tao on 6/18/16.
  */
object ParCollections {
  def main(args: Array[String]): Unit = {
    val list = (1 to 10000000).toList
    val ts = System.currentTimeMillis()
    val newList: ParSeq[Int] = list.par.map(_ +42)
    println(s"耗时 ${(System.currentTimeMillis() - ts)/1000} 秒")
    println(newList.size)
  }
}
