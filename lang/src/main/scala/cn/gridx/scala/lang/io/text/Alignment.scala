package cn.gridx.scala.lang.io.text

/**
  * Created by tao on 6/4/16.
  */
object Alignment {
  def main(args: Array[String]): Unit = {
    AlignString()
    AlignNumber()
  }


  /**
    * 左对齐对齐字符串
    * 输出
    *
      信息1 Hello world!        结束
      信息2 Goodbye, earth !    结束

    * */
  def AlignString(): Unit = {
    val s1 = "Hello world!"
    val s2 = "Goodbye, earth !"

    val res1 = String.format("%s %-20s%s", "信息1", s1, "结束")
    val res2 = String.format("%s %-20s%s", "信息2", s2, "结束")

    println(res1)
    println(res2)
  }


  /**
    * 输出为
      1.73
      20.34
      结果  1.73
      结果 20.34

    * */
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
  }

}
