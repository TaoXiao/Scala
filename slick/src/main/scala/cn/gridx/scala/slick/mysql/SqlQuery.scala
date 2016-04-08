package cn.gridx.scala.slick.mysql

import scala.slick.jdbc.GetResult
import scala.slick.session.Database
import scala.slick.session.Database.threadLocalSession
import scala.slick.jdbc.StaticQuery._

/**
  * Created by tao on 4/8/16.
  */
object SqlQuery {
  def db: Database = {
    val url = "jdbc:mysql://gridxmysql.cywgostu0so4.us-west-1.rds.amazonaws.com/pge_prod?user=demo&password=RE3u6pc8ZYx1c&connectTimeout=60000&socketTimeout=60000"
    Database.forURL(url, driver = "com.mysql.jdbc.Driver")
  }



  def main(args: Array[String]): Unit = {
    implicit val getCustomerResult =
      GetResult(r => Customer(r.<<, r.<<, r.<<, r.<<))

    val id = "0000021395"

    val list: List[Customer] = db withSession {
      val q = sql"""select __pk_customerattribute, contract_id,attribute_id, attribute_value from customerattribute where  contract_id = $id"""
      val ret = q.as[Customer]  // 需要一个implicit session  ->  import scala.slick.session.Database.threadLocalSession
      ret.list
    }

    println(list.mkString("[", ",", "]"))
  }
}

case class Customer(__pk_customerattribute: Int,
                    contract_id: String,
                    attribute_id: Int,
                    attribute_value: String)

