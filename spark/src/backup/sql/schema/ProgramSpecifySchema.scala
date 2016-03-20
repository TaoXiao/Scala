package cn.gridx.spark.examples.sql.schema

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{SchemaRDD, SQLContext}
import org.apache.spark.sql.catalyst.expressions.Row
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by tao on 8/10/15.
 */
object ProgramSpecifySchema {
    /* 文件的内容为
        张三,30,174.5,true
        李四,33,182.6,true
        王二麻子,40,171.1,true
        小凤仙,26,162.5,false
     */
    def DataFilePath = "/user/tao/xt-data/data1.txt"

    def main(args: Array[String]): Unit = {
        Logger.getRootLogger.setLevel(Level.ERROR)

        val sparkConf = new SparkConf().setAppName("Programmatically Specify Table Schema")
        val sparkCtx  = new SparkContext(sparkConf)
        val sqlCtx    = new SQLContext(sparkCtx)

        // 外部文件中的每行内容为 [姓名],[年龄],[身高],[是否男性]
        // 例如： 肖韬,30,174.5,True
        val text = sparkCtx.textFile(DataFilePath)

        // 指定每条数据（Row）的schema（列名与类型）
        val schema = StructType(Array(
                StructField("name", StringType, false),
                StructField("age",  IntegerType, false),
                StructField("height", DoubleType, false),
                StructField("is_male", BooleanType, false)))

        // 构造Row
        val rows = text.map(line => {
            val fields = line.split(",")
            Row(fields(0), fields(1).toInt, fields(2).toDouble, fields(3).toBoolean)
        })

        // 将schema应用到rows上，生成SchemaRDD
        val people: SchemaRDD = sqlCtx.applySchema(rows, schema)
        // 将people注册为一个名为"people"的数据表
        people.registerTempTable("people")

        println("-------------------------------------")
        val res: SchemaRDD = sqlCtx.sql("select * from people where age >= 33 ")

        /** 从SchemaRDD中访问Row的每列数据
         *
         * `Row` Represents one row of output from a relational operator.  Allows both generic access by ordinal,
         * which will incur boxing overhead for primitives, as well as native primitive access.
         *
         * It is invalid to use the native primitive interface to retrieve a value that is null, instead a
         * user must check `isNullAt` before attempting to retrieve a value that might be null.
         */
        res.map(row => ("Name =>" + row.getString(0), "Age => " + row.getInt(1),
                "Height => " + row.getDouble(2), "Is_Male => " + row.getBoolean(3)) )
            .collect()
            .foreach(println)

        println("-------------------------------------")

        sparkCtx.stop()
    }
}
