package cn.gridx.scala.lang.classes.constructors.fields.lazyevaluate



/**
 * Created by tao on 8/21/15.
 */
class Foo(name: String) {
    val x = {
        println("I'm x, I am evaluated now")
        "X"
    }


    /**
     * When a field is declared as `lazy`, it won't be evaluated
     * until it is accessed
     */
    lazy val y = {
        println("I'm y, I am evaluated now")
        "Y"
    }

}
