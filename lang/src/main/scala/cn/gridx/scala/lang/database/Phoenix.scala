package cn.gridx.scala.lang.database

import java.sql.DriverManager

/**
 * Created by tao on 11/16/15.
 *
 * 调用命令：
 *      java -cp examples-1.0-SNAPSHOT.jar:/opt/cloudera/parcels/CDH-5.3.2-1.cdh5.3.2.p0.10/jars/scala-library-2.10.4.jar:基础公共数据整合/lib/phoenix-4.3.1-client.jar cn.gridx.scala.example.database.Phoenix
 *
 * 不需要用到其他的跟HBase或者Hadoop相关的包，只需要用到phoenix-client.jar
 */
object Phoenix {
    def ZkNode = "hadoop1.com:2181"

    def main(args:Array[String]): Unit = {
        println("\nhello\n")
        // CreateTable
        UpsertValues
    }

    def CreateTable(): Unit = {
        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver")
        val conn = DriverManager.getConnection(s"jdbc:phoenix:${ZkNode}")
        val sql = """create table "fundamental:test" (
                    "pk" varchar not null primary key,
                    "c1" varchar,
                    "c2" varchar )""".stripMargin

        val pstmt = conn.prepareStatement(sql)
        pstmt.execute
        pstmt.close
        conn.close
    }

    def UpsertValues(): Unit = {
        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver")
        val conn = DriverManager.getConnection(s"jdbc:phoenix:${ZkNode}")
        val sql = """
                upsert into "fundamental:test" ("pk", "c1", "c2")
                    values('100', '200', '300')""".stripMargin
        val pstmt = conn.prepareStatement(sql)
        pstmt.execute
        conn.commit  // 需要commit才能让数据写进去
        pstmt.close
        conn.close
    }


}
