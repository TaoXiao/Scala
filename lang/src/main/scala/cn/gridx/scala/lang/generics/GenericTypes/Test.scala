package cn.gridx.scala.lang.generics.GenericTypes

/**
 * Created by tao on 1/3/16.
 */
object Test {
    def main(args: Array[String]): Unit = {
        println(classOf[scala.Tuple2[_, _]])
        println(classOf[Array[scala.Tuple2[_,_]]])
        println(classOf[scala.Tuple2[Any, Any]])
        //println(scala.Tuple2[_, _].getClass)
    }
}
