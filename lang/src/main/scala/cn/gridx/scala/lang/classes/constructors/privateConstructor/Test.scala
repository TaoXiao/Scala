package cn.gridx.scala.lang.classes.constructors.privateConstructor

/**
 * Created by tao on 8/20/15.
 */
object Test {
    def main(args: Array[String]) = {
        val p1 = Person.getInstance
        println(s"count = ${p1.count}")

        val p2 = Person.getInstance
        println(s"count = ${p1.count}")


        val p3 = Person.getInstance
        println(s"count = ${p1.count}")
    }
}
