package cn.gridx.scala.lang.test

import sys.process._
/**
  * Created by tao on 11/24/16.
  */
object TestShell extends App  {
  println("OK")
  val cmd = "ls -al"

  val ret = cmd!!

  println(s"ret = $ret")
}
