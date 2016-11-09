package cn.gridx.scala.lang.test

import java.io.StringReader

import com.google.gson.{JsonObject, JsonParser}
import org.apache.commons.lang.StringUtils

/**
  * Created by tao on 7/31/16.
  */
object test2 {
  val x: Int = 100

  def main(args: Array[String]): Unit = {
    val json = """{
                 |        "province": "贵州省" ,
                 |        "city": "贵阳市",
                 |        "area": "观山湖区",
                 |        "company_name": "老干妈食品有限公司",
                 |        "latitude": 3534.2435
                 |      }""".stripMargin
    val param = parsePayload(json)

    println(param)
    print(param.longitude.getOrElse("None"))
  }

  def parsePayload(payload: String): EnterpriseInfoParam = {
    val parser = new JsonParser()
    val obj: JsonObject = parser.parse(new StringReader(payload)).asInstanceOf[JsonObject]
    EnterpriseInfoParam(
      obj.get("province").getAsString, obj.get("city").getAsString, obj.get("area").getAsString, obj.get("company_name").getAsString,
      if (obj.has("longitude")) Some(obj.get("longitude").getAsDouble) else None,
      if (obj.has("latitude")) Some(obj.get("latitude").getAsDouble) else None)
  }
}


case class A(a:String, b: Int, c: String, d: String )

final case class EnterpriseInfoParam(province: String, city: String, area: String, company_name: String, longitude: Option[Double], latitude: Option[Double])
