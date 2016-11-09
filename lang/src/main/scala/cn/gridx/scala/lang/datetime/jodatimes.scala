package cn.gridx.scala.lang.datetime

import java.util.Date

import org.joda.time.DateTime

/**
  * Created by tao on 12/21/15.
  */
object jodatimes {
  def main(args: Array[String]): Unit = {
    val d1 = new DateTime("2015-04-16T00:00:00.000-07:00")
    val d2 = new DateTime("2015-04-16T23:00:00.000-07:00")
    val t1 = new DateTime("2015-04-16T20:00:00.000-07:00")
    val t2 = new DateTime("2015-05-16T20:00:00.000-07:00")
    if (!t1.isBefore(d1) && !t1.isAfter(d2))
      println("t1 yes")
    else
      println("t1 no")

    if (!t2.isBefore(d1) && !t2.isAfter(d2))
      println("t2 yes")
    else
      println("t2 no")
    // compareDate
    // long2Date
    // moveDate
    // getSharpClock
  }

  /**
    * 将long类型的数据转换成DateTime
    **/
  def long2Date(): Unit = {
    val ts = 1447372799*1000
    val dt = new DateTime(ts)
    println(dt)

    println(new Date(ts))
  }


  /**
    * 比较日期
    * */
  def compareDate(): Unit = {
    val d = DateTime.now.toString("yyyy-MM-dd HH:mm:ss")
    val d1 = "2015-12-21 19:44:25"
    println(d1.compareTo(d))  // -1 , 表示小于
  }

  /**
    * 向前或者向后移动几天/几个月
    * */
  def moveDate(): Unit = {
    val today = new DateTime(System.currentTimeMillis())
    println(s"今天 $today")

    val d1 = today.minusDays(365)
    val d2 = today.plusDays(365)

    println(s"365天之前 $d1")
    println(s"365天之后 $d2")
  }

  /**
    * 取得一个时间点对应的整点
    * 例如, 3:55 对应的整点就是 3:00
    * */
  def getSharpClock(): Unit = {
    val today =  new DateTime(System.currentTimeMillis())
    val hour = today.getHourOfDay
    println(hour)
  }
}
