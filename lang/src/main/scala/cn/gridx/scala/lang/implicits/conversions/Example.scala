package cn.gridx.scala.lang.implicits.conversions

// implicit conversion `RMB2Dollar`本package内
// 因此需要 import into this scope as a single identifier

import other_conversions.OtherConversion.RMB2Dollar


/**
 * Created by tao on 11/24/15.
 */
object Example {
    def main(args: Array[String]): Unit = {
        /**
         * Converting to an expected type
         */
        val d1 = new Dollar(2)
        val d2 = new Dollar(20)
        val r1  = new RMB(80)
        val r2  = new RMB(800)
        val euro= new Euro(15)

        println(r1.addDollar(r2))
        println(d1.addRMB(d2))
        println(euro.addEuro(r1))

        printDollar(2.0)
        //printRMB(4.0)   // 无法通过编译，应用`double2Dollar`时，不能再应用其他implicit conversion


        /**
          * Converting the receiver
          */
        val jpy = new JPY(1000)
        println(1000.0 + jpy)


        /**
         * Simulating new syntax
         *
         * Map(1 -> "One", 2 -> "Two")中的符号 `->`是怎么来的？
         * 它实际上是class `ArrowAssoc`中的一个方法
         * 并且scala.Predef中定义了一个从`Any`到`ArrowAssco`的implicit conversion
         * 当遇到1时，1会从`Any`转换为`ArrowAssco`，进而可以使用操作符 `->`
         */
        val map: Map[Int, String] = Map(1 -> "One", 2 -->> "Two", 3 -->> "Three")
        println(scala.reflect.runtime.universe.typeTag[Map[Int, String]])
        map.foreach(println)

        // bring other_conversions.TaoPrompts.prompt1 into scope
        import other_conversions.TaoPrefences._
        Greeter.greet("Tao") // 隐式地使用提示符$
        Greeter.greet("Tao")(new Prompt(">"), new Drink("Sprit"))  // 显式地使用提示符>



        println(maxListElem2(List(1,2,3,2,1,4,5,0)))

    }

    def printDollar(dollar: Dollar) = println(dollar)
    def printRMB(rmb: RMB) = println(rmb)

    implicit def double2Dollar(v: Double): Dollar = new Dollar(v)

    final class MyArrowAssco[A](val  _leftValue:A) extends AnyVal {
        def -->> [B](y:B): Tuple2[A, B] = Tuple2(_leftValue, y)
    }

    implicit def any2MyArrowAssco[A](x: A): MyArrowAssco[A] = new MyArrowAssco(x)


    def maxListElem[T <: Ordered[T]](list: List[T]): T =
        list match {
            case List()   => throw new IllegalArgumentException("Empty list")
            case List(x)  => x
            case x::rest  =>
                val maxRest = maxListElem(rest)
                if (maxRest > x) maxRest
                else x
        }


    /**
     * 将T转化为Ordered[T]之后再进行比较
     *
     * @param list
     * @param orderer
     * @tparam T
     * @return
     */
    def maxListElem2[T](list: List[T])(implicit orderer: T => Ordered[T]): T =
        list match {
            case List()  =>  throw new IllegalArgumentException("Empty list")
            case List(x) => x
            case x::rest =>
                val maxRest = maxListElem2(rest)(orderer)
                if (orderer(x) > maxRest) x     // orderer(x)将x由T转化为类型Ordered[T]
                else maxRest
        }


    /**
     * 使用Ordering[T]类的`gt`方法来进行比较
     * @param list
     * @param ordering
     * @tparam T
     * @return
     */
    def maxListElem3[T](list: List[T])(implicit ordering: Ordering[T]): T =
        list match {
            case List()  =>  throw new IllegalArgumentException("Empty list")
            case List(x) => x
            case x::rest =>
                val maxRest = maxListElem3(rest)(ordering)
                if (ordering.gt(x, maxRest)) x     // 用`ordering.gt`比较x与maxRest
                else maxRest
        }
}



