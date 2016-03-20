package cn.gridx.spark.examples.basic

import org.apache.spark.storage.ShuffleBlockId
import org.apache.spark.{SparkEnv, Partitioner, SparkContext, SparkConf}

/**
 * Created by tao on 12/25/15.
 */
object TestJars {
    def main(args: Array[String]): Unit = {
        val path = "/user/hdfs/300"

        val conf = new SparkConf()
            .setAppName("Test Spark 1.5.0-cdh5.5.1")
        val sc = new SparkContext(conf)

        println("\n\n")
        println(s"spark.executor.cores = ${conf.getInt("spark.executor.cores", -1)}")
        println(s"spark.task.cpus = ${conf.getInt("spark.task.cpus", -1)}")
        println("\n\n")

        val rdd = sc.parallelize(Range(0, 1000), 20)
            .flatMap(i => for (j <- Range(0, 10000000)) yield (j, i))
            .map(x => (x._1+100, x._2-100))
            .saveAsTextFile(path)

        println(s"\n\n\n结束\n\n\n")

        // org.apache.hadoop.yarn.client
        //org.apache.spark.ExecutorAllocationManager
        SparkEnv.get.blockManager

        sc.stop
    }

}
