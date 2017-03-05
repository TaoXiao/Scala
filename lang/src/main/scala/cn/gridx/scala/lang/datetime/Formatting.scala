package cn.gridx.scala.lang.datetime

import java.text.SimpleDateFormat
import java.util.Date


import com.codahale.jerkson.Json
import org.json4s._
import org.json4s.jackson.JsonMethods
import org.json4s.jackson.Serialization._
import spray.json._

import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.joda.time.{DateTime, DateTimeZone}

/**
  * Created by tao on 4/5/16.
  */
object Formatting {
  def main(args: Array[String]): Unit = {
    implicit val format = org.json4s.DefaultFormats

    // val s = List("a", "b")
    //val json = Json.generate(s)
    //println(json)

    val obj  = JsonMethods.parse("""["a","b"]""").extract[List[String]]
    println(obj)
//    println(new DateTime(2145916800000L))
//
//    // DateTimeZone.setDefault(DateTimeZone.forID("America/Los_Angeles"))
//    val day: DateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime("2016-03-06 12:34:56")
//    val milli = day.getMillis
//    println(milli)
//    println(new Date(milli))
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


  def format(): Unit = {
    val now = DateTime.now()
    println(now)
    val f = DateTime.now.toString("yyyy-MM-dd")
    println(f)
  }


}
