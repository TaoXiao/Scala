package cn.gridx.scala.lang.json


/**
  * Created by tao on 8/30/16.
  */
object TestArray {
  def lnOf(n: Double) = scala.math.log(n)
  def log(n: Double, x: Double) = scala.math.log(x) / lnOf(n)
  def Base = 1.07

  def transform(x: Double): Double = {
    if (x > 0)
     return log(Base, x)
    else {
      return -log(Base, -x)
    }
  }

  def backTransform(x: Double): Double = {
    if (x > 0)
      return scala.math.pow(Base, x)
    else
      return -scala.math.pow(Base, -x)
  }

  def main(args: Array[String]): Unit = {

    val result = Range(1, 101)
    // result.foreach(x => println(x + " "))

    val sampleNum = 30

    val samples: Array[Int] = {
      if (result.size > sampleNum)
        for (i <- Range(0, result.size, result.size / sampleNum)) yield result(i)
      else
        result
    }.toArray

    for (i <- 0 until samples.size)
      println(s"#${i+1}:  ${samples(i)}")
  }
}
