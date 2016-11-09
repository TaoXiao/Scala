package cn.gridx.scala.lang.datetime

import java.text.SimpleDateFormat
import java.util.Date

import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.joda.time.{DateTime, DateTimeZone}

/**
  * Created by tao on 4/5/16.
  */
object Formatting {
  def main(args: Array[String]): Unit = {
    println(new DateTime(2145916800000L))

    // DateTimeZone.setDefault(DateTimeZone.forID("America/Los_Angeles"))
    val day: DateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime("2016-03-06 12:34:56")
    val milli = day.getMillis
    println(milli)
    println(new Date(milli))
  }

  def UtilDate(): Unit = {
    /*
    val formatter = new formatter("yyyy-MM-dd HH:mm:ss")
    DateTimeZone.setDefault(DateTimeZone.forID("America/Los_Angeles"))
    DateTime.parse("", formatter)
    */
    val day: DateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime("2016-03-06")
    println(day)
  }



}
