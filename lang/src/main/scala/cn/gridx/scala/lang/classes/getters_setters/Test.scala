package cn.gridx.scala.lang.classes.getters_setters

/**
 * Created by tao on 8/20/15.
 */
object Test extends App {
    val p = new Person("Xiao", 31)
    println(p.name)     //accessor

    p.name = "Tao"      // mutator
    println(p.name)
}
