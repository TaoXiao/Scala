package cn.gridx.scala.lang.exceptions

/**
  * Created by tao on 12/25/16.
  */
object TestCatch {
  def main(args: Array[String]): Unit = {
    val bean = new Bean()
    val x = bean.f(0)
  }
}

class Bean {
  def f(a: Int) = {
    println(s"a = $a")
    try {
      val b = 100/a
      println(s"100/a = $a")
    } catch {
      case ex: Throwable =>
        println("message --- " + ex.getMessage)
        println("stackTrace --- " + ex.getStackTraceString)
    } finally {
      println("在finally中")
    }


    println("将从f返回")
  }

}
