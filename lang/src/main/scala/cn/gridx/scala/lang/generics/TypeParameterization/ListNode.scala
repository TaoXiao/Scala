package cn.gridx.scala.lang.generics.TypeParameterization

/**
 * Created by tao on 11/23/15.
 */
case class ListNode[+T](h:T, t:ListNode[T]) {

}

object ListNode {
    def main(args:Array[String]): Unit = {

        println("yeah")
    }
}
