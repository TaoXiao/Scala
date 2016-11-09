package cn.gridx.scala.lang.collections.compare

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
  * Created by tao on 7/9/16.
  */
object SortExamples {

  def main(args: Array[String]): Unit = {
    complexSorting()
  }


  def PeriodSorter(p1:Period, p2:Period): Boolean = {
    if (p1.start <= p2.start)
      true
    else
      false
  }

  private case class Period(start: Long, end: Long)

  // 复合排序
  def complexSorting(): Unit = {
    val L = List((1, 22), (1, 3), (1, 14), (0, 15), (3, 18), (2, 0), (3, 0))
    L.sortBy(x => (x._1, x._2)).foreach(println)

  }


}
