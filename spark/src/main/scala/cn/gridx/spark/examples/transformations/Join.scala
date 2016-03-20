package cn.gridx.spark.examples.transformations

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext._

/**
 * Created by tao on 8/12/15.
 */
object Join {
    def main(args: Array[String]): Unit = {
        Logger.getRootLogger.setLevel(Level.ERROR)
        val sc = new SparkContext(new SparkConf())
        val rdd1 = sc.parallelize(Array(
                            ("A", "a1"), ("A", "a2"),
                            ("B", "b1"), ("B", "b2"),
                            ("C", "c1"),
                            ("E", "e1"),
                            ("F", "f1")), 6)

        val rdd2 = sc.parallelize(Array(
            ("A", "ax1"), ("A", "ax2"),
            ("B", "bx1"),
            ("F", "fx1"),
            ("G", "gx1"),
            ("H", "hx1")), 3)

        /*
            输出：
                (B,(b1,bx1))
                (B,(b2,bx1))
                (F,(f1,fx1))
                (A,(a2,ax1))
                (A,(a2,ax2))
                (A,(a1,ax1))
                (A,(a1,ax2))
         */
        InnerJoin(rdd1, rdd2)


        /*
            输出：
                (B,(Some(b1),bx1))
                (B,(Some(b2),bx1))
                (H,(None,hx1))
                (F,(Some(f1),fx1))
                (G,(None,gx1))
                (A,(Some(a2),ax1))
                (A,(Some(a2),ax2))
                (A,(Some(a1),ax1))
                (A,(Some(a1),ax2))
         */
        RightOuterJoin(rdd1, rdd2)


        /*
            输出：
                (B,(b2,Some(bx1)))
                (B,(b1,Some(bx1)))
                (C,(c1,None))
                (E,(e1,None))
                (F,(f1,Some(fx1)))
                (A,(a2,Some(ax1)))
                (A,(a2,Some(ax2)))
                (A,(a1,Some(ax1)))
                (A,(a1,Some(ax2)))
         */
        LeftOuterJoin(rdd1, rdd2)

        sc.stop()
    }

    def InnerJoin(rdd1: RDD[(String, String)], rdd2: RDD[(String, String)]): Unit = {
        val result: RDD[(String, (String, String))] = rdd1.join(rdd2)

        println("Inner Join Results : ")
        result.collect.foreach(println)
        println("---------------------------")
    }

    def RightOuterJoin(rdd1: RDD[(String, String)], rdd2: RDD[(String, String)]): Unit = {
        val result: RDD[(String, (Option[String], String))] = rdd1.rightOuterJoin(rdd2)

        println("Right Outer Join  Results : ")
        result.collect.foreach(println)
        println("---------------------------")
    }

    def LeftOuterJoin(rdd1: RDD[(String, String)], rdd2: RDD[(String, String)]): Unit = {
        val result: RDD[(String, (String, Option[String]))] = rdd1.leftOuterJoin(rdd2)

        println("Left Outer Join  Results : ")
        result.collect.foreach(println)
        println("---------------------------")
    }
}
