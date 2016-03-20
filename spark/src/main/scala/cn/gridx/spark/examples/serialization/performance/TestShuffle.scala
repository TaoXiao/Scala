package cn.gridx.spark.examples.serialization.performance

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by tao on 12/31/15.
 */
object TestShuffle {
    def InputPath  = "hdfs://nameservice1/user/tao/testDir"
    def OutputPath = "hdfs://nameservice1/user/tao/outputPath"

    def main(args: Array[String]): Unit = {
        val conf = new SparkConf()
            .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .set("spark.yarn.executor.memoryOverhead", "2000")
            //.registerKryoClasses(Array(classOf[String]))
        val sc = new SparkContext(conf)

        sc.textFile(InputPath)
            .flatMap(_.toCharArray)
            .map((_, 1))
            .reduceByKey(_ + _)
            .saveAsTextFile(OutputPath)

        sc.stop
    }
}
