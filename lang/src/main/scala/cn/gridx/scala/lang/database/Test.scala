package cn.gridx.scala.lang.database

import org.joda.time.{DateTime, DateTimeZone}

import scala.collection.mutable

/**
  * Created by tao on 10/19/16.
  */
object Test extends App {
  val m1 = mutable.HashMap[String, Int]()
  m1.put("One", 1)
  m1.put("Two", 2)
  m1 += (("Three", 3))

  val m2 = mutable.HashMap[String, Int]()
  m2.put("Four", 4)
  m2.put("Five", 5)

  println(m1 ++ m2)

}
