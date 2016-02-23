package cn.gridx.scala.lang.traits.basics

/**
 * Created by tao on 12/30/15.
 */

// Bird1 只会飞
class Bird1 extends Bird with Flying {
    val song = "我会飞飞飞。。。 "
}

object Bird1 extends App {
    val bird = new Bird1()
    bird.fly()
    bird.sing()
}