package cn.gridx.spark.examples.basic

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{Path, FileSystem}
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by tao on 1/22/16.
 */
object AppendNum {
    def main(args: Array[String]): Unit = {
        Console.out.println("运行开始AppendNum")

        val conf = new SparkConf()
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        val sc = new SparkContext(conf)

        if (args.length < 2) {
            Console.err.println("必须输入2个参数 (inPath, outPath)，请重试")
            System.exit(1)
        }

        val inPath  = args(0)
        val outPath = args(1)

        val lines = sc.textFile(inPath, 5).collect
        val out = FileSystem.get(new Configuration()).create(new Path(outPath))

        for (i <- 0 until lines.size) {
            out.writeChars(s"${i} -> ")
            out.writeChars(lines(i))
            out.writeChars("\n")
        }

        out.flush
        out.close

        sc.stop
        Console.out.println("AppendNum运行结束")
    }
}
