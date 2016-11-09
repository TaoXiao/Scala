package cn.gridx.scala.lang.processes

import java.io.{BufferedReader, InputStreamReader}
import java.util.Date

/**
  * Created by tao on 9/29/16.
  */
object NewProcess extends App {
  println("当前时间: " + new Date())
  Thread.sleep(1000*1)

  println("即将reboot ...")
  import sys.process._
  //val pb: ProcessBuilder = Process("/bin/bash /Users/tao/IdeaProjects/Scala/reboot.sh")
  // val p: Process = pb.run
  Runtime.getRuntime.exec("/bin/bash /Users/tao/IdeaProjects/Scala/reboot.sh")
  /* val in = new BufferedReader(new InputStreamReader(p.getInputStream()))
  var line: String  = in.readLine()
  while (line != null) {
    println(line)
    line = in.readLine()
  } */

  // Runtime.getRuntime.exec("/bin/bash /Users/tao/IdeaProjects/Scala/reboot.sh")

  // Thread.sleep(3000)
  println("主进程即将自杀 。。。")

  System.exit(0)
}


object Coordinator extends App {
  println("协调者正在运行")
}
