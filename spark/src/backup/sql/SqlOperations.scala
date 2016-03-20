package cn.gridx.spark.examples.sql

/**
 * Created by tao on 11/28/15.
 */

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.types.{IntegerType, StructField, StringType, StructType}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SchemaRDD, SQLContext}
import org.apache.spark.sql.catalyst.expressions.Row
// import org.apache.spark.sql.catalyst.types.{StringType, StructField, StructType}

/**
 * Created by tao on 4/8/15.
 */
object SqlOperations {
    def main(args: Array[String]): Unit = {
        // 运行Spark Job时，控制台上只出现WARN级别以上的日志数据
        val logger = Logger.getRootLogger
        logger.setLevel(Level.WARN)

        val conf = new SparkConf().setAppName("Test Join operations ")
        val sc = new SparkContext(conf)
        test_join(sc)
        sc.stop()
    }

    /**
     * test joining two tables on a given primary key
     *
     * File1's contents:
        Tom,Male,27
        John,Male,28
        Kate,Female,29
        Peter,Male,30


        File2's contents:
            Tom#T1
            Tom#T2
            Tom#T3
            John#J1
            Kate#K1
            Kate#K2
            Kate#K3
     *
     *
     *
     * @param sc
     */
    def test_join(sc: SparkContext): Unit = {
        def Path_File_1 = "hdfs://hadoop5.com:8020/user/tao/xt-data/file1.txt"
        def Path_File_2 = "hdfs://hadoop5.com:8020/user/tao/xt-data/file2.txt"


        val sqlCtx = new SQLContext(sc)

        val schema1 = StructType(List(
            StructField("name", StringType, false),
            StructField("gender", StringType, true),
            StructField("age", IntegerType, true)
        ))

        val schema2 = StructType(List(
            StructField("name", StringType, false),
            StructField("property", StringType, true)
        ))

        val rowRDD1 = sc.textFile(Path_File_1).map(_.split(","))
                .map(p => Row(p(0),p(1), p(2).toInt))

        val rowRDD2 = sc.textFile(Path_File_2).map(_.split("#"))
                .map(p => Row(p(0), p(1)))

        val schemaRDD1 = sqlCtx.applySchema(rowRDD1, schema1)
        val schemaRDD2 = sqlCtx.applySchema(rowRDD2, schema2)

        schemaRDD1.registerTempTable("t1")
        schemaRDD2.registerTempTable("t2")

        //---------- 1. Inner Join --------------

        /* 输出结果：
          [Kate,Female,29,Kate,K1]
          [Kate,Female,29,Kate,K2]
          [Kate,Female,29,Kate,K3]
          [Tom,Male,27,Tom,T1]
          [Tom,Male,27,Tom,T2]
          [Tom,Male,27,Tom,T3]
          [John,Male,28,John,J1]
        */
        var result: SchemaRDD = sqlCtx.sql("select * from t1, t2 where t1.name = t2.name")
        result.collect().foreach(println)

        println("\n\n\n")

        /* 输出结果：
            [Kate,Female,29,K1]
            [Kate,Female,29,K2]
            [Kate,Female,29,K3]
            [Tom,Male,27,T1]
            [Tom,Male,27,T2]
            [Tom,Male,27,T3]
            [John,Male,28,J1]
         */
        result = sqlCtx.sql("select t1.name, t1.gender, t1.age, t2.property from t1, t2 where t1.name = t2.name")
        result.collect().foreach(println)

        println("\n\n\n")

        /* 输出结果
            name=Kate, gender=Female, age=29, property=K1
            name=Kate, gender=Female, age=29, property=K2
            name=Kate, gender=Female, age=29, property=K3
            name=Tom, gender=Male, age=27, property=T1
            name=Tom, gender=Male, age=27, property=T2
            name=Tom, gender=Male, age=27, property=T3
            name=John, gender=Male, age=28, property=J1

            SchemaRDD中的每一个元素是一个Row，代表结果中的一行数据
            里面的列的序号从0开始，通过列序号和数据类型可以取出该行中某一列的数据
         */
        result.map(row => "name=" + row.getString(0) + ", gender=" + row.getString(1) + ", age=" + row.getInt(2) + ", property=" + row.getString(3))
                .collect.foreach(println)

        println("\n\n\n")




        /*---------- 2. Left Join --------------
          输出结果：
            name=Kate, gender=Female, age=29, property=K1
            name=Kate, gender=Female, age=29, property=K2
            name=Kate, gender=Female, age=29, property=K3
            name=Tom, gender=Male, age=27, property=T1
            name=Tom, gender=Male, age=27, property=T2
            name=Tom, gender=Male, age=27, property=T3
            name=Peter, gender=Male, age=30  可见，t2中不存在 name='Peter' 的记录
            name=John, gender=Male, age=28, property=J1
         */

        result = sqlCtx.sql("select t1.name, t1.gender, t1.age, t2.property from t1 left join t2 on t1.name=t2.name ")
        result.map(row => "name=" + row.getString(0) +  ", gender=" + row.getString(1) + ", age=" + row.getInt(2) + {
            // 对于可能为空的column，要用 isNullAt(i) 来进行判定
            if (!row.isNullAt(3))  ", property=" + row.getString(3)
            else "" }
        ).collect.foreach(println)


        println("\n\n\n")


        /*---------- 3. Aggregate By Key --------------
          输出结果：
            ((Tom,Male,27),List(T3, T2, T1))
            ((John,Male,28),List(J1))
            ((Kate,Female,29),List(K3, K2, K1))
            ((Peter,Male,30),List(NULL))
         */
        import org.apache.spark.SparkContext._
        result = sqlCtx.sql("select t1.name, t1.gender, t1.age, t2.property from t1 left join t2 on t1.name=t2.name ")
        result.map(row => ((row.getString(0), row.getString(1), row.getInt(2)), // 前面三个column作为key
                        { if (row.isNullAt(3)) "NULL" else row.getString(3) })) // 最后一个column作为value
              .aggregateByKey(List[String]())((xs:List[String], y:String) => y :: xs,
                    (xs:List[String], ys:List[String]) => xs ::: ys)
                .collect
                .foreach(println)

    }
}

