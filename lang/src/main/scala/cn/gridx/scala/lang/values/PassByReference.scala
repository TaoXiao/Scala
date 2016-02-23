package cn.gridx.scala.lang.values

/**
 * Created by tao on 6/27/15.
 */
object PassByReference {
    def main(args: Array[String]): Unit = {
        val objA = new A(100,200)
        println(objA.a + " | " + objA.b)
    }

    class A(val a:Int, val b:Int) {


    }
}
