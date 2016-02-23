package cn.gridx.scala.lang.generics

import scala.reflect.ClassTag

/**
 * Created by tao on 11/18/15.
 *
 * 范型方法
 */
object GenericMethods {
    def main(args: Array[String]): Unit = {
        val s = new Student("Jack", "Nanjing", "22")
        val t = new Teacher("Tom", "Shanghai", "30")
        println(CloneInstance(classOf[Student], s)) // 必须用classOf[Student], 不能用s.getClass
        println(CloneInstance(classOf[Teacher], t))

        println("*"*50)

        println(CreateInstanceByClass(classOf[Student]))
        println("+"*10)
        println(CreateInstanceByClass(classOf[Teacher]))
        println("+"*10)
        println(CreateInstanceByClass(classOf[String]))

        println("*"*50)

        println(CreateInstanceByClass_1(classOf[Student]))
        println("+"*10)
        println(CreateInstanceByClass_1(classOf[Teacher]))
        println("+"*10)
        println(CreateInstanceByClass_1(classOf[String]))
    }

    /**
     * 对输入的任何类型的对象，克隆一个新实例并返回
     * @param clz
     * @param obj
     * @tparam T
     * @return
     */
    def CloneInstance[T:ClassTag](clz:Class[T], obj:T): T = {
        val instance = clz.newInstance()
        val fields = clz.getDeclaredFields

        for (f <- fields) {
            f.setAccessible(true) // 防止不能访问private field
            f.set(instance, f.get(obj))
        }

        instance
    }


    /**
     * 通过class type来匹配
     */
    def CreateInstanceByClass[T: ClassTag](clz:Class[T]): Any = {
        val studentClzName = classOf[Student].getCanonicalName
        val teacherClzName = classOf[Teacher].getCanonicalName

        val obj = clz.getCanonicalName match {
            case `studentClzName` => new Student("n", "c", "a")
            case `teacherClzName` => new Teacher("N", "H", "G")
            case _       =>
        }

        obj
    }


    /**
     * 通过 class type 来匹配
     */
    def CreateInstanceByClass_1[T: ClassTag](clz:Class[T]): Any = {
        val studentClz = classOf[Student]
        val teacherClz = classOf[Teacher]
        val obj = clz match {
            case `studentClz` => new Student("n", "c", "a")
            case `teacherClz` => new Teacher("N", "H", "G")
            case _          =>
        }

        obj
    }
}
