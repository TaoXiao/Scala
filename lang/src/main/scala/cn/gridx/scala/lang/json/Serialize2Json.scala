package cn.gridx.scala.lang.json

import java.util

import com.google.gson._



/**
  * Created by tao on 2/29/16.
  */
object Serialize2Json {
  def main(args: Array[String]): Unit = {
    // Serialize_PrimaryTypes
    // Serialize_CustomTypes
    // renameFileds
    // UseJsonSerializer
    InsertSubJson
  }


  /**
    * 将基本数据类型通过GSON转换成json
    **/
  def Serialize_PrimaryTypes(): Unit = {
    val gson = new Gson()

    val a = new Array[Int](5)
    for (i <- 0 until a.size)
      a(i) = i * 100
    var json = gson.toJson(a)
    println(s"a is converted to $json")


    val b = new util.TreeMap[String, Array[Int]]()
    b.put("张三", Array.fill[Int](3)(3))
    b.put("李四", Array.fill[Int](4)(4))
    b.put("王五", Array.fill[Int](5)(5))
    json = gson.toJson(b)
    println(s"b is converted to $json")
  }


  /**
    * 将自定义的数据类型转换成JSON
      {   "name"  : "Jack",
          "weight": 76.6,
          "salaries" : {
              "January" : 11111.1,
              "February": 22222.2,
              "March"   : 33333.3
          },
          "children" : [
              "Tom","Merry","Rose"
          ]
      }
    * */
  def Serialize_CustomTypes(): Unit = {
    val salaries = new util.HashMap[String, Float]()
    salaries.put("January"  , 11111.1f)
    salaries.put("February" , 22222.2f)
    salaries.put("March"    , 33333.3f)

    val children = Array("Tom", "Merry", "Rose")

    val jack = Person("Jack", 76.6f, salaries, children)
    val json = new Gson().toJson(jack)
    println(s"jack is converted to $json")


  }

  case class Person(name: String, weight: Float,
                    salaries: util.HashMap[String, Float],
                    children: Array[String]) {}


  /**
    * 为class生成JSON时,将它的filed进行重命名
    * */
  def renameFileds(): Unit = {
    val gson = new Gson()

    val st = new ScalaST("Jack Tomcat", 300)
    val tree = gson.toJson(st)

    println(tree)
  }

  /***
    * 在一个JSON中插入一个sub-json
    */
  def InsertSubJson(): Unit = {
    val gson = new Gson()
    val jArr = new JsonArray()
    // val m: HashMap[String, String] = HashMap("A" -> "a")
    import scala.collection.JavaConversions._
    // val g: JsonElement = gson.toJsonTree(m)
    val m1 = new util.HashMap[String, String]()
    m1.put("A", "a")
    val m2 = new util.HashMap[String, String]()
    m2.put("B", "b")
    val m3 = new util.HashMap[String, String]()
    m3.put("C", "c")

    jArr.add(gson.toJsonTree(m1))
    jArr.add(gson.toJsonTree(m2))
    println(jArr)

    val jSubObj = new JsonObject()
    jSubObj.addProperty("A", "a")
    jSubObj.addProperty("C", "c")
    jSubObj.addProperty("B", "b")


    val jObj = new JsonObject()
    jObj.add("bootingTimes", jSubObj)
    println(jObj)

  }


}

