package cn.gridx.scala.lang.generics.TypeErasure

import scala.reflect.runtime.universe.{TypeTag, typeOf}

/**
 * Created by tao on 11/24/15.
 */
object ScalaExamples {
    def main(args: Array[String]): Unit = {
        //meth2(List(1,2,3))


        /**
         * 操作符`=:=`用于比较两个type是否相同
         * 操作符`<:<`用于比较两个type的继承关系
         */
        // println(typeOf[java.lang.String] =:= typeOf[Predef.String])
        // println(typeOf[List[java.lang.String]] <:< typeOf[List[Predef.String]])


        // testClassTag

        func(f)
    }


    /**
     * Warning: non-variable type argument String in type pattern List[String] is unchecked since it is eliminated by erasure
            case _: List[String] => println("It is List[String]")
                    ^
     Warning: non-variable type argument Int in type pattern List[Int] is unchecked since it is eliminated by erasure
            case _: List[Int] => println("It is List[Int]")
                    ^
     */
    def meth1[T](xs: List[T]): Unit = {
        xs match { //  无法分别正确的类型，因为类型擦除
            case _: List[String] => println("It is List[String]")
            case _: List[Int] => println("It is List[Int]")
        }
    }


    def meth2[T: TypeTag](xs: List[T]) = {
        typeOf[T] match {
            case t if t =:= typeOf[String] => println("It is List[String]")
            case t if t =:= typeOf[Int] => println("It is List[Int]")
        }
    }


    def testClassTag() {
        def createGenericArray[T: scala.reflect.ClassTag](seq: T*): Array[T] = Array[T](seq: _*)

        val A = createGenericArray(1,2,3,4)
        val B = createGenericArray("X", "Y", "Z")
        println(A.getClass + " -> " +  A.mkString("[", ",", "]"))
        println(B.getClass + " -> " +  B.mkString("[", ",", "]"))

        println("------------------------------------------------------")
    }


    /**
     * 这里 f :=> Boolean 是什么意思？
     * @param x
     */
    def func(x :  => Boolean): Unit = {
    }

    def f(): Boolean = {
        println("Hello")
        true
    }

}
