package cn.gridx.scala.spray.request.get

import java.util.Date

/**
  * Created by tao on 12/1/16.
  */
object utils {
  def blockingOp(): Unit = {
    println(s"进入 ${new Date()}")
    Thread.sleep(10*1000)
    println(s"退出 ${new Date()}")
  }
}
