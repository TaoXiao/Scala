package cn.gridx.scala.lang.datetime

import org.joda.time.{DateTime, Days, Period}

/**
  * Created by tao on 10/15/16.
  */
object Process extends App {
  val d1 = new DateTime("2016-11-10T12:25:00")
  val d2 = new DateTime("2016-11-20T12:24:00")

  var d = d1
  while (!d.isAfter(d2) ) {
    println(d)
    d = d.plusDays(1)
  }

}
