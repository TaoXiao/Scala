package cn.gridx.scala.spark.sql.schema

import java.text.SimpleDateFormat
import java.util.Date

import org.joda.time.DateTime

/**
  * Created by tao on 4/11/16.
  *
  * The Scala interface for Spark SQL supports automatically converting an RDD containing case classes to a DataFrame.
  * The case class defines the schema of the table. The names of the arguments to the case class are read
  * using reflection and become the names of the columns.
  *
  * Case classes can also be nested or contain complex types such as Sequences or Arrays. This RDD can be implicitly
  * converted to a DataFrame and then be registered as a table. Tables can be used in subsequent SQL statements.
  */
object ByReflection {
  def main(args: Array[String]): Unit = {
    val dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val day = new DateTime(1400025599000L)
    println(day)
    println(new DateTime(day.getMillis))

    /*
    println("second: " + day.secondOfMinute.get)
    println("minute: " + day.minuteOfHour.get)
    println("hour: " + day.hourOfDay.get)
    */
  }


}
