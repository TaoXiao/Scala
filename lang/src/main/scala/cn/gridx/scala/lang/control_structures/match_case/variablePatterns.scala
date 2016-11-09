package cn.gridx.scala.lang.control_structures.match_case

/**
 * Created by tao on 11/22/15.
 */
object Driver {
    def main(args: Array[String]): Unit = {
        val x1 = "hello"
        val x2 = 100
        val x3 = 1.11
        testVariablePattern_1(x1)     // 输出：String => hello
        testVariablePattern_1(x2)     // 输出：Int => 100
        testVariablePattern_1(x3)     // 输出：can not match

        println("+"*20)

        testVariablePattern_2("Hello")  // 输出：1. hello
        testVariablePattern_2("Hi")     // 输出：2. hi
        testVariablePattern_2("no")     // 输出：3. I don't know


        println("+"*20)

        testVariablePattern_3(100)      // 输出：x1 => 100
        testVariablePattern_3(200)      // 输出：x1 => 100
        testVariablePattern_3(300)      // 输出：x1 => 100
        testVariablePattern_3(400)      // 输出：x1 => 100

        println("+"*20)

        testVariablePattern_4(100)      // 输出：x1 => 100
        testVariablePattern_4(200)      // 输出：x2 => 200
        testVariablePattern_4(300)      // 输出：x3 => 300
        testVariablePattern_4(400)      // 输出：does not match


      println("+"*20)
      testVariablePattern_6("res")
      testVariablePattern_6("com")
      testVariablePattern_6("agr")
      testVariablePattern_6("hello")

    }



    /**
     * 关于`match ... case`中的 variable patterns，
     * 参考 http://alvinalexander.com/scala/scala-unreachable-code-due-to-variable-pattern-message
     * @param x
     */
    def testVariablePattern_1(x: Any): Unit = {
        x match {
            case s: String  => println(s"String => ${s}")
            case i: Int     => println(s"Int => ${i}")
            case _          => println("can not match")
        }
    }


    def testVariablePattern_2(x: String): Unit = {
        x match {
            case "Hello" => println("1. hello")
            case "Hi"    => println("2. hi")
            case _       => println("3. I don't know")
        }
    }


    /**
     * x1,x2,x3都是Int类型的，无法区分
     * Scala uses a simple lexical rule for disambiguation:
     * a simple name starting with a lowercase letter is taken to be a pattern variable;
     * all other references are taken to be constants.
     * @param x
     */
    def testVariablePattern_3(x: Int): Unit = {
        val x1 = 100
        val x2 = 200
        val x3 = 300

        x match {
            case x1 => println(s"x1 => 100")
            case x2 => println(s"x2 => 200")
            case x3 => println(s"x3 => 300")
            case _  => println("does not match")
        }
    }

    /**
     * 解决方法1：变量名用backticks包起来
     * @param x
     */
    def testVariablePattern_4(x: Int): Unit = {
        val x1 = 100
        val x2 = 200
        val x3 = 300

        x match {
            case `x1` => println(s"x1 => 100")
            case `x2` => println(s"x2 => 200")
            case `x3` => println(s"x3 => 300")
            case _  => println("does not match")
        }
    }

    /**
     * 解决方法2: 将变量名变为大写字母开头的
     * @param x
     */
    def testVariablePattern_5(x: Int): Unit = {
        val X1 = 100
        val X2 = 200
        val X3 = 300

        x match {
            case X1 => println(s"X1 => 100")
            case X2 => println(s"X2 => 200")
            case X3 => println(s"X3 => 300")
            case _  => println("does not match")
        }
    }

    def testVariablePattern_6(x: String) = {
        x match {
          case contractScopes.`resSS` =>
            println("RES")
          case contractScopes.`com` =>
            println("COM")
          case contractScopes.`agr` =>
            println("AGR")
          case x =>
            println(s"unknown : $x")
        }

    }

}


object contractScopes {
  val resSS = "res"
  val com = "com"
  val agr = "agr"
}
