package cn.gridx.scala.spark.cassandra.read

import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd.CassandraTableScanRDD
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by tao on 3/18/16.
  *
  * 如果在Spark on YARN上运行这个Job时,会遇到关于Guava版本冲突的问题
  * 需要通过Gradle的shadowJar来解决这个问题
  * 参考 https://www.zybuluo.com/xtccc/note/317832
  *
  * 从Cassandra中读取数据的方法,参考 https://github.com/datastax/spark-cassandra-connector/blob/master/doc/2_loading.md
  */
object BasicRead {
  def CassandraHost = "ecs1"
  def Space = "xt_space"
  def Table = "cars"

  def main(args: Array[String]): Unit = {
    //read0
    rows2tuples
    //readColumns
  }


  /**
    * 最基本的功能测试
    * 测试是否能够从Cassandra来读取数据
    * */
  def read0() = {
    val conf = new SparkConf()
      .set("spark.cassandra.connection.host", "10.163.104.81")
    val sc = new SparkContext(conf)

    //
    val rdd: RDD[CassandraRow]
      = sc.cassandraTable("xt_space", "cars")

    rdd.toArray().foreach(println)

    sc.stop
  }

  /***
    *
    */
  def rows2tuples(): Unit = {
    val conf = new SparkConf(true)
      .set("spark.cassandra.connection.host", CassandraHost)
    val sc = new SparkContext(conf)

    val rdd: CassandraTableScanRDD[(String, String, Float, Float)] =
      sc.cassandraTable[(String, String, Float, Float)](Space, Table)
        .select("brand", "series", "volumn", "price")

    println(s"rdd.cassandraCount = ${rdd.cassandraCount}")
    rdd.collect.foreach(println)
  }


  case class CarBean(brand:String, series:String, volumn:Float, price: Option[Float])


  /**
    * 通过column name或者column index来从table中取出特定的列
    *
    * 如果某个列的数据一定不会为null -->  `row.getFloat`
    * 如果某个列中的数据可能为null   -->  `row.getFloatOption`
    * */
  def readColumns(): Unit = {
    val conf = new SparkConf(true)
          .set("spark.cassandra.connection.host", CassandraHost)
    val sc = new SparkContext(conf)

    val rdd = sc.cassandraTable(Space, Table)
          .filter(row => !row.getString("brand").equals("Benz"))
          .map(row => CarBean(row.getString("brand"), row.getString("series"), row.getFloat("volumn"), row.getFloatOption("price")))
    rdd.collect.foreach(println)

    sc.stop
  }




  /***
    * 读取collection类型的columns
    * collection包括 list  set  map
    */
  def readCollections(): Unit = {

  }



}
