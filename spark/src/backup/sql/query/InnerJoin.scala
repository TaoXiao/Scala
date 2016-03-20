package cn.gridx.spark.examples.sql.query

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{SchemaRDD, SQLContext}
import org.apache.spark.sql.catalyst.expressions.Row
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by tao on 8/10/15.
 */
object InnerJoin {
    /* 文件1的内容为
            张三,30,174.5,true
            李四,33,182.6,true
            王二麻子,40,171.1,true
            小凤仙,26,162.5,false

       文件2的内容为
            张三,南京,大学
            李四,上海,高中
            王二麻子,北京,初中
            小凤仙,西安,小学


       两个文件的主键（姓名）是一对一的关系
     */
    def DataFilePath1 = "/user/tao/xt-data/data1.txt"
    def DataFilePath2 = "/user/tao/xt-data/data2.txt"

    def main(args: Array[String]): Unit = {
        //Logger.getRootLogger.setLevel(Level.ERROR)

        // 测试 inner join
        join(DataFilePath1, DataFilePath2, "select * from t1, t2 where t1.name = t2.name")



    }

    /**
     * 通过`sql`参数我们可以指定inner join，left join, right join 或者 full join
     * @param path1     -   第一个文件的路径
     * @param path2     -   第二个文件的路径
     * @param sql       -   SQL语句
     *
     * 两张表的名字默认是t1、t2
     */
    def join(path1:String, path2:String, sql:String): Unit = {
        val sparkConf = new SparkConf().setAppName("Spark Sql Join Example")
        val sparkCtx  = new SparkContext(sparkConf)
        val sqlCtx    = new SQLContext(sparkCtx)

        val rows1 = sparkCtx.textFile(path1)
                .map(line => { val fields = line.split(",")
            Row(fields(0), fields(1).toInt, fields(2).toDouble, fields(3).toBoolean)
        })
        val schema1 = StructType(Array(
            StructField("name", StringType),    StructField("age", IntegerType),
            StructField("height", DoubleType),  StructField("is_male", BooleanType) ))
        val t1: SchemaRDD = sqlCtx.applySchema(rows1, schema1)
        t1.registerTempTable("t1")

        val rows2 = sparkCtx.textFile(path2)
                .map(line => { val fields = line.split(",")
            Row(fields(0), fields(1), fields(2))
        })
        val schema2 = StructType(Array(
            StructField("name", StringType),    StructField("city", StringType),
            StructField("education", StringType) ))
        val t2: SchemaRDD = sqlCtx.applySchema(rows2, schema2)
        t2.registerTempTable("t2")

        /* 执行查询的输出为：
                [王二麻子,40,171.1,true,王二麻子,北京,初中]
                [李四,33,182.6,true,李四,上海,高中]
                [张三,30,174.5,true,张三,南京,大学]
                [小凤仙,26,162.5,false,小凤仙,西安,小学]
         */
        val res = sqlCtx.sql(sql)
        res.collect.foreach(println)

        sparkCtx.stop()
    }
}
