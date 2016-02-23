package cn.gridx.scala.lang.json

import java.util

/*
import org.json4s.{JsonAST, ShortTypeHints}
import org.json4s.jackson.Serialization

import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

// import scala.collection.immutable.HashMap
*/

/**
 * Created by tao on 8/3/15.
 */
object TestJson {
    def main(args: Array[String]): Unit = {
        //implicit val formats = Serialization.formats(ShortTypeHints(List()))

        /**
         * 要 import org.json4s.JsonDSL._
         * 才能令符号 ~ 有意义
         */
        /*
        val str: JsonAST.JObject =
            ("property_name" -> "三聚氰胺(mg/kg)") ~ ("property_result"  -> "阴性")


        println(compact(render(str)))
        */

        val jsonStr = """{
                          "names" : {
                            "A" : "肖韬",
                            "B" : "林国宝"
                          },
                        "city" : "福州",
                        "female":false
                        }
                      """

        val result = new org.codehaus.jackson.map.ObjectMapper().readValue(
                jsonStr, classOf[util.HashMap[String, Object]])
        println("names ==> " + result.get("names"))
        println("names.A ==> " + result.get("names").asInstanceOf[util.HashMap[String, Object]].get("A"))
        println("city ==> " + result.get("city"))
        println("female ==> " + result.get("female"))

    }
}
