package cn.gridx.scala.lang.datetime

import org.joda.time.{DateTime, DateTimeZone, Days}

/**
  * Created by tao on 10/3/16.
  */
object Intervals {
  /*
  val start = new DateTime("2010-10-03T12:00:00")
  val end = new DateTime("2011-12-13T12:00:00")

  val (startYear, startMonth) = (start.getYear, start.getMonthOfYear)
  val (endYear, endMonth) = (end.getYear, end.getMonthOfYear)

  var d = start
  while (d.getYear <= endYear && d.getMonthOfYear <= endMonth) {
    println(d)
    d = d.plusMonths(1)
  }
  */

  def main(args: Array[String]) {
    val d1 = new DateTime("2015-01-01T")
    val d2 = new DateTime("2015-01-02")
    println(DaysBetween(d1, d2, true))  // 1
    println(DaysBetween(d2, d1, true))  // -1

    println(DaysBetween(new DateTime("2016-02-12T12:00:00.00"), new DateTime("2016-02-13T11:59:59.00"), true)) // 0
    println(DaysBetween(new DateTime("2016-02-12T12:00:00.00"), new DateTime("2016-02-13T12:00:00.00"), true)) // 1

    // 注意daylight saving time
    // 2016年的夏令时从3月13日开始
    println(DaysBetween(new DateTime("2016-03-12T12:00:00.00"), new DateTime("2016-03-13T12:00:00.00"), true)) //  1
    println(DaysBetween(new DateTime("2016-03-12T12:00:00.00-08:00"), new DateTime("2016-03-13T12:00:00.00-07:00"), true)) // 0

    println(DaysBetween(new DateTime("2016-03-12T12:00:00.00"), new DateTime("2016-03-13T12:00:00.00"), false)) //  1
    println(DaysBetween(new DateTime("2016-03-12T12:00:00.00-08:00"), new DateTime("2016-03-13T12:00:00.00-07:00"), false)) // 0


  }


  /********************************************************
    * 计算两个日期之间相隔多少天
    *
    * @param d1
    * @param d2
    * @param withDaylight 是否考虑夏令时
    *
    * 如果不考虑夏令时, 则只看两个日期的年月日相隔多少天
    * 如果考虑夏令时, 则要看是否存在快慢1小时的问题
    ******************************************************/
  def DaysBetween(d1: DateTime, d2: DateTime, withDaylight: Boolean): Int = {
    if (withDaylight)
      Days.daysBetween(d1, d2).getDays
    else {
      val (year1, month1, day1) = (d1.getYear, d1.getMonthOfYear, d1.getDayOfMonth)
      val (year2, month2, day2) = (d2.getYear, d2.getMonthOfYear, d2.getDayOfMonth)
      Days.daysBetween(new DateTime(s"$year1-$month1-${day1}T00:00:00-00:00"),
                       new DateTime(s"$year2-$month2-${day2}T00:00:00-00:00"))
        .getDays
    }
  }


}
