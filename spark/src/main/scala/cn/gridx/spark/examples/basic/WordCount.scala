package cn.gridx.spark.examples.basic

import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by tao on 1/20/16.
 */
object WordCount  {

    def main(args: Array[String]): Unit = {
        Console.out.println("运行开始WordCount")
        
        if (args.length < 2) {
            Console.err.println("必须输入2个参数 (inPath, outPath)，请重试")
            System.exit(1)
        }

        System.out.println(s"输入参数有${args.length}个")
        for (i <- 0 until args.length)
            Console.out.println(s"args($i) -> ${args(i)}")

        val inPath  = args(0)
        val outPath = args(1)

        val conf = new SparkConf()
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        val sc = new SparkContext(conf)

        sc.textFile(inPath, 3)
                .flatMap(_.split(" "))
                .map((_, 1))
                .reduceByKey(_ + _)
                .saveAsTextFile(outPath)

        Console.out.println("WordCount运行结束")

        sc.stop
    }
}
