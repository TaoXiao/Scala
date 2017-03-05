package cn.gridx.scala.aop.concepts

/**
  * Created by tao on 12/6/16.
  */

object Logging {
  def logCall[T](msg: String)(block: => T): T = {
    println(s"message = $msg")
    block
  }
}

object MyTest  {
  def InsertNum(num: Int): Int = {
    println(s"$num is inserted !")
    return num*100
  }

  def main(args: Array[String]): Unit = {
    val result = Logging.logCall("插入日志")(InsertNum(20))
    println(s"result = $result")
  }
}