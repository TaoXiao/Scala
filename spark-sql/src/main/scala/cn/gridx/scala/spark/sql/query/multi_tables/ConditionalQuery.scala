package cn.gridx.scala.spark.sql.query.multi_tables

import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by tao on 4/12/16.
  */
object ConditionalQuery {
  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf)
    val sqlCtx = new SQLContext(sc)

    val persons  = LoadPersons(sc, sqlCtx)
    val students = LoadStudents(sc, sqlCtx)

    persons.registerTempTable("persons")
    students.registerTempTable("students")

    val result: DataFrame = sqlCtx.sql("select * from persons p, students s where p.id = s.id")
    result.persist
    println(s"result.count -> ${result.count}")
    result.collect.map(println)

    sc.stop
  }

  def LoadPersons(sc: SparkContext, sqlCtx: SQLContext): DataFrame = {
    import sqlCtx.implicits._

    val persons = sc.parallelize(
      Array(Person(1000, "Tom", "male", 44), Person(2000, "Jack", "male", 45),
        Person(3000, "Jim", "male", 46), Person(4000, "Rose", "female", 23)), 4)

    persons.toDF
  }

  def LoadStudents(sc: SparkContext, sqlCtx: SQLContext): DataFrame = {
    import sqlCtx.implicits._

    val students = sc.parallelize(
      Array(Student(1000, "Tom", "New York"), Student(2000, "Jack", "New Jersey"),
        Student(3000, "Kate", "San Jose"), Student(4000, "Bruce", "California")), 6)

    students.toDF
  }
}


case class Person(id: Int, name: String, gender: String, age: Int)

case class Student(id: Int, name: String, home: String)
