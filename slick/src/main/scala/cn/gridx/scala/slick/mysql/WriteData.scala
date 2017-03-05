package cn.gridx.scala.slick.mysql

import scala.collection.mutable
import scala.slick.session.Database
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.session.Database.threadLocalSession

/**
  * Created by tao on 2/20/17.
  */
object WriteData {
  def db: Database = {
    val host = "10.100.34.151"
    val database = "pge"
    val user = "demo"
    val password = "RE3u6pc8ZYx1c"
    val url = s"jdbc:mysql://$host/$database?user=$user&password=$password&connectTimeout=60000&socketTimeout=60000"
    Database.forURL(url, driver = "org.mariadb.jdbc.Driver")
  }


  def main(args: Array[String]): Unit = {
    val result = f()
    println(result)
  }


  def f(): Int = {
    val map = mutable.HashMap[String, Database]()
    map.put("A", db)

    var count = -1
    map.get("A").get withSession {
      val statement = sql" select count(1) from BatchProgress where batch_status = 'finish' "
      count = statement.as[Int].first()
      // 不能在这里直接return count
    }

    count
  }
}



