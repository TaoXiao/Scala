package cn.gridx.scala.lang.json

import com.google.gson.{Gson, GsonBuilder}

import scala.collection.immutable.HashMap.HashMap1
import scala.collection.mutable

/**
  * Created by tao on 6/16/16.
  *
  * 将一个JSON字符串直接转换成一个我们实现定义好的case class
  */
object FromJsonStr2Object extends App {
  val json =
    """
      |{
      |  "option_conditions": [
      |    { "name": "care",
      |       "value" : "Y"
      |    },
      |    { "name": "mb",
      |       "value": "Y"
      |    }
      |  ],
      |  "dimension_conditions" : [
      |    { "name": "E1" ,
      |      "min" : 523.1,
      |      "max" : 1024.8
      |    },
      |    { "name": "E6" ,
      |      "min" : 323.1,
      |      "max" : 924.8
      |    }
      |  ],
      |  "target_options" : [
      |    "care", "mb", "da", "cca"
      |  ],
      |  "target_dimensions": [
      |    { "name" : "E1",
      |      "interval" : [0, 150, 300, 450, 600, 750, 900, 1150, 1400]
      |    },
      |    { "name" : "E6",
      |      "interval" : [0, 200, 300, 400, 500, 600, 700, 800, 900]
      |    },
      |    { "name" : "ETOUA",
      |      "interval" : [0, 300, 500, 700, 900, 1100, 900, 1150, 1300]
      |    }
      |  ]
      |}
    """.stripMargin

  val gson = new Gson()
  /*val obj: Query = gson.fromJson(json, classOf[Query])
  obj.option_conditions.foreach(println)

  obj.option_conditions.foreach(println)
  obj.dimension_conditions.foreach(println)
  obj.target_options.foreach(println)
  obj.target_dimensions.foreach(x => x.interval.foreach(println))


  // 不行, 必须用java collection才能实现自动转换
  val st = gson.fromJson(json, classOf[ST])
  st.option_conditions.foreach(println)*/

  val str = s"""{
      "optionFilter" : {
        "care" : "Y",
        "fera" : "Y"
      },
      "dimensionFilter" : {
        "ETOUA-ETOUB" : {
          "min" : 100,
          "max" : 1400
        },
        "E1-ETOUA" : {
          "min" : 300,
          "max" : 2000
        }
      },
      "dimensionIntervals" : {
        "ETOUA-ETOUB" : [0, 200, 400, 600, 800, 1000, 1500, 2000, 2500, 3000],
        "ETOUB-ETOUA" : [300, 500, 800, 2000],
        "E1-ETOUA" : [100, 400, 600, 1000, 2000]
      },
      "targetOptions" : ["care", "mb", "da", "cca", "tbs"]
    }"""

  // val gson: Gson = new GsonBuilder().serializeNulls().create()
  val param = gson.fromJson(str, classOf[JavaParam])
  println(param.dimensionIntervals.get("ETOUA-ETOUB").mkString(","))

}

case class Query(option_conditions: Array[OptionCondition],
                 dimension_conditions: Array[DimensionCondition],
                 target_options: Array[String],
                 target_dimensions: Array[TargetDimension])

case class OptionCondition(name: String, value: String)
case class DimensionCondition(name: String, min: Float, max: Float)
case class TargetDimension(name: String, interval: Array[Float])

case class ST(option_conditions: Array[mutable.HashMap[String, String]],
              dimension_conditions: Array[mutable.HashMap[String, Object]],
              target_options: Array[String],
              target_dimensions: Array[mutable.HashMap[String, Object]])

case class JavaParam(optionFilter: java.util.HashMap[String, String],
                     dimensionFilter: java.util.HashMap[String, java.util.HashMap[String, Float]],
                     dimensionIntervals: java.util.HashMap[String, Array[Float]],
                     targetOptions: Array[String])
