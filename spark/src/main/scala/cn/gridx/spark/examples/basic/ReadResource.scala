package cn.gridx.spark.examples.basic

import java.io.{InputStreamReader, BufferedReader}
import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{Path, FileSystem}
import org.apache.spark.{SparkContext, SparkConf}
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import scala.beans.BeanProperty

/**
 * Created by tao on 1/25/16.
 */
object ReadResource {
    def main(args: Array[String]) {
        if (args.length < 1) {
            Console.err.println(s"请输入1个参数（现在只有${args.length}个参数）")
            Console.err.println("参数为\n 配置文件HDFS路径")
            System.exit(1010)
        }

        val YamlResPath = args(0)


        Console.out.println(s"参数为： $YamlResPath")


        readHdfsTextFile(new Path(YamlResPath))


        /*
        val conf = new SparkConf()
        val sc = new SparkContext(conf)

        val yaml   = new Yaml(new Constructor(classOf[SpeedyConfig]))
        val stream = getClass.getResourceAsStream(YamlResPath)
        val config = yaml.load(stream).asInstanceOf[SpeedyConfig]
        val str = s"${config.getName} -> ${config.getAge} -> ${config.getCity}"

        Console.out.println(s"\n\n读取到的配置文件内容为\n\t ${str}\n\n")

        sc.stop
        */
    }

    def readHdfsTextFile(path: Path): Unit = {
        val conf = new Configuration()
        val fs = FileSystem.get(conf)
        val reader = new BufferedReader(new InputStreamReader(fs.open(path)))
        var sb = new StringBuffer()

        var line = reader.readLine()
        while (null != line) {
            sb.append(line)
            sb.append("\n")
            line = reader.readLine
        }

        Console.out.println(s"\n\n读取除的配置内容为：\n${sb.toString}")
        reader.close
    }
}


class SpeedyConfig {
    @BeanProperty var name : String = "Unknown name"
    @BeanProperty var age  : Int    = -1
    @BeanProperty var city : String = "Unknown city"
}
