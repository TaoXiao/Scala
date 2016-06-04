package cn.gridx.scala.spark.sql.dataframes

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by tao on 5/25/16.
  */
object ReadMySQL {
  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf())

    val config = Map(
      "url"     -> s"jdbc:mysql:///${yaml.getMysqlDatabase}",
      "user"    -> yaml.getMysqlUsername,
      "password" -> yaml.getMysqlPassword,
      "dbtable" -> ("(select __pk_customer, unique_tracking_id, contract_id, meteraccount_id,  servicepoint_id, " +
        "entity_id_a, entity_id_b, _fk_contractroot, billing_cycle_group_id, time_zone, UNIX_TIMESTAMP(start_date) start," +
        "CASE WHEN end_date IS NOT NULL THEN UNIX_TIMESTAMP(end_date) ELSE 0 END AS end from customer " +
        "where meteraccount_id = '0000527405') a "), //  todo: 测试,只取一条数据
      "driver"  -> "com.mysql.jdbc.Driver"/*,
      "partitionCoumn" -> Pk,
      "lowerBound" -> "1",
      "upperBound" -> "6154243",
      "numPartitions" -> "500"*/
    )
  }

}
