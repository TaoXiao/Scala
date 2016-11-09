package cn.gridx.scala.lang.control_structures.for_loop


/**
  * Created by tao on 7/21/16.
  */
object DoubleLoops extends App {
  val A = Array(0, 1, 2, 3, 4, 5)

  for (i <- 0 until A.size; j <- 0 until i) {
    println(i, j)
  }
}
