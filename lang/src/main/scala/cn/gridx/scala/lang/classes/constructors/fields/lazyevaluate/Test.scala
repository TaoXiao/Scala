package cn.gridx.scala.lang.classes.constructors.fields.lazyevaluate


/**
 * Created by tao on 8/21/15.
 */

object Test extends App {
    // 输出 I'm x, I am evaluated now
    val f = new Foo("foo")


    // 输出
    // I'm y, I am evaluated now
    // Y
    println(f.y)
}
