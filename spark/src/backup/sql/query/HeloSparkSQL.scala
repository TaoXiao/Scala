package cn.gridx.spark.examples.sql.query

/**
 * Created by tao on 11/28/15.
 */

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{SchemaRDD, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql._


/**
 * Created by tao on 3/24/15.
 * These examples are based on Spark 1.2.1
 */
object HelloSparkSQL {
    def main(args: Array[String]): Unit = {

        test4
    }

    /**
     *
     */
    def test1() = {
        val conf =  new SparkConf().setAppName("Hello SparkSQL")
        val sc   =  new SparkContext(conf)
        /*  一个json数据必须在一行中，否则会报错：
                com.fasterxml.jackson.core.JsonParseException: Unexpected end-of-input within/between OBJECT entries
                    at [Source: java.io.StringReader@53adceb8; line: 1, column: 61]  */
        val jsonFile = "/user/tao/xt-data/product1.json"
        val sqlContext = new SQLContext(sc)
        val df: SchemaRDD = sqlContext.jsonFile(jsonFile)

        df.registerTempTable("product")
        df.printSchema()

        val rdd: SchemaRDD = sqlContext.sql("select RECORDS from product")

        sc.stop()
    }


    /**
     * Inferring the schema using reflection
     *
     * The Scala interaface for Spark SQL supports automatically converting
     * an RDD containing case classes to a SchemaRDD.
     *
     * The case class defines the schema of the table. The names of the
     * arguments to the case class are read using reflection and
     * become the names of the columns. Case classes can also be nested or
     * contain complex types such as Sequences or Arrays. This RDD can be
     * implicitly converted to a SchemaRDD and then be registered as a table.
     * Tables can be used in subsequent SQL statements
     */
    def test2() = {
        /* spark sql 1.2 与 1.3 不兼容
        val conf = new SparkConf()
                .setAppName("Test Spark SQL")
        val sc = new SparkContext(conf)
        val sqlCtx = new SQLContext(sc)

        // 『createSchemaRDD』 is used to implicitly converts an RDD to SchemaRDD
        // import sqlCtx.createSchemaRDD


        val p1 = Person("Jon", 28)
        val p2 = Person("Peter", 29)
        val p3 = Person("Kate", 30)

        val people: RDD[Person] = sc.parallelize(List(p1, p2, p3), 3)
        people.registerTempTable("people")

        val all: SchemaRDD = sqlCtx.sql("select * from people")
        val jon: SchemaRDD = sqlCtx.sql("select * from people where name = 'Jon'")

        println("====================================")
        all.collect.foreach(println)
        println("====================================")
        jon.collect.foreach(println)
        println("====================================")

        sc.stop
        */
    }

    // case class
    case class Person(name: String, age: Int)

    /**
     * The file 『hdfs://hadoop5.com:8020/user/tao/xt-data/people.txt 』 has four lines,
     * and the contents are as follows:
     * Kim,100
     * John,200
     * Kate, 300
     * Tom, 400
     */


    /**
     * Programmatically specifying the schema
     *
     * When case classes cannot be defined ahead of time (for example,
     * the structure of records is encoded in a string, or a text dataset
     * will be parsed and fields will be projected differently for
     * different users), a SchemaRDD can be created programmatically with three steps.
     *
     * 1. Create an RDD of Rows from the original RDD;
     * 2. Create the schema represented by a StructType matching the structure of Rows in the RDD created in Step 1.
     * 3. Apply the schema to the RDD of Rows via applySchema method provided by SQLContext.
     */
    def test3(): Unit = {
        def TextFilePath = "hdfs://hadoop5.com:8020/user/tao/xt-data/people.txt"
        val sc = new SparkContext(new SparkConf().setAppName("Test Spark SQL !"))
        val sqlCtx = new SQLContext(sc)
        val text = sc.textFile(TextFilePath)
        // schema is encoded in a string
        val schemaString = "name age"

        // generate the schema based on the string of schema
        val schema: StructType = StructType(  schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)) )

        // convert RDD[String] to RDD[Row]
        val rowRDD: RDD[Row] = text.map(_.split(",")).map(p => Row(p(0), p(1).trim))

        // Apply the schema to RDD[Row]
        val peopleSchemaRDD: SchemaRDD = sqlCtx.applySchema(rowRDD, schema)

        // register the SchemaRDD as a table
        peopleSchemaRDD.registerTempTable("people")

        // SQL query的结果是SchemaRDD，  SchemaRDD支持所有的RDD操作
        val results: SchemaRDD = sqlCtx.sql("select * from people where name='John'")

        println("*************************************")
        results.collect().foreach(println)
        // columns of the row can be accessed by ordinal
        results.map(x => "Name:" + x(0) + ", " + "Age:" + x(1)).collect.foreach(println)
        println("*************************************")

        sc.stop()
    }

    /**
     * Language-integrated relational queries
     * 可以用 rdd.where(···).where(···).select(···)等方式来进行query
     *
     * Output is as follows:
     * Result:[John]
     * Result:[Kate]
     */
    def test4() {
        def TextFilePath = "hdfs://hadoop5.com:8020/user/tao/xt-data/people.txt"
        val sc = new SparkContext(new SparkConf().setAppName("Test Spark SQL !"))
        val sqlCtx = new SQLContext(sc)
        val text = sc.textFile(TextFilePath)

        // 必须要导入，否则 where clause不存在
        import sqlCtx._

        val schema = StructType(List(
            StructField("name", StringType, false),     // 第1列的列名是 name，类型是 String
            StructField("age", IntegerType, false)))    // 第2列的列名是 age， 类型是 Integer

        val rowRDD = text.map(_.split(","))
                .map(p => Row(p(0).trim, p(1).trim.toInt)) // 第一列是string，第二列是int

        val schemaRDD = sqlCtx.applySchema(rowRDD, schema)

        schemaRDD.registerTempTable("schemaRDD")

        /**
         * 选择 age 大于等于300的记录
         *
         * The DSL uses Scala symbols to represent columns in the underlying table,
         * which are identifiers prefixed with a tick (').
         * Implicit conversions turn these symbols into expressions that are
         * evaluated by the SQL execution engine
         */

        /* spark sql 1.2 与 1.3 不兼容
        val results = schemaRDD.where('age >= 200).where('age <= 300).select('name)

        results.collect().foreach(x => println("result:" + x))
        */
    }
}
