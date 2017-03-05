package cn.gridx.scala.lang.functions.definitions

/**
  * Created by tao on 11/14/16.
  */
object NestedFunctions {
  def main(args: Array[String]): Unit = {
    val ret = f1()
    println(s"ret = $ret")
  }

  def f1(): Int = {
    def f11(): Int = {
      println("f11")
      return 11
    }
    println("f1")
    return f11()
  }

}
