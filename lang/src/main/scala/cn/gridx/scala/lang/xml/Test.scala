package cn.gridx.scala.lang.xml

import scala.xml.{NodeSeq, XML}

/**
  * Created by tao on 11/7/16.
  *
  */
object Test {
  def main(args: Array[String]): Unit = {
    readXML()
  }


  /**
    * 参考：http://alvinalexander.com/scala/scala-xml-searching-xmlns-namespaces-xpath-parsing
    * */
  def readXML(): Unit = {
    val xml = XML.loadFile("/Users/tao/IdeaProjects/Scala/lang/src/main/resources/sample.xml")
    val version: String = (xml \\ "rss" \ "@version") text
    val title = (xml \\ "rss" \\ "channel" \\ "title") text
    val temp: String = (xml \\ "channel" \\ "item" \ "condition" \ "@temp") text

    println(title)
    println(version)
    println(temp)
  }

}
