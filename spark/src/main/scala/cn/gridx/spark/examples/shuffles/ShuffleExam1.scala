package cn.gridx.spark.examples.shuffles

import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

/**
 * Created by tao on 12/30/15.
 */
object ShuffleExam1 extends App {
    val sc = new SparkContext(new SparkConf())

    val tokens = sc.textFile("hdfs://nameservice1/user/tao/corpus").flatMap(_.split(" "))

    val wordCounts = tokens.map((_, 1))
            .partitionBy(new HashPartitioner(20)).reduceByKey(_ + _)

    wordCounts.partitions.size
    println(s"\n\n${wordCounts.partitions.size}\n\n")

    val filtered = wordCounts.filter(_._2 >= 10)
    val charCounts = filtered.flatMap(_._1.toCharArray).map((_, 1)).reduceByKey(_ + _)

    println(s"\n\n${charCounts.count}\n\n")

}
