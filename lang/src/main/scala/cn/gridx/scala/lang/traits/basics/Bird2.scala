package cn.gridx.scala.lang.traits.basics

/**
 * Created by tao on 12/30/15.
 */

// Bird2 既会飞，也会游
class Bird2 extends Bird with Flying with Swimming with Running {
    val song = "我会飞，也能游，还能跑 ！！"
}

object Bird2 extends App {
    val b = new Bird2()
    b.fly()
    b.swim()
    b.run()
    b.sing()
}
