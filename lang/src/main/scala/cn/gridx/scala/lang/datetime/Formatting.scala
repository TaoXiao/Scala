package cn.gridx.scala.lang.datetime

import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by tao on 4/5/16.
  */
object Formatting {
  def main(args: Array[String]): Unit = {
    UtilDate
  }

  def UtilDate(): Unit = {
    val dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = new Date()
    val str = dt.format(date)
    println(date)   // Tue Apr 05 20:28:05 CST 2016
    println(str)    // 2016-04-05 20:28:05
  }
}
