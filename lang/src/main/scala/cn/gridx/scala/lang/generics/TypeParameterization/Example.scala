package cn.gridx.scala.lang.generics.TypeParameterization

/**
 * Created by tao on 11/23/15.
 */
object Example {
    def main(args: Array[String]): Unit = {
        val orange = new Box[Orange](new Orange)
        val apple  = new Box[Apple](new Apple)
        val box: Box[Fruit] = new Box[Apple](new Apple)
    }
}
