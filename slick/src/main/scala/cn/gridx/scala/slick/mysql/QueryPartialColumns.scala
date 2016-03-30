package cn.gridx.scala.slick.mysql

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
object QueryPartialColumns {
  def HOST   = "ecs5:3306"
  def DB     = "slick"
  def TABLE  = "Persons"
  def Driver = "com.mysql.jdbc.Driver"
  def User   = "root"
  def Passwd = "njzd2014"

  /**
    *  只想取出表中部分的columns,
    *  实际上, MySql的"Persons"表中共有4个columns,但这里我们只定义2个columns, 这样便只取出2个columns
    *
    *  相当于:  select id, name from Persons
    */
  object t_persons extends Table[(Int, String)](TABLE) {
    def id    = column[Int]("ID", O.PrimaryKey)
    def name  = column[String]("NAME")

    def * = id ~ name
  }

  def main(args: Array[String]): Unit = {
    getData
  }


  def getData(): Unit = {
    Database
      .forURL(s"jdbc:mysql://$HOST/$DB", driver = Driver, user = User, password = Passwd)
      .withSession {
        Query(t_persons) foreach {
          case (id, name) => println(s"id = $id, name = $name")
        }
      }
  }
}
