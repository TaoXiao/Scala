package cn.gridx.scala.lang.collections

import scala.collection.mutable.ListBuffer

/**
 * Created by tao on 9/7/15.
 */
object Operations {
    def main(args: Array[String]): Unit = {

        // Slice()
        /*
        val A = new Array[String](6)
        A(0) = "a"
        A(1) = "b"
        A(2) = "c"
        A(3) = "d"
        A(4) = "e"
        A(5) = "f"

        for (i <- 0 until A.length if i%2==0) {
            println(A(i))
            println(A(i+1))
        }*/
        Foreach

    }

    def Zip(): Unit = {
        val a = ListBuffer[Int]()
        val b = ListBuffer[Int]()

        for (i <- 0 until 10) {
            a.append(i)
            b.append(i*1000)
        }

        a zip b.filter(_ <= 6000) foreach println
    }


    def Slice(): Unit = {
        val A = new Array[String](4)
        A(0) = "a"
        A(1) = "b"
        A(2) = "c"
        A(3) = "d"

        val B = A.slice(1, A.length)
        B.foreach(println)
    }


    def Foreach(): Unit = {
        var A = Array(1,2,3,4,5)
        A = A.map(_*100)
        A.foreach(println)
    }
}
