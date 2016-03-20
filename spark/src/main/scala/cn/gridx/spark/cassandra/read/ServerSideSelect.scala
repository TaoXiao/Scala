package cn.gridx.spark.cassandra.read

import com.datastax.spark.connector.rdd.CassandraTableScanRDD
import org.apache.spark.{SparkContext, SparkConf}
import com.datastax.spark.connector._

/**
  * Created by tao on 3/19/16.
  *
  * 参考 https://github.com/datastax/spark-cassandra-connector/blob/master/doc/3_selection.md
  */
object ServerSideSelect {
  def CassandraHost = "ecs1"
  def Space = "xt_space"
  def Table = "cars"

  def main(args: Array[String]): Unit = {
    Select
  }

  /**
    * 把查询条件(where clause)直接传递到Cassandra,并在server端执行条件查询
    * */
  def Select(): Unit = {
    val conf = new SparkConf(true)
          .set("spark.cassandra.connection.host", CassandraHost)
    val sc = new SparkContext(conf)

    println(""" Query Clause : ("brand = ?", "Audi") """)
    val rdd = sc.cassandraTable(Space, Table)
          .select("brand" as "col_brand", "series" as "col_series", "volumn" as "col_volumn")  // 选取感兴趣的几个columns
          .where("brand = ?", "Audi") // 条件查询

    println(s"rdd.cassandraCount = ${rdd.cassandraCount}") // 计数
    rdd.collect.foreach(println)

    println(""" Query Clause : where("brand = ? and volumn = ?", "Audi", 34)""")
    val rdd2 = sc.cassandraTable(Space, Table)
            .select("brand", "series", "volumn")
            .where("brand = ? and volumn = ?", "Audi", 34)
    println(s"rdd2.cassandraCount = ${rdd2.cassandraCount}")
    rdd2.collect.foreach(println)

    sc.stop
  }


  // A single Cassandra partition never spans multiple Spark partitions, it is possible to very efficiently group
  // data by partition key without shuffling data around
}
