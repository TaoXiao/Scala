package cn.gridx.spark.examples.io

import com.sematext.hbase.wd.RowKeyDistributorByHashPrefix.OneByteSimpleHash
import com.sematext.hbase.wd.{RowKeyDistributorByHashPrefix, WdTableInputFormat}
import org.apache.hadoop.hbase.client.{HTable, Put, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, KeyValue}
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by tao on 7/6/15.
 *
 * 测试混合使用HaseWD及Phoenix（中的salted table）
 */
object TestHBaseWdPhoenix {
    def FamilyF = "F".getBytes
    def TableName = "sentiments:salted"

    def main(args:Array[String]): Unit = {
        // 先往HBase表中写入一些数据
        //write()
        
        // 然后用Spark来读取这些数据
        read()
    }

    /**
     * 在Phoenix中创建一个salted表，命令为:
     *      create table "sentiments:salted" (pk VARCHAR primary key, F.C1 varchar, F.C2 varchar, F.C3 varchar) SALT_BUCKETS=5;
     *
     * 然后在这里用HBaseWD进行对该表的写入（rowkey前加入一个字节）
     *
     * ---------------------------------------------------------------
     * 写入完成后，从HBase中查询该表，发现rowkey前面加上了一个字节的前缀，如下：
     *
     *  hbase(main):029:0> scan 'sentiments:salted'
            ROW                                  COLUMN+CELL
                 \x04key-1                           column=F:C1, timestamp=1436169832864, value=value-11
                 \x04key-1                           column=F:C2, timestamp=1436169832864, value=value-12
                 \x05key-2                           column=F:C1, timestamp=1436169833208, value=value-21
                 \x05key-2                           column=F:C2, timestamp=1436169833208, value=value-22
                 \x06key-3                           column=F:C1, timestamp=1436169833213, value=value-31
                 \x06key-3                           column=F:C2, timestamp=1436169833213, value=value-32
            3 row(s) in 0.1730 seconds


     * ---------------------------------------------------------------
     * 从Phoenix来查的话，则看到的是正常的数据，如下：

            0: jdbc:phoenix:ecs2.njzd.com:2181> select * from "sentiments:salted";
            +------------------------------------------+------------------------------------------+------------------------------------------+-----------+
            |                    PK                    |                    C1                    |                    C2                    |           |
            +------------------------------------------+------------------------------------------+------------------------------------------+-----------+
            | key-1                                    | value-11                                 | value-12                                 |           |
            | key-2                                    | value-21                                 | value-22                                 |           |
            | key-3                                    | value-31                                 | value-32                                 |           |
            +------------------------------------------+------------------------------------------+------------------------------------------+-----------+


     *  实际上，用HBaseWD的话，rowkey前面被加上了一个字节
     *  用Phoneix来创建salted table的话，Phoenix认为rowkey的第一个字节是一个prefix,它并不关心该字节是什么
     *
     *  所以，可以用Phoenix来创建一个salted table，然后再用HBaseWD往里面写数据
     */
    def write(): Unit = {
        val hbaseConf = HBaseConfiguration.create
        val htable = new HTable(hbaseConf, TableName)
        val keyDistributor = new RowKeyDistributorByHashPrefix(new OneByteSimpleHash(30))

        var put = new Put(keyDistributor.getDistributedKey("key-1".getBytes))
        put.add(FamilyF, "C1".getBytes, "value-11".getBytes)
        put.add(FamilyF, "C2".getBytes, "value-12".getBytes)
        htable.put(put)
        htable.flushCommits

        put = new Put(keyDistributor.getDistributedKey("key-2".getBytes))
        put.add(FamilyF, "C1".getBytes, "value-21".getBytes)
        put.add(FamilyF, "C2".getBytes, "value-22".getBytes)
        htable.put(put)
        htable.flushCommits

        put = new Put(keyDistributor.getDistributedKey("key-3".getBytes))
        put.add(FamilyF, "C1".getBytes, "value-31".getBytes)
        put.add(FamilyF, "C2".getBytes, "value-32".getBytes)
        htable.put(put)
        htable.flushCommits

        htable.close

        println("写入结束")
    }

    /**
     * 测试用Spark来读取该表中的内容
     */
    def read(): Unit = {
        val sparkConf = new SparkConf()
        sparkConf.setAppName("Spark read from HBaseWD")
        sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

        val sparkContext = new SparkContext(sparkConf)

        val hbaseConf = HBaseConfiguration.create
        hbaseConf.set(TableInputFormat.INPUT_TABLE, TableName)

        val keyDistributor = new RowKeyDistributorByHashPrefix(new OneByteSimpleHash(30))
        keyDistributor.addInfo(hbaseConf)   // 将`ROW_KEY_DISTRIBUTOR_CLASS`信息写入到`hbaseConf`中

        val rdd = sparkContext.newAPIHadoopRDD(hbaseConf, classOf[WdTableInputFormat],
                        classOf[ImmutableBytesWritable], classOf[Result])
                .map{case (key, result) => (Bytes.toStringBinary(key.get), result) }

        /**
         *
         * 打印出的rowkey是带prefix的（1个字节），输出如下

                key = \x04key-1
                    Family = F  |  Qualifier = C1  |  Value = value-11
                    Family = F  |  Qualifier = C2  |  Value = value-12

                key = \x05key-2
                    Family = F  |  Qualifier = C1  |  Value = value-21
                    Family = F  |  Qualifier = C2  |  Value = value-22

                key = \x06key-3
                    Family = F  |  Qualifier = C1  |  Value = value-31
                    Family = F  |  Qualifier = C2  |  Value = value-32

            因此，如果需要拿到原始的rowkey的话，需要使用`keyDistributor.getOriginalKey()`
         */
        for ((key, result) <- rdd.collect) {
            println(s"key = ${key}")

            val kvs = result.list.listIterator()
            while (kvs.hasNext) {
                val kv: KeyValue = kvs.next
                println(s"\tFamily = ${Bytes.toStringBinary(kv.getFamily)}  |  Qualifier = ${Bytes.toStringBinary(kv.getQualifier)}  |  Value = ${Bytes.toStringBinary(kv.getValue)}")
            }

            println
        }

        sparkContext.stop
    }
}
