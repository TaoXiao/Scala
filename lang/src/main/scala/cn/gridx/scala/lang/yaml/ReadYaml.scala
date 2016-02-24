package cn.gridx.scala.lang.yaml


import java.io.InputStream
import java.util

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

import scala.beans.BeanProperty

/**
  * Created by tao on 1/24/16.
  */

class SpeedyConfig extends Serializable {
  @BeanProperty var name: String = "Unknown name"
  @BeanProperty var age: Int = -1
  @BeanProperty var city: String = "Unknown city"
  @BeanProperty var height: Float = 0
  @BeanProperty var details: util.HashMap[String, Object] = null
}


object ReadYaml {

  def main(args: Array[String]): Unit = {
    println("从text读取")
    val text: String = "name: Jack\nage: 29\ncity: NewYork"
    println(text)
    ReadFromString(text)


    println("\n\n从resource文件读取,并装配成Bean")
    // 如果是从resources目录读取文件的话,则不能直接运行本程序,要把本项目打成JAR包,然后运行本文件的主类
    // 否则,`getClass.getResourceAsStream("/sample.yaml")`是找不到目标文件的
    var stream = getClass.getResourceAsStream("/sample.yaml")
    ReadFromFile(stream)


    println("\n\n从resource文件读取,以HashMap的形式直接读取出来")
    stream = getClass.getResourceAsStream("/sample.yaml")
    ReadFromFile2(stream)

  }

  /**
    * 将配置组装成预置的Bean
    * */
  def ReadFromFile(stream: InputStream): Unit = {
    val yaml = new Yaml(new Constructor(classOf[SpeedyConfig]))
    val config: SpeedyConfig = yaml.load(stream).asInstanceOf[SpeedyConfig]
    println(s"${config.getName}, ${config.getAge}, ${config.getCity}")
    println(config.getDetails)
  }

  /**
    * 不使用Bean,直接将内容当做Map解析出来
    *
    * */
  def ReadFromFile2(stream: InputStream): Unit = {
    val yaml = new Yaml()
    val obj = yaml.load(stream).asInstanceOf[util.HashMap[String, Object]]

    // 用`get` 可以取出Map的内容
    val stringValue   = obj.get("name")
    val intValue      = obj.get("age").asInstanceOf[Int]
    val booleanValue  = obj.get("city").asInstanceOf[Boolean]
    val floatValue    = obj.get("height").asInstanceOf[Double].toFloat

    // 必须是util.HashMap
    val mapValue = obj.get("details").asInstanceOf[util.HashMap[String, Object]]

    println(s"name    -> ${stringValue}, " +
            s"age     -> ${intValue}, " +
            s"city    -> ${booleanValue}, " +
            s"height  -> ${floatValue}")

    println(s"map -> ${mapValue}")
  }


  def ReadFromString(text: String): Unit = {
    val yaml: Yaml = new Yaml(new Constructor(classOf[SpeedyConfig]))
    val config: SpeedyConfig = yaml.load(text).asInstanceOf[SpeedyConfig]
    println(s"${config.getName}, ${config.getAge}, ${config.getCity}")
  }
}
