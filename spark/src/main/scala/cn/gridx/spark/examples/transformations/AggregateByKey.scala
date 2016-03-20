package cn.gridx.spark.examples.transformations

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.{Partitioner, SparkConf, SparkContext}

import scala.collection.mutable.ListBuffer

/**
 * Created by tao on 7/10/15.
 */
object AggregateByKey {
    def main(args: Array[String]): Unit = {


        Logger.getRootLogger.setLevel(Level.WARN)

        /* test_basic

        println("------------")


        test_PartitionNumber_1

        println("------------")


        test_PartitionNumber_2 */

        test_CustomPartitioner
    }
    /**
     * 测试`调用aggregateByKey`前后RDD的partition的变化
     * 本例不带`partitioner`参数
     *
     * 测试后发现：在执行`aggregateByKey[U: ClassTag](zeroValue: U)(seqOp: (U, V) => U,
            combOp: (U, U) => U)`前后，rdd的partition并没有发生变化

        解释：1) 调用的`aggregateByKey`是不带`partitioner`参数的
             2) aggregateByKey按照key聚合，并不会改变key，因此可以保持partition
     */
    def test_basic(): Unit = {
        val conf = new SparkConf().setAppName("Test-AggregateByKey")
        val sc = new SparkContext(conf)

        val rdd1: RDD[(String, Int)] = sc.parallelize(1 to 10, 5)
                .flatMap(x => for (i <- 0 to 20) yield (s"key-${x}", i))

        println("rdd1共有 " + rdd1.count + "个元素")
        println("现在的partition的数量为 " + rdd1.partitions.length)

        val rdd2 = rdd1.aggregateByKey(ListBuffer[Int]())(
            (xs, x) => {xs.append(x); xs}, (xs, ys) => {xs.appendAll(ys); xs} )

        println("rdd2共有 " + rdd2.count + "个元素")
        println("现在rdd2的partition的数量为 " + rdd2.partitions.length)


        sc.stop
    }


    /**
     * 测试`调用aggregateByKey`前后RDD的partition的变化
     * 本例带`numPartitions`参数
     *
     * 测试后发现：在执行`aggregateByKey[U: ClassTag](zeroValue: U, numPartitions: Int)(seqOp: (U, V) => U,
            combOp: (U, U) => U)`前后，rdd的partitions数量从5变为了10
     */
    def test_PartitionNumber_1(): Unit = {
        val conf = new SparkConf().setAppName("Test-AggregateByKey")
        val sc = new SparkContext(conf)

        val rdd1: RDD[(String, Int)] = sc.parallelize(1 to 10, 5)
                .flatMap(x => for (i <- 0 to 20) yield (s"key-${x}", i))

        println("rdd1有 " + rdd1.count + "个元素")
        println("现在rdd2的partition的数量为 " + rdd1.partitions.length)

        // 加入参数`numPartitions`，3 < 5
        val rdd2 = rdd1.aggregateByKey(ListBuffer[Int](), 10)(
            (xs, x) => {xs.append(x); xs}, (xs, ys) => {xs.appendAll(ys); xs} )

        println("rdd2 " + rdd2.count + "个元素")
        println("现在rdd2的partition的数量为 " + rdd2.partitions.length)

        sc.stop
    }


    /**
     * 测试`调用aggregateByKey`前后RDD的partition的变化
     * 本例带`numPartitions`参数
     *
     * 测试后发现，即使传入一个较小的值，rdd的partition数量从5变为了3
     *
     * */
    def test_PartitionNumber_2(): Unit = {
        val conf = new SparkConf().setAppName("Test-AggregateByKey")
        val sc = new SparkContext(conf)

        val rdd1: RDD[(String, Int)] = sc.parallelize(1 to 10, 5)
                .flatMap(x => for (i <- 0 to 20) yield (s"key-${x}", i))

        println("rdd1有 " + rdd1.count + "个元素")
        println("现在rdd2的partition的数量为 " + rdd1.partitions.length)

        // 加入参数`numPartitions`，3 < 5
        val rdd2 = rdd1.aggregateByKey(ListBuffer[Int](), 3)(
            (xs, x) => {xs.append(x); xs}, (xs, ys) => {xs.appendAll(ys); xs} )

        println("rdd2 " + rdd2.count + "个元素")
        println("现在rdd2的partition的数量为 " + rdd2.partitions.length)

        sc.stop
    }


