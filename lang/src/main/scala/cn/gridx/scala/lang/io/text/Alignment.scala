package cn.gridx.scala.lang.io.text

import java.text.DecimalFormat

/**
  * Created by tao on 6/4/16.
  */
object Alignment {
  def main(args: Array[String]): Unit = {
    RightAlign
  }


  /**
    * 左对齐对齐字符串
    * 输出
    *
    * 信息1 Hello world!        结束
    * 信息2 Goodbye, earth !    结束
    *
    * */
  def AlignString(): Unit = {
    val s1 = "Hello world!"
    val s2 = "Goodbye, earth !"

    val res1 = String.format("%-40s%s", s1, "结束")
    val res2 = String.format("%-40s%s", s2, "结束")

    println(res1)
    println(res2)
  }


  def RightAlign(): Unit = {
    val s1 = "Hello world!"
    val s2 = "Goodbye, earth !"

    val res1 = String.format("%18s |%s", s1, "结束")
    val res2 = String.format("%18s |%s", s2, "结束")

    println(res1)
    println(res2)
  }


  /*
    * 输出为
     1.73
     20.34
     结果  1.73
     结果 20.34
     AA        123       true
     BBBBBB    123456    false

  **/
  def AlignNumber(): Unit = {
    val n1: Float = 1.73f
    val n2: Float = 20.33985f

    // 将数字转换为字符串 (自动进行四舍五入)
    val s1 = new java.text.DecimalFormat(".###").format(n1)
    val s2 = new java.text.DecimalFormat(".###").format(n2)
    println(s1)
    println(s2)

    // 对字符串进行右对齐操作
    println(String.format("%s%6s", "结果", s1))
    println(String.format("%s%6s", "结果", s2))

    println(String.format("%-10s%-10s%s", "AA",  123.toString, true.toString))
    println(String.format("%-10s%-10s%s", "BBBBBB", 123456.toString, false.toString))
  }

}
