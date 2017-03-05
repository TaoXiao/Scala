package cn.gridx.scala.lang.processes


import java.io.File

import sys.process._
/**
  * Created by tao on 11/18/16.
  */
object ExecuteCommand {
  def main(args: Array[String]): Unit = {
    val folder = new File(".").getAbsoluteFile.getParent
    println(folder)
  }



}
