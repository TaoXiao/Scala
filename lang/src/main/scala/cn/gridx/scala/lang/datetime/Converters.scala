package cn.gridx.scala.lang.datetime

import org.joda.time.DateTime

/**
  * Created by tao on 4/12/16.
  */
object Converters {

  def main(args: Array[String]): Unit = {
    val milli = 1460449019992L // 2016-04-12T16:16:59.992+08:00
    val t1 = toPreSharpHour(milli)
    val t2 = toNextSharyHour(milli)

    println(t1)
    println(t2)

    if (t1.plusHours(1).equals(t2))
      println("Euqals")
    else
      println("Not equals")

    if (t1.isBefore(t2))
      println("t1 is before t2")

  }

  /**
    * 将milli对应的时间点转为上一个整小时
    * 例如:  2016-06-06 18:28:30  ->   2016-06-06 18:00:00
    * */
  def toPreSharpHour(milli: Long): DateTime = {
    val remainder = milli%(3600*1000)
    new DateTime(milli - remainder)
  }


  /**
    * 将milli对应的时间点转化为下一个整小时
    * 例如:  2016-06-06 18:28:30  ->   2016-06-06 19:00:00
    * */
  def toNextSharyHour(milli: Long): DateTime = {
    toPreSharpHour(milli).plusHours(1)
  }


  /**
    * 计算t1和t2之包含了多少个hour,
    * 例如 18点与22点之间包含了 5个hours (18点, 19点, 20点, 21点, 22点)
    * */
  def hoursCount(t1: DateTime, t2: DateTime): Unit = {

  }
}
