package cn.gridx.scala.lang.types.traits

/**
 * Created by tao on 11/20/15.
 */
object Test {
    def main(args: Array[String]): Unit = {
        val bmw = new BMW
        println(bmw.getType)
        println(bmw.getBrand)
        println(bmw.getProduct)
    }
}
