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
      GetResult(r => Customer(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))

    val id = "6199444205"

    val list = db withSession {
      val q = sql"""select contract_id,attribute_id,attribute_value, UNIX_TIMESTAMP(start_date) start,CASE WHEN end_date IS NOT NULL THEN UNIX_TIMESTAMP(end_date) ELSE 0 END AS end from customerattribute where active_flag =1 and contract_id = $id"""
      val ret = q.as[Customer]  // 需要一个implicit session  ->  import scala.slick.session.Database.threadLocalSession
      ret.list
    }

    println(list)
  }
}

case class Customer(cusotmerPk: Int,
                    uniqueId: String,
                    contractId: String,
                    meteraccountId: String,
                    servicePointId: String,
                    entityAId: String,
                    entityBId: String,
                    contractRoot: Int,
                    billingCycleGroupId: String,
                    timezone: String,
                    startDate: Long,
                    endDate: Long)

