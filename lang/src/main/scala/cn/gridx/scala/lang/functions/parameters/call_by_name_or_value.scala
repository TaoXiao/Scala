package cn.gridx.scala.lang.functions.parameters

/**
 * Created by tao on 11/19/15.
 */
object call_by_name_or_value {

    def loop: Unit = {
        println("loop")
        loop
    }

    /**
     * 用`def`和`val`定义函数的区别是什么？
     */
    //val x = loop  // 用val定义，是call-by-value，这里会无限地循环执行，即使x还未被调用
    def y = loop  // 用def定义，是call-by-name，只有调用y时才会执行

    def main(args: Array[String]): Unit = {
       // println("Hello")
       // and(false, loop)

       // b1(a)
       // b2(a)

        def NullField = """\N"""   // 注意：不可写为 \N
        println(NullField)

    }

    /**
     * y: => Unit 表示对参数y的引用是 call-by-name
     * 当x为false时，y不会调用
     * 当x为true时，每当函数`and`中引用一次y时，都会调用y
     * 函数参数的默认引用方式为call-by-value
    */
    def and(x:Boolean, y: => Unit):Unit = {
        if (x)
            y
        else
            println("do nothing")
    }


    def a():Int = {
        println("I'm  a")
        100
    }

    /** call-by-value
     * 输出：
     *  I'm  a
        x1 = 100
        x2 = 100
        x3 = 100
     */
    def b1(x:Int): Unit = {
        println(s"x1 = ${x}")
        println(s"x2 = ${x}")
        println(s"x3 = ${x}")
    }


    /**
     * call-by-name
     * 输出：
     *      I'm  a
            x1 = 100
            I'm  a
            x2 = 100
            I'm  a
            x3 = 100
     *
     *
     * @param x
     */
    def b2(x: => Int): Unit = {
        println(s"x1 = ${x}")
        println(s"x2 = ${x}")
        println(s"x3 = ${x}")
    }
}
