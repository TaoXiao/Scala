package cn.gridx.scala.lang.exceptions

/**
 * Created by tao on 12/14/15.
 */
object Basic {
    def main(args: Array[String]): Unit = {
        val a = 100
        val b = 0

        val s = "abc'def"
        println(s.replace("'", "\\'"))

        try {
            // val x = a / b
            // println(s"结果为 ${x}")

            val x = 999999999
            val y = 999999999
            println(x*y*x*y)
        } catch {
            case ex : ArithmeticException => println(s"算数异常 >>> ${ex}")
            case ex : Exception => println(s"未知异常 >>> ${ex}")
        }
    }
}
