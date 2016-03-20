package cn.gridx.spark.examples.io

import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, KeyValue}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by tao on 7/6/15.
 */
object ReadHBase {
    def main(args: Array[String]): Unit = {
        Logger.getRootLogger.setLevel(Level.WARN)


    }

    /**
     * 从一个普通的表中读取数据(即rowkey没有经过任何处理)
     * @param tableName
     */
    def readPlainTable(tableName: String): Unit = {
        val sparkConf = new SparkConf()
        sparkConf.setAppName("Read from HBase")
        // 为类`ImmutableBytesWritable`设置Kryo的序列化机制
        sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        sparkConf.set("spark.kryo.registrator", "cn.gridx.spark.examples.CustomKryoRegistrator")

        val sparkContext = new SparkContext(sparkConf)

        val hbaseConf = HBaseConfiguration.create
        hbaseConf.set(TableInputFormat.INPUT_TABLE, tableName)  // 设置要读取的HBase表名

        /**
         * 从表中读取全部的数据
         *
         * 陷阱！！！
         * 这里需要对取出的每个`ImmutableBytesWritable`进行复制，否则它们里面的内容都是一样的，且是最后一个key的内容
         */
        val rdd: RDD[(ImmutableBytesWritable, Result)] = sparkContext.newAPIHadoopRDD(
            hbaseConf,  classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])
                .map{case (key, result) => (new ImmutableBytesWritable(key), result) }


        /**
         * 输出如下：（确实是原表中的全部数据）
         *
         *  key = row-1
	            Family = F  |  Qualifier = C1  |  Value = val-1
                Family = F  |  Qualifier = C3  |  Value = val-5

            key = row-2
                Family = F  |  Qualifier = C1  |  Value = val-2

            key = row-3
                Family = F  |  Qualifier = C2  |  Value = val-3

            key = row-4
                Family = F  |  Qualifier = C2  |  Value = val-4
         */
        for ((key, result) <- rdd.collect) {
            println(s"key = ${Bytes.toStringBinary(key.get)}")

            val kvs = result.list.listIterator()
            while (kvs.hasNext) {
                val kv: KeyValue = kvs.next
                println(s"\tFamily = ${Bytes.toStringBinary(kv.getFamily)}  |  Qualifier = ${Bytes.toStringBinary(kv.getQualifier)}  |  Value = ${Bytes.toStringBinary(kv.getValue)}")
            }

            println
        }

        sparkContext.stop
    }


    def readSaltedTable(): Unit = {

    }

}
