package cn.gridx.scala.lang.database

import java.sql.DriverManager

/**
  * Created by tao on 6/22/16.
  */
object PagedMySQL  {
  def main(args: Array[String]): Unit = {
    /*
    val url = "jdbc:mysql://rdsu41uutacn4ssbirh9public.mysql.rds.aliyuncs.com:3306/lims_four_pro?zeroDateTimeBehavior=convertToNull"
    val user = "bigdata"
    val passwd = "bigdata_kc11730"
    val conn = DriverManager.getConnection(url, user, passwd)
    val table = "fw_activity"
    var sql = s"""select count(1) from $table"""
    var rs = conn.prepareStatement(sql).executeQuery()
    rs.next()
    val total = rs.getInt(1)  // 表中总共有多少条数据?
    */

    val total = 3001
    val batchSize = 1000 // 希望一次查询查询出的数据量
    val loops = total / batchSize

    val remaining = total % batchSize
    var pos = 0
    var i = 0

    do {
      val limit_start = i*batchSize
      val limit_size =
        if (0 == loops) remaining
        else if (i == loops-1) batchSize + remaining
        else batchSize
      println(s""" LIMIT $limit_start, $limit_size""")
      i += 1
    } while (i < loops)


  }
}
