package cn.gridx.spark.examples.io

import java.io._

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by tao on 7/1/15.
 */
object HelloSpark extends App{
    //TestRecordDelimiter(args(0))

    //writeFile("./testFile.txt")
    readFile("./testFile.txt")

    def TestRecordDelimiter(TextFilePath: String): Unit = {
        val logger = Logger.getRootLogger
        logger.setLevel(Level.WARN)

        println(s"\n\nTextFilePath = ${TextFilePath}\n\n")

        val conf = new SparkConf()
        val sc   = new SparkContext(conf)

        val jobInstance = Job.getInstance
        val hadoopConf  = jobInstance.getConfiguration
        hadoopConf.set("textinputformat.record.delimiter", "\2")

        val rdd = sc.newAPIHadoopFile(TextFilePath, classOf[TextInputFormat],
            classOf[LongWritable], classOf[Text], hadoopConf)




        rdd.map("<<<< " + _._2.toString + ">>>>").collect.foreach(println)


        sc.stop
    }

    def writeFile(path: String): Unit = {
        println("开始接入文件")

        val writer = new FileWriter(new File(path))
       writer.append("A11\1A12\1A13\1A14\1")

        writer.close

        println("结束写入文件")
    }

    def readFile(path:String) = {
        val reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))
        var line = reader.readLine

        val parts = line.split('\1')

        println("内容为 " + parts.mkString("|"))

    }

}