    def test_CustomPartitioner(): Unit = {
        val conf = new SparkConf().setAppName("Test-CustomPartitioner")
        val sc = new SparkContext(conf)

        val rdd1 = sc.parallelize(1 to 32, 4)
                .map(_%4)
                .map(x => (s"key-$x", s"value-$x"))
                .mapPartitionsWithIndex((index, it) => {
                    val list = for (p <- it) yield
                        (p, s"par-${index}")
                    list.toIterator
        }, true)

        println("--------- RDD中元素原始的partition分布情况 -------------------")
        // 输出为
        /*
            ((key-1,value-1),par-0)
            ((key-2,value-2),par-0)
            ((key-3,value-3),par-0)
            ((key-0,value-0),par-0)
            ((key-1,value-1),par-0)
            ((key-2,value-2),par-0)
            ((key-3,value-3),par-0)
            ((key-0,value-0),par-0)
            ((key-1,value-1),par-1)
            ((key-2,value-2),par-1)
            ((key-3,value-3),par-1)
            ((key-0,value-0),par-1)
            ((key-1,value-1),par-1)
            ((key-2,value-2),par-1)
            ((key-3,value-3),par-1)
            ((key-0,value-0),par-1)
            ((key-1,value-1),par-2)
            ((key-2,value-2),par-2)
            ((key-3,value-3),par-2)
            ((key-0,value-0),par-2)
            ((key-1,value-1),par-2)
            ((key-2,value-2),par-2)
            ((key-3,value-3),par-2)
            ((key-0,value-0),par-2)
            ((key-1,value-1),par-3)
            ((key-2,value-2),par-3)
            ((key-3,value-3),par-3)
            ((key-0,value-0),par-3)
            ((key-1,value-1),par-3)
            ((key-2,value-2),par-3)
            ((key-3,value-3),par-3)
            ((key-0,value-0),par-3)
         */
        rdd1.collect.foreach(println)



        println("----------  调用aggregateByKey （不提供partition number，也不提供partitioner））  ------------------")
        val rdd2 = sc.parallelize(1 to 32, 4)
                .map(_%4)
                .map(x => (s"key-$x", s"value-$x"))
                .aggregateByKey(ListBuffer[String]())(
                    (xs, x) => {xs.append(x); xs}, (xs, ys) => {xs.appendAll(ys); xs})
                .mapPartitionsWithIndex((index, it) => {
                        val list = for (x <- it) yield (x, s"new-par-$index")
                        list.toIterator }
                )

        // 输出为
        /*
            ((key-2,ListBuffer(value-2, value-2, value-2, value-2, value-2, value-2, value-2, value-2)),new-par-0)
            ((key-3,ListBuffer(value-3, value-3, value-3, value-3, value-3, value-3, value-3, value-3)),new-par-1)
            ((key-0,ListBuffer(value-0, value-0, value-0, value-0, value-0, value-0, value-0, value-0)),new-par-2)
            ((key-1,ListBuffer(value-1, value-1, value-1, value-1, value-1, value-1, value-1, value-1)),new-par-3)
         */
        rdd2.collect.foreach(println)


        println("----------  调用aggregateByKey （提供partitioner））  ------------------")
        val rdd3 = sc.parallelize(1 to 32, 4)
                .map(_%4)
                .map(x => (s"key-$x", s"value-$x"))
                .aggregateByKey(ListBuffer[String](), new  CustomPartitioner(10))(
                    (xs, x) => {xs.append(x); xs}, (xs, ys) => {xs.appendAll(ys); xs})
                .mapPartitionsWithIndex((index, it) => {
                    val list = for (x <- it) yield (x, s"new-par-$index")
                    list.toIterator }
                 )

        // 输出为
        /*
            ((key-0,ListBuffer(value-0, value-0, value-0, value-0, value-0, value-0, value-0, value-0)),new-par-1)
            ((key-1,ListBuffer(value-1, value-1, value-1, value-1, value-1, value-1, value-1, value-1)),new-par-3)
            ((key-2,ListBuffer(value-2, value-2, value-2, value-2, value-2, value-2, value-2, value-2)),new-par-5)
            ((key-3,ListBuffer(value-3, value-3, value-3, value-3, value-3, value-3, value-3, value-3)),new-par-7)

            可见，向aggregateByKey提供了`custom partitioner`之后，不仅可以改变RDD的partition的数量
                还能控制RDD中各元素分布在哪个partition中
         */
        rdd3.collect.foreach(println)

    }
}

/**
 * 这类指定9个以上的partition
 * @param partitionNumber
 */
class CustomPartitioner(val partitionNumber: Int) extends Partitioner {
    def numPartitions: Int = partitionNumber

    def getPartition(key: Any): Int = {
        val k = key.asInstanceOf[String].split("key-")(1).toInt
        k match {
            case 0 => 1
            case 1 => 3
            case 2 => 5
            case 3 => 7
            case _ => 9
        }
    }
}
