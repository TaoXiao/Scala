package cn.gridx.scala.lang.traits.basics

/**
 * Created by tao on 12/30/15.
 */
class Bird3 extends Bird {
    val song = "我啥都不会"
}

object Bird3 extends App {
    val b = new Bird3()
    b.sing()
}
