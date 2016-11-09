package cn.gridx.scala.lang.test

import java.io.File

import sys.process._

/**
  * Created by tao on 10/26/16.
  */
object TestPWD extends App {
  val script = "/Users/tao/IdeaProjects/gridx/test.sh"
  val cmd = (s"$script" #>> new File("./my.log"))
  println(s"cmd  = $cmd")
  cmd!
}
