package cn.gridx.scala.lang.classes.case_class

/**
 * Created by tao on 8/20/15.
 */
object TestCaseClass {
    def main(args: Array[String]): Unit = {
        /**
         * 输出
            Person(String, int):  name=Xiao,  age=30
            Person(String, int):  name=[Unknown Name],  age=-1
            Person(String, int):  name=Xiao,  age=-1
            Person(String, int):  name=[Unknown Name],  age=30
         */
        val p1 = Person("Xiao", 30) // 新建case class实例不需要new
        val p2 = Person()
        val p3 = Person("Xiao")
        val p4 = Person(30)


        val a = "Hello"
        val b = 100
        val c = 99.9999
        val B = Bean(b = b, c = c, a = a)
        println(B)
    }
}

case class Bean(a: String, b: Int,  c: Double) {
    override
    def toString(): String = {
        s"""a = $a
           |b = $b
           |c = $c
         """.stripMargin
    }
}
