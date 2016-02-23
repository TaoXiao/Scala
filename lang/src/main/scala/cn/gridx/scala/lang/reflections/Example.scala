package cn.gridx.scala.lang.reflections

/**
 * Created by tao on 11/17/15.
 */
object Example {
    def main(args: Array[String]): Unit = {
        val stu = new Student("null", "null", "null")
        println(countNonNullFields(classOf[Student], stu))
    }

    /**
     * 计算一个类的实例中非空的字段有多少个？
     * @param clz
     * @param obj
     * @tparam T
     */
    def countNonNullFields[T](clz:Class[T], obj:T): Int = {
        val fields = clz.getDeclaredFields
        var count = 0

        for (f <- fields) {
            if (null != f.get(obj)) {
                count = count + 1
            }
        }

        count
    }
}
