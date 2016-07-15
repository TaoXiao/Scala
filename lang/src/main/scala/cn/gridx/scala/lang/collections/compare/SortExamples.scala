package cn.gridx.scala.lang.collections.compare

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
  * Created by tao on 7/9/16.
  */
object SortExamples {

  def main(args: Array[String]): Unit = {
    //DateTimeZone.setDefault(DateTimeZone.forID("America/Los_Angeles"))
    //val day = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime("2016-05-06 12:34:56")
    //println(day)

    // println(new DateTime(1457769540000L))
  }


  def PeriodSorter(p1:Period, p2:Period): Boolean = {
    if (p1.start <= p2.start)
      true
    else
      false
  }

  private case class Period(start: Long, end: Long)

}
