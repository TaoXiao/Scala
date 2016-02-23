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
    }
}
