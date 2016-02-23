package cn.gridx.scala.lang.generics.TypeTags

/**
 * Created by tao on 11/23/15.
 */
object Examples {
    def main(args: Array[String]): Unit = {
        val tt: scala.reflect.runtime.universe.TypeTag[Int] = scala.reflect.runtime.universe.typeTag[Int]
        println(tt) // 输出 TypeTag[Int]

        val ct: ClassManifest[String] = scala.reflect.classTag[String]
        println(ct) // 输出 java.lang.String
    }
}
