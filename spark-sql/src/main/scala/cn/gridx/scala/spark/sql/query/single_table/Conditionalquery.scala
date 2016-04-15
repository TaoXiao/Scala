package cn.gridx.scala.spark.sql.query.single_table

import java.util.Date

import org.joda.time.DateTime

/**
  * Created by tao on 4/12/16.
  */
object Conditionalquery {
  def main(args: Array[String]): Unit = {
    val d1 = new DateTime(1422748800000L)
    val d2 = new DateTime(1422748800000L)

    println(d1)
    println(d2)

    println(d1.equals(d2))
  }
}
