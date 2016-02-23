package cn.gridx.scala.lang.yaml


import java.io.{InputStream, File, FileInputStream}

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import scala.beans.BeanProperty
import scala.io.Source

/**
 * Created by tao on 1/24/16.
 */

class SpeedyConfig  extends Serializable{
    @BeanProperty var name : String = "Unknown name"
    @BeanProperty var age  : Int    = -1
    @BeanProperty var city : String = "Unknown city"
}


object ReadYaml {

    def main(args: Array[String]): Unit = {
        // 测试
        println("从text读取")
        val text: String = "name: Jack\nage: 29\ncity: NewYork"
        println(text)
        ReadFromString(text)

        println("\n\n从resource文件读取")
        val stream = getClass.getResourceAsStream("/test.yaml")
        ReadFromFile(stream)
    }

    def ReadFromFile(stream: InputStream): Unit = {
        val yaml = new Yaml(new Constructor(classOf[SpeedyConfig]))
        val config: SpeedyConfig = yaml.load(stream).asInstanceOf[SpeedyConfig]
        println(s"${config.getName}, ${config.getAge}, ${config.getCity}")
    }

    def ReadFromString(text: String): Unit = {
        val yaml: Yaml   = new Yaml(new Constructor(classOf[SpeedyConfig]))
        val config: SpeedyConfig = yaml.load(text).asInstanceOf[SpeedyConfig]

        println(s"${config.getName}, ${config.getAge}, ${config.getCity}")
    }
}
