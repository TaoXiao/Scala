package cn.gridx.scala.lang.datetime

import java.util.Date

import org.joda.time._

/**
  * Created by tao on 12/21/15.
  */
object jodatimes {
  def main(args: Array[String]): Unit = {
    getWeekDay
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


  /**
    * 计算两个点之间的间隔时长
    * */
  def calcInterval(): Unit = {
    var d1 = new DateTime("2016-01-01T12:00:00")
    var d2 = new DateTime("2016-01-02T13:20:30")
    val days = Days.daysBetween(d1, d2).getDays
    println(s"days = $days")

    d1 = new DateTime("2016-01-01T12:00:00")
    d2 = new DateTime("2016-01-01T13:20:30")
    val hours = Hours.hoursBetween(d1, d2).getHours
    println(s"hours = $hours")

    val minutes = Minutes.minutesBetween(d1, d2).getMinutes
    println(s"minutes = $minutes")

    val seconds = Seconds.secondsBetween(d1, d2).getSeconds
    println(s"seconds = $seconds")
  }


  /**
    * 找到给定日期的00:00:00这个时刻
    * */
  def startOfSameDay(d: DateTime) = {
    val year    = d.getYear
    val month   = d.getMonthOfYear
    val day     = d.getDayOfMonth
    val hour    = d.getHourOfDay
    val minute  = d.getMinuteOfHour
    val second  = d.getSecondOfMinute

    new DateTime(s"${year}-${month}-${day}")
  }



  /**
    * 2015-02-01T13:59:00-08:00   ->    2015-02-02T00:00:00.000-08:00
    * */
  private def startOfNextDay(d: DateTime) = {
    val year    = d.getYear
    val month   = d.getMonthOfYear
    val day     = d.getDayOfMonth
    val hour    = d.getHourOfDay
    val minute  = d.getMinuteOfHour
    val second  = d.getSecondOfMinute

    new DateTime(s"${year}-${month}-${day}").plusDays(1)
  }

  /**
    * 判断是周几?
    * 是工作日还是周末?
    * */
  private def getWeekDay(): Unit = {
    val d = new DateTime("2017-01-01")
    println(d)
    println(s"dayOfWeek = ${d.getDayOfWeek}")
  }
}
