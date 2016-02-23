package cn.gridx.scala.lang.control_structures.loop_break


/**
 * Created by tao on 11/23/15.
 *
 * Scala的for循环中没有原生的`break`, `continue`关键字
 */
object example {
    def main(args: Array[String]): Unit = {
        //SingleBreak
        NestedBreak
    }

    /**
     * 终止单层循环
     */
    def SingleBreak(): Unit = {
        val loop = new scala.util.control.Breaks
        loop.breakable {
            for (x <- 0 until 100) {
                if (x >= 5)  loop.break
                println(x )
            }
        }
    }


    /**
     * 选择结束哪一层循环
     */
    def NestedBreak(): Unit = {
        val outer = new scala.util.control.Breaks
        val inner = new scala.util.control.Breaks

        outer.breakable {
            for (x <- 0 until 100) {
                if (x >= 5) outer.break  // 结束外层循环
                print(x + " -> ")
                inner.breakable {
                    for (y <- 0 until 100) {
                        if (y >= 10)  inner.break  // 结束内层循环
                        print(y + " ")
                    }
                }
                println
            }
        }
    }
}
