package cn.gridx.scala.lang.database

import java.io.PrintWriter
import java.sql.DriverManager

import scala.collection.mutable

/**
  * Created by tao on 5/25/16.
  */
object ReadMySQL {
  def path = "data_2.txt"
  def main(args: Array[String]): Unit = {
    val url = "jdbc:mysql://10.100.34.151:3306/pge_prod?user=demo&password=RE3u6pc8ZYx1c"
    val conn = DriverManager.getConnection(url)
    var sql = "select count(*) from pge_prod.accountbillimpactmailer "

    var pstmt = conn.prepareStatement(sql)
    var rs = pstmt.executeQuery()
    rs.next()
    val total = rs.getInt(1)  //  总数
    println(s"数据库中的记录总数为 $total")
    rs.close()

    val writer = new PrintWriter(path)

    sql =
      s""" select contract_id, servicepoint_id,
          da, cca, tbs, care, fera, sr, sc, mb, ee, nems,
          ratecompared1, ratecompared1_amt,
          ratecompared2, ratecompared2_amt,
          ratecompared3, ratecompared3_amt
      from pge_prod.accountbillimpactmailer """

    pstmt = conn.prepareStatement(sql)
    rs = pstmt.executeQuery()
    var count = 0
    while (rs.next()) {
      val key = rs.getString("contract_id") + ":" + rs.getString("servicepoint_id")

      val options = mutable.HashSet[String]()
      if (!rs.getString("da").isEmpty)  options.add(s"""opt:da:${rs.getString("da")}""")
      else  options.add(s"""opt:da:N""")

      if (!rs.getString("cca").isEmpty)   options.add(s"""opt:cca:${rs.getString("cca")}""")
      else  options.add(s"""opt:cca:N""")

      if (!rs.getString("tbs").isEmpty)   options.add(s"""opt:tbs:${rs.getString("tbs")}""")
      else  options.add(s"""opt:tbs:N""")

      if (!rs.getString("care").isEmpty)  options.add(s"""opt:care:${rs.getString("care")}""")
      else  options.add(s"""opt:care:N""")

      if (!rs.getString("fera").isEmpty)  options.add(s"""opt:fera:${rs.getString("fera")}""")
      else  options.add(s"""opt:fera:N""")

      if (!rs.getString("sr").isEmpty)    options.add(s"""opt:sr:${rs.getString("sr")}""")
      else  options.add(s"""opt:sr:N""")

      if (!rs.getString("sc").isEmpty)    options.add(s"""opt:sc:${rs.getString("sc")}""")
      else  options.add(s"""opt:sc:N""")

      if (!rs.getString("mb").isEmpty)    options.add(s"""opt:mb:${rs.getString("mb")}""")
      else  options.add(s"""opt:mb:N""")

      if (!rs.getString("ee").isEmpty)    options.add(s"""opt:ee:${rs.getString("ee")}""")
      else  options.add(s"""opt:ee:N""")

      if (!rs.getString("nems").isEmpty)  options.add(s"""opt:nems:${rs.getString("nems")}""")
      else  options.add(s"""opt:nems:N""")

      val dims = mutable.HashSet[String]()
      if (!rs.getString("ratecompared1").isEmpty && !rs.getString("ratecompared1_amt").isEmpty)
        dims.add(s"""dim:${rs.getString("ratecompared1")}:${rs.getString("ratecompared1_amt")}""")
      if (!rs.getString("ratecompared2").isEmpty && !rs.getString("ratecompared2_amt").isEmpty)
        dims.add(s"""dim:${rs.getString("ratecompared2")}:${rs.getString("ratecompared2_amt")}""")
      if (!rs.getString("ratecompared3").isEmpty && !rs.getString("ratecompared3_amt").isEmpty)
        dims.add(s"""dim:${rs.getString("ratecompared3")}:${rs.getString("ratecompared3_amt")}""")


      writer.println(s"""$key,${options.mkString(",")},${dims.mkString(",")}""")
      count += 1

      if (count % 10000 == 0)
        println(count)
    }

    writer.close()

    conn.close()
  }
}
