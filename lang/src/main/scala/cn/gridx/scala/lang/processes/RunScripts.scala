package cn.gridx.scala.lang.processes

import java.io.File

/**
  * Created by tao on 9/29/16.
  */
object RunScripts {
  def main(args: Array[String]): Unit = {
    run_2()
  }


  /**
    * 执行 command, 并得到script的返回值
    */
  def run_1(): Unit = {
    import sys.process._

    val script = "/Users/tao/Downloads/test.sh"
    println("开始")
    val ret: Int = s"$script"! ;// ret是script的返回值
    println(s"返回值 = $ret")  // 返回96
  }


  /**
    * 将script的输出重定向到文件
    */
  def run_2(): Unit = {
    import sys.process._

    val script = "/Users/tao/Downloads/test.sh"
    println("开始")
    val ret: Int = (s"$script" #>> new File("log"))!

    println(s"返回值 = $ret")
  }


}



