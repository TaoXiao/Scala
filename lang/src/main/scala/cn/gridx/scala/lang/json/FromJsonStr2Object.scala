package cn.gridx.scala.lang.json

import com.google.gson.Gson

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
  val obj = gson.fromJson(json, classOf[Query])

  obj.option_conditions.foreach(println)
  obj.dimension_conditions.foreach(println)
  obj.target_options.foreach(println)
  obj.target_dimensions.foreach(x => x.interval.foreach(println))

}

case class Query(option_conditions: Array[OptionCondition],
                 dimension_conditions: Array[DimensionCondition],
                 target_options: Array[String],
                 target_dimensions: Array[TargetDimension])

case class OptionCondition(name: String, value: String)
case class DimensionCondition(name: String, min: Float, max: Float)
case class TargetDimension(name: String, interval: Array[Float])
