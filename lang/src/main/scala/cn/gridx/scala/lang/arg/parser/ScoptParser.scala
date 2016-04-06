package cn.gridx.scala.lang.arg.parser
import scopt.OptionParser

/**
  * Created by tao on 4/6/16.
  */


case class MyConfig(name: String = "Unknown Name", age: Int = -1, male: Boolean = true)

object ScoptParser {

  def main(args: Array[String]): Unit = {
    parser.parse(args, MyConfig()) match {
      case Some(config) => println(s"\n输入的参数合法, 列出参数: \n\t$config\n" +
        s"home = $home\n")
      case _ => println(s"\n输入的参数非法\n")
    }
  }

  var home: String  = _

  val parser: OptionParser[MyConfig] =
    new scopt.OptionParser[MyConfig]("请输入你的名字, 年龄, 是否为男性(Y|N)") {
      head("CLI Arguments Parser", "0.1")

      opt[Int]('a', "age") action {
        (a, c) => c.copy(age = a)
      } text("你的年龄")

      opt[String]('n', "name") action {
        (n, c) => c.copy(name = n)
      } text("你的名字")

      opt[Boolean]('m', "male") action {
        (m, c) => c.copy(male = m)
      } text("你是男性吗?")

      opt[String]('h', "home") action {
        (h, c) => { home = h }
        c
      } text ("你的家庭住址")

    }



}
