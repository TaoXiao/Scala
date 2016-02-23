package cn.gridx.scala.lang.types

import java.nio.ByteBuffer

/**
 * Created by tao on 6/29/15.
 */
object Conversions {


    def main(args: Array[String]): Unit = {
       println(withDefault(Some(100)))
    }

    def bytes2Int(): Unit = {
        val a = 1002
        val bytes = ByteBuffer.allocate(4).putInt(a).array()
        println()
        println(ByteBuffer.wrap(bytes).getInt())




       // val bb = ByteBuffer.wrap()
    }

    val withDefault: Option[Int] => Int = {
        case Some(x) => x
        case None => 0
    }

    val n: (Int) => Int = {
       _ + 1
    }



 }
