package cn.gridx.scala.lang.functions.parameters

/**
 * Created by tao on 11/24/15.
 */
object VariableParam {

    def main(args: Array[String]): Unit = {
        f("a", "b", "c")
        f()

        val A = Array("X", "Y", "Z")
        // f(A)     // 不能将数组直接传给f
        f(A: _*)    // _* 告诉编译器将A的每一个元素各自当做参数传给方法`f`

    }


    /**
     * 可变长度参数
     * 可以传给方法`f`任意多个String类型的参数，0个，1个，2个，...
     * @param param
     */
    def f(param: String*): Unit = {
        println(s"""# of parameters: ${param.length}""")
        for (p <- param)
            println(p)
    }
}
