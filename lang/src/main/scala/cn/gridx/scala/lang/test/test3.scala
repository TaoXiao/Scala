package cn.gridx.scala.lang.test

import java.util.Date

import org.joda.time.{DateTime, DateTimeZone}

import scala.collection.mutable

/**
  * Created by tao on 8/11/16.
  */
object test3 {
  def main(args: Array[String]): Unit = {
    val d = new DateTime(2015, 12, 31, 0, 0, 0)
    println(d.getYear + ":" + d.getMonthOfYear + ":" + d.getDayOfMonth)
  }


  def convertFromUTC(millis: Long, newtz: DateTimeZone): DateTime = {
    val newT2 = new DateTime(millis,  DateTimeZone.UTC)
    try {
      val newTime = new DateTime(newT2.getYear, newT2.getMonthOfYear, newT2.getDayOfMonth,
        newT2.getHourOfDay, newT2.getMinuteOfHour, newT2.getSecondOfMinute, newtz)
      return newTime
    } catch {
      case e: Throwable => println(e.toString)
    }
    newT2
  }
}
