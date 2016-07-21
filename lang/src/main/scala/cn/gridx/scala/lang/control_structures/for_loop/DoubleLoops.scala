package cn.gridx.scala.lang.control_structures.for_loop
import scala.collection.immutable.IndexedSeq

/**
  * Created by tao on 7/21/16.
  */
object DoubleLoops extends App {
  val A = Array(1, 2, 3, 4, 5)
  val B = Array("A", "B", "C", "D", "E")

  val pairs: IndexedSeq[(Int, Int)] =
    for (i <- 0 until A.size; j <- 0 until A.size if i != j) yield
      A(i) -> A(j)

  pairs.foreach(println)
}
