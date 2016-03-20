package cn.gridx.spark.sql

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.JdbcRDD
import java.sql.{Connection, DriverManager, ResultSet}

/**
  * Created by tao on 3/19/16.
  */
object MySQL {
  def Database = "slick"
  def Url = s"jdbc:mysql://localhost:3306/$Database"
  def Username = "root"
  def Password = "Root1234NJ"
  def Table    = "Persons"


  def main(args: Array[String]): Unit = {
    UseJdbcRDD
  }

  def UseJdbcRDD(): Unit = {
    Class.forName("com.mysql.jdbc.Driver")

    val sc = new SparkContext(new SparkConf)
    val rdd = new JdbcRDD(sc,
      () => DriverManager.getConnection(Url, Username, Password),
      s"select * from $Table limit ?, ?",
      1, 4, 2,
      rs => rs.getString("NAME") + " -> " + rs.getString("GENDER")
    )

    println(rdd.count)
    sc.stop
  }
}
