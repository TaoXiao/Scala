package cn.gridx.scala.akka

import java.util.Date

/**
  * Created by tao on 9/30/16.
  */
object Test extends App {
  for (i <- Range(0, 100)) {
    println(new Date())
    Thread.sleep(3000)
  }
}
