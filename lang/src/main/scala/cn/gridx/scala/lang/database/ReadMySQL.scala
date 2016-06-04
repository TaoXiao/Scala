package cn.gridx.scala.lang.database

import java.sql.DriverManager

/**
  * Created by tao on 5/25/16.
  */
object ReadMySQL {
  def main(args: Array[String]): Unit = {
    var list = List[Long]()
    list = 100L :: list
    list = 200L :: list
    list = 300L :: list

    val x = list.forall(_ != 0)
    println(x)

    /*
    val conn = DriverManager.getConnection("jdbc:mysql://10.100.34.151/pge_prod")
    val pstmt = conn.prepareStatement("select count(*) from customerbillingcycle")
    val rs = pstmt.executeQuery
    rs.next
    println("结果为 " + rs.getInt(1))
    */
  }
}
