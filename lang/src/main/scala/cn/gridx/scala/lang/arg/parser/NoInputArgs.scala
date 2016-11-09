package cn.gridx.scala.lang.arg.parser

import org.apache.log4j.LogManager
import scopt.OptionParser

/**
  * Created by tao on 8/4/16.
  */
object NoInputArgs {
  val logger = LogManager.getLogger(this.getClass)

  val parser: OptionParser[MyConfig] =
    new scopt.OptionParser[MyConfig]("请输入你的名字, 年龄, 是否为男性(Y|N)") {
      head("CLI Arguments Parser", "0.1")

      opt[Int]('a', "age") action {
        (a, c) => c.copy(age = a)
      } text("你的年龄")

      // 用required来要求该参数必须存在
      opt[String]('n', "name") action {
        (n, c) => c.copy(name = n)
      } text("你的名字")

      opt[Boolean]('m', "male") action {
        (m, c) => c.copy(male = m)
      } text("你是男性吗?")
    }

  def main(args: Array[String]): Unit = {
    logger.info("hello")
    parser.parse(args, MyConfig()) match {
      case Some(config) =>
        println(s"config = $config")
      case _ =>
        println("None")
    }
  }
}
