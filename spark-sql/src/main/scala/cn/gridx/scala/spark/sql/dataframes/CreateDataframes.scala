package cn.gridx.scala.spark.sql

import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by tao on 4/10/16.
  */
object CreateDataframes {

  def main(args: Array[String]): Unit = {
    // Read_MySQL
    CaseClass_to_Dataframes
  }

  /**
    * 读取JSON文件
    * */
  def Json_Dataframe(): Unit = {
    val sc = new SparkContext(new SparkConf())
    val sqlCtx = new SQLContext(sc)


    /**
    "root" : {
      "name" : "ccc",
      "age"  : 26
    }
    */
    val file = "/test/person.json"
    val df: DataFrame = sqlCtx.read.json(file)
    df.show()

    sc.stop
  }


  /**
    * 从MySql中读取数据
    *
    * */
  def Read_MySQL(): Unit = {
    val map = Map( "url" -> "jdbc:mysql://ecs5/slick",
      "user" -> "root",
      "password" -> "njzd2014",
      "driver"  -> "com.mysql.jdbc.Driver",
      "dbtable" -> "Persons")

    val sqlCtx = new SQLContext(new SparkContext(new SparkConf()))

    val df: DataFrame = sqlCtx.read.format("jdbc").options(map).load

    // 输出所有的数据
    df.show
    println("*"*30)

    // 输出schema
    df.printSchema()
    println("*"*30)

    // 选择其中的两列
    df.select("NAME", "GENDER", "AGE").show
    println("*"*30)

    // 对column value可以进行计算
    df.select(df("Name"), df("AGE") * 100).show()
    println("*"*30)

    // 对column value进行条件查询
    df.filter(df("AGE") >= 20 && df("AGE") <= 30).show()
    println("*"*30)

    // group by 某一列
    df.groupBy("AGE").count.show()

  }

  /**
    * CASE CLASS  -->  Dataframe
    * */
  def CaseClass_to_Dataframes(): Unit = {
    val sc = new SparkContext(new SparkConf)
    val sqlCtx = new SQLContext(sc)

    val rdd = sc.parallelize(
      Array(Person(100, "Tom", "male", 44), Person(200, "Jack", "male", 45),
            Person(300, "Jim", "male", 46), Person(400, "Rose", "female", 23)), 4)

    // implicitly convert an RDD to a dataframe
    import sqlCtx.implicits._

    val df: DataFrame = rdd.toDF
    df.registerTempTable("persons")

    println("\nDisplay all men")
    val men: DataFrame = sqlCtx.sql("select * from persons where gender = 'male' ")
    men.collect.foreach(println)

    // 通过 field index 来取出columns
    println("\nDisplay men's names by column index")
    men.map(t => s"Name : ${t(1)}").collect.foreach(println)


    // 通过 field name 来取出columns
    println("\nDisplay men's age by column names")
    men.map(t => s"Name : ${t.getAs[Int]("age")}").collect.foreach(println)

  //  println("")
  //  mem.map(t => t.getValuesMap[Any](List("")))

    sc.stop

  }

}


case class Person(id: Int, name: String, gender: String, age: Int)
