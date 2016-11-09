package test

import cn.gridx.scala.akka.app.crossfilter.OPAQ
import org.testng.annotations.Test

import scala.util.Random

/**
  * Created by tao on 7/28/16.
  */
class TestOPAQ {
  @Test
  def testPartition() {
    val size = 5000000
    val A = new Array[Double](size)
    for (i <- 0 until size)
      A(i) = Random.nextDouble()

    var ts = System.currentTimeMillis()
    val samples = OPAQ.genSamples(100, A, 0, A.size - 1)
    println(s"耗时: ${(System.currentTimeMillis() - ts)/1000f} 秒")



    /*ts = System.currentTimeMillis()
    val B = A.sorted
    println(s"耗时: ${(System.currentTimeMillis() - ts)/1000f} 秒")
    */

    //samples.foreach(println)
  }
}
