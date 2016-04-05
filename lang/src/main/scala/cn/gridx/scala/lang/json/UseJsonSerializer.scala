package cn.gridx.scala.lang.json

import java.lang.reflect.Type
import java.util

import com.google.gson._

/**
  * Created by tao on 4/4/16.
  *
  * 对一个给定的class, 在将其序列化为Json串时, 自定义Json的格式和内容
  *
  * 既可以改变class原有的field name, 也可以增加或者删除fields
  *
  * 输出为
      {
        "品 牌": "BMW",
        "是否新车": "No",
        "历任车主数量": 3,
        "车主 (包含二手车主)": [
          "张三",
          "李四",
          "王二"
        ],
        "价 格": {
          "人民币": 880000.0,
          "英  镑": 84615.38461538461,
          "美  元": 110000.0
        }
      }
  */
object UseJsonSerializer extends App {
  val car = Car("BMW", "X5", 3.0F, 880000F, Array[String]("张三", "李四", "王二"), false)

  val gsonBuilder = new GsonBuilder
  gsonBuilder.registerTypeAdapter(classOf[Car], new CarSerializer)
  gsonBuilder.setPrettyPrinting
  val gson = gsonBuilder.create

  val json = gson.toJson(car)
  println(json)


  case class Car(brand: String, series: String, displacement: Float,
                 price: Float, owners: Array[String], newCar: Boolean)

  /**
    * 正对类Car的Serializer
    * */
  class CarSerializer extends JsonSerializer[Car] {
    override def serialize(car: Car, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      val ret = new JsonObject

      // 加入普通基本类型的项
      ret.addProperty("品 牌", car.brand)
      ret.addProperty("是否新车", if (car.newCar) "Yes" else "No")
      ret.addProperty("历任车主数量", car.owners.size)

      // 加入Array类型的项
      val jsonArr = new JsonArray
      for (owner <- car.owners)
        jsonArr.add(new JsonPrimitive(owner))
      ret.add("车主 (包含二手车主)", jsonArr)

      val gson = new Gson()

      // 加入Map类型的项
      // `JsonObject#add` 可以加入一个 `JsonElement` 类型的property
      // 借此, 可以加入map等复杂类型
      val map = new util.HashMap[String, Double]()
      map.put("人民币", car.price)
      map.put("美  元", car.price/8)
      map.put("英  镑", car.price/10.4)
      ret.add("价 格", gson.toJsonTree(map))

      ret
    }
  }
}





