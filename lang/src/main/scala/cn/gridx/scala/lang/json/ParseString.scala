package cn.gridx.scala.lang.json

import com.google.gson.{JsonArray, JsonParser}
import scala.collection.JavaConversions._

/**
  * Created by tao on 8/30/16.
  */
object ParseString extends App {
  def payload = """{
                  |        months: [1,2,4,7],
                  |        extents:  [-15, 20],
                  |        attrs: {
                  |             CARE: 0,
                  |             FREA:  1
                  |        }
                  |}""".stripMargin

  val parser = new JsonParser()
  val jObj = parser.parse(payload).getAsJsonObject
  val jMonths  = jObj.getAsJsonArray("months")
  val jExtents = jObj.getAsJsonArray("extents")
  val jAttrs   = jObj.getAsJsonObject("attr")

  if (null == jAttrs)
    println("为空")

  println(jMonths)
  println(jExtents)
  println(jAttrs)

  println(jExtents.get(0).getAsDouble + ":" + jExtents.get(1).getAsDouble)
}

