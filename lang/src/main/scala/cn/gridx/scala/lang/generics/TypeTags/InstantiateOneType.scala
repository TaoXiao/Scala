package cn.gridx.scala.lang.generics.TypeTags



/**
 * Created by tao on 11/25/15.
 */
object InstantiateOneType {
    def main(args: Array[String]): Unit = {
        val mirror: scala.reflect.runtime.universe.Mirror =
                scala.reflect.runtime.universe.runtimeMirror(getClass.getClassLoader)

        val classBMW:  scala.reflect.runtime.universe.ClassSymbol =
                scala.reflect.runtime.universe.typeOf[BMW].typeSymbol.asClass

        val classBMWMirror: scala.reflect.runtime.universe.ClassMirror =
                mirror.reflectClass(classBMW)

    }

}


class BMW(val series: String, val volumn: String, hybrid: String) {

}

case class BENZ(val classes: String, val volumn: String) {

}

