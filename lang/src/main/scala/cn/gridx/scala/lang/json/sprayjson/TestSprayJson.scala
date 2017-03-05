package cn.gridx.scala.lang.json.sprayjson


import MyJsonProtocol._
import org.json4s.jackson.JsonMethods
import spray.json._
import org.json4s._
import scala.concurrent.Future

object TestSprayJson {
  def main(args: Array[String]): Unit = {
    /*
    val record        = Record(1, "description here")
    val moreRecord    = MoreRecord("127.0.0.1", Some(record))
    val json: JsValue = moreRecord.toJson
    val jsonString    = json.toString()
    println(jsonString)

    // 测试null
    var str = """{"ip":"127.0.0.1"}"""
    var obj = str.parseJson.convertTo[MoreRecord]
    println(obj)
    */

    implicit val formats = DefaultFormats

    import scala.concurrent.ExecutionContext.Implicits.global

    val f = Future {
      val targetOption = """{"138":"1"}"""
      val targetAttribute = """{"5":"interval","156":"E1","184":"#{calculationEndDate}","193":"#{calculationStartDate}"}"""
      println(s"targetOption = $targetOption")
      println(s"targetAttribute = $targetAttribute")

      val j1: JValue = JsonMethods.parse(targetOption)
      println(s"j1 = $j1")

      val m1: Map[String, String] = j1.extract[Map[String, String]]
      println(s"m1 = $m1")

      val j2 = JsonMethods.parse(targetAttribute)
      println(s"j2 = $j2")

      val m2 = j2.extract[Map[String, String]]
      println(s"m2 = $m2")

      val x = new JObject(List(("138", JString("1")))).extract[Map[String, String]]
      println(x)
    }


    Thread.sleep(10*1000)
  }
}

