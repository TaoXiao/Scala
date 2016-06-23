package cn.gridx.scala.lang.database

import java.sql.DriverManager

/**
  * Created by tao on 6/22/16.
  */
object PagedMySQL  {
  def main(args: Array[String]): Unit = {
    val url = "jdbc:mysql://rdsu41uutacn4ssbirh9public.mysql.rds.aliyuncs.com:3306/lims_four_pro?zeroDateTimeBehavior=convertToNull"
    val user = "bigdata"
    val passwd = "bigdata_kc11730"
    val conn = DriverManager.getConnection(url, user, passwd)
    val table = "fw_activity"
    var sql = s"""select count(1) from $table"""
    var rs = conn.prepareStatement(sql).executeQuery()
    rs.next()
    val total = rs.getInt(1)  // 表中总共有多少条数据?

    val batchSize = 10000 // 希望以此查询得到
    val loops = total / batchSize

    val remaining = total % batchSize
    var pos = 0

    for (i <- 0 until loops) {
      sql = s""" select * from $table limit ${i*batchSize}, $batchSize """
      println(sql)
    }

    if (remaining > 0) {
      val sql = s" select * from $table limit ${loops*batchSize}, $remaining"
    }

  }
}
