package cn.gridx.scala.lang.control_structures.match_case

/**
  * Created by tao on 2/25/16.
  */
object basics {
  def main(args: Array[String]): Unit = {
    val x = 300

    x match {  //  一个case后面即使有多条语句,也不需要大括号 {}
      case 100 => println("I'm 100")
                  println("I'm hit")

      case 200 => println("I'm 200")
                  println("I'm also hit")

      case _ => println("I don't know who I am")
                println("I'm yet hit")
    }

    val y = "你好"

    y match {
      // case "", "你好"    =>
      case "hello" => println("找到了")
      case _ => println("没找到")
    }


    matchString("a")
    matchString("")
    matchString(null)
  }


  def matchString(str: String): Unit = {
    str match {
      case s: String =>
        println(s"字符串为$s")
    }
  }

}
