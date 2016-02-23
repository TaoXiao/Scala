package cn.gridx.scala.lang.datetime

import org.joda.time.DateTime

/**
 * Created by tao on 12/21/15.
 */
object times {
    def main(args: Array[String]): Unit = {
        val d = DateTime.now.toString("yyyy-MM-dd HH:mm:ss")
        val d1 = "2015-12-21 19:44:25"
        println(d1.compareTo(d))

    }
}
