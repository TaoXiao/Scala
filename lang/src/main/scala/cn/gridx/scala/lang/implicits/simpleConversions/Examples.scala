package cn.gridx.scala.lang.implicits.simpleConversions

import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by tao on 4/5/16.
  */
object Examples {
  implicit def date2String(date: Date): String = {
    println("implicit conversion invoked")
    val dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    dt.format(date)
  }

  def main(args: Array[String]): Unit = {
    val date = new Date()
    val str = "Time: " + date // 不行, 没法调用implicit
    printDate(str)

    printDate(new Date()) // 可以调用implicit
  }

  def printDate(str: String): Unit = {
    println(str)
  }
}
