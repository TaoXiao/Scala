package cn.gridx.scala.lang.processes

/**
  * Created by tao on 9/29/16.
  */
object RunScripts extends App {
  import sys.process._
  val script = "/Users/tao/Downloads/test.sh"
  s"$script" !
}
