package cn.gridx.scala.lang.test

import org.joda.time.{DateTime, Period}

import scala.collection.mutable.{ArrayBuffer, HashMap => MMap}
/**
  * Created by tao on 9/24/16.
  */
object TestString extends App {
  val s = "hello\n"
  val s1 = s.replace("\n", "")
  println(s"[$s1]")

}
