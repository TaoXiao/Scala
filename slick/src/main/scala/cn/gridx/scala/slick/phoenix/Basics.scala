package cn.gridx.scala.slick.phoenix


import scala.slick.driver.MySQLDriver.simple._


/**
  * `Database.threadLocalSession` simplifies the session handling
  * by attaching a session to the current thread so you do not
  * have to pass it around on your own (or at least assign it
  * to an implicit variable).
  * */
import Database.threadLocalSession

/**
  * Created by tao on 3/29/16.
  */
object Basics {
  def ZkNode = "hadoop1.com:2181"
  def Driver = "org.apache.phoenix.jdbc.PhoenixDriver"

  object ProductCategory extends
      Table[(String, String, String, String, String, String, String)] ("FsnipData:fsn.product_category") {
    def pk    = column[String]("PK", O.PrimaryKey)
    def id    = column[String]("id")
    def code  = column[String]("code")
    def name  = column[String]("name")
    def display = column[String]("display")
    def imgUrl  = column[String]("imgUrl")
    def time    = column[String]("RECORD_INSERT_TIME")

    def * = pk ~ id ~ code ~ name ~ display ~ imgUrl ~ time
  }

  def main(args: Array[String]): Unit = {
    getData
  }

  def getData(): Unit = {
    Database
      .forURL(s"jdbc:phoenix:$ZkNode", driver = Driver)
      .withSession {
        Query(ProductCategory) foreach(println)
      }
  }
}
