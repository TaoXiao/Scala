package cn.gridx.spark.examples.basic

import org.apache.spark.{SparkContext, SparkConf}

import scala.math.random
/**
 * Created by tao on 1/13/16.
 */
object Pi extends App {
    val sc = new SparkContext(new SparkConf)
    val N = 1000

    val M = sc.parallelize(Range(0, N), 6)
        .map(i => {
           val (x, y) = (random, random)
           if (x*x + y*y < 1) 1
           else 0 })
        .filter(_ == 1)
        .count

    println(s"\nPi = ${4.0*M/N}\n")

    sc.stop
}
