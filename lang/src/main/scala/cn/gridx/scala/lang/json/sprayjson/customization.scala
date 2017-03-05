package cn.gridx.scala.lang.json.sprayjson

import org.joda.time.DateTimeZone
import spray.json._

import scala.collection.mutable.ListBuffer


object DateTimeZoneFormat extends RootJsonFormat[DateTimeZone] {
  // 序列化
  def write(tz: DateTimeZone): JsValue = {
    JsObject("id" -> JsString(tz.getID))
  }

  // 反序列化
  def read(value: JsValue): DateTimeZone = {
    value.asJsObject.getFields("id") match {
      case Seq(JsString(id)) =>
        DateTimeZone.forID(id)
      case _ =>
        throw new RuntimeException("ERROR")
    }
  }
}


object JavaJsonProtocols extends DefaultJsonProtocol with NullOptions {
  implicit val __DateTimeZone       = DateTimeZoneFormat
  implicit val __A                  = jsonFormat1(A)
  // implicit val __GlobalDataRequest  = jsonFormat17(GlobalDataRequest)
}


case class A(a: List[DateTimeZone])


/**
  * Created by tao on 1/11/17.
  */
object customization extends App {
  import JavaJsonProtocols._

  println("测试A")
  val tz = DateTimeZone.forID("America/Los_Angeles")

  var json = tz.toJson
  var jString = json.toString
  println("序列化后的字符为:")
  println(jString)

  println("\n反序列化的结果为:")
  var ast = jString.parseJson
  var obj = ast.convertTo[DateTimeZone]
  println(obj)


  println("//////////////////////////////////")
  val list = List("1", "2", "3")
  println(list.toJson.toString)



  ///////////////////////////////////////////////////////////

  /*
  println("\n\n测试 GlobalDataRequest")
  val req = GlobalDataRequest(null, "AAA", "BBBB", "CCC",
  "DDDD", tz, 3, None,
    true, true, false, false, false,
    null, "FILTERS", null, true)

  json = req.toJson
  jString = json.toString
  println("序列化后的字符为:")
  println(jString)
*/

}
