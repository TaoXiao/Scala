package cn.gridx.scala.lang.control_structures.loop_continue

import util.control.Breaks._

/**
  * Created by tao on 10/28/16.
  */
object Test extends App {
  println("start")

  for (i <- 0 until 100) {
    breakable {
      if (i == 9)
        break // we only skip printing 9
      println(i)
    }
  }

  println("stop")
}
