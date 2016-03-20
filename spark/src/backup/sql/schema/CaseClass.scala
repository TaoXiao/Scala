package cn.gridx.spark.examples.sql.schema

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SchemaRDD, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by tao on 8/10/15.
 */
class CaseClass {

    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("Hello Spark SQL")
        val sparkCtx  = new SparkContext(sparkConf)
        val sqlCtx    = new SQLContext(sparkCtx)

        val c1 = Car("BMW", "X1", 2.0)
        val c2 = Car("Benz", "GLC", 2.5)
        val c3 = Car("Audi", "A6", 1.8)
        val cars: RDD[Car] = sparkCtx.parallelize(List(c1,c2,c3), 3)

        /**
         * Inferring the schema using reflection
         *
         * Importing a SQLContext brings an implicit into scope that automatically
         * converts a standard RDD whose elements are scala case classes into a SchemaRDD.
         * This conversion can also be done explicitly
         * using the `createSchemaRDD` function on a [[SQLContext]].
         *
         * The Scala interface for Spark SQL supports automatically converting
         * an RDD containing case classes to a SchemaRDD.
         *
         * The case class defines the schema of the table. The names of the
         * arguments to the case class are read using reflection and
         * become the names of the columns. Case classes can also be nested or
         * contain complex types such as Sequences or Arrays. This RDD can be
         * implicitly converted to a SchemaRDD and then be registered as a table.
         * Tables can be used in subsequent SQL statements
         */

        /* spark sql 1.2 与 1.3 不兼容
        import sqlCtx.createSchemaRDD
        cars.registerTempTable("cars")
        */

        val res1: SchemaRDD = sqlCtx.sql("select * from cars")
        val res2: SchemaRDD = sqlCtx.sql("select * from cars where volume >= 2.0")

        print("\n------------------------\n")
        res1.collect().foreach(println)

        print("\n------------------------\n")
        res2.collect().foreach(println)

        print("\n------------------------\n")

        sparkCtx.stop()
    }

}

case class Car(brand:String, series: String,  volume: Double)
