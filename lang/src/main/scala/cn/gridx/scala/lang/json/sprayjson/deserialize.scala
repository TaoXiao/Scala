package cn.gridx.scala.lang.json.sprayjson

import spray.json.DefaultJsonProtocol
import spray.json._

/**
  * Created by tao on 11/23/16.
  */
case class CA(flag: String, list: List[Int])
case class Bean(a: String, b: Int, ca: CA)

object BeanJsonProtocol extends DefaultJsonProtocol {
  implicit val CAFormat   = jsonFormat2(CA)
  implicit val BeanFormat = jsonFormat3(Bean)
}

import BeanJsonProtocol._

object  Deserialize extends App {
  val json =
    """
      { "a": "A",
        "b": 100,
        "ca" : {
            "flag" : "FLAG!!",
            "list": [1,2,3,4,5]
        }
      }"""

  // get Abstract Syntax Tree
  val ast: JsValue = json.parseJson

  // convert AST to object
  val obj: Bean = ast.convertTo[Bean]
  println(obj)
}
