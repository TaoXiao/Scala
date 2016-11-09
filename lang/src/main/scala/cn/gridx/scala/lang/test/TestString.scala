package cn.gridx.scala.lang.test

import org.joda.time.{DateTime, Period}

import scala.collection.mutable.{ArrayBuffer, HashMap => MMap}
/**
  * Created by tao on 9/24/16.
  */
object TestString extends App {
  println(new DateTime("2016-10-20T23:59:59.000-07:00").minusDays(365))
}
