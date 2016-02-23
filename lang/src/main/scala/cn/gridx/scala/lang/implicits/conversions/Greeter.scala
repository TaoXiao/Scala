package cn.gridx.scala.lang.implicits.conversions

/**
 * Created by tao on 11/24/15.
 */
object Greeter {
    def greet(name: String)(implicit prompt: Prompt, drink: Drink) = {
        println(
            s"""${prompt.msg} Welcom ${name},
               |we have a cup of ${drink.drink} for you !""".stripMargin)

        println(s"""${prompt.msg}""")
    }
}
