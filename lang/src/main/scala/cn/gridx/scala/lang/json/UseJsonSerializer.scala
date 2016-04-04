package cn.gridx.scala.lang.json

import java.lang.reflect.Type
import com.google.gson._

/**
  * Created by tao on 4/4/16.
  *
  * 对一个给定的class, 在将其序列化为Json串时, 自定义Json的格式和内容
  *
  * 既可以改变class原有的field name, 也可以增加或者删除fields
  *
  */
object UseJsonSerializer extends App {
  val car = Car("BMW", "X5", 3.0F, 88F, Array[String]("张三", "李四", "王二"), false)

  val gsonBuilder = new GsonBuilder
  gsonBuilder.registerTypeAdapter(classOf[Car], new CarSerializer)
  gsonBuilder.setPrettyPrinting
  val gson = gsonBuilder.create

  val json = gson.toJson(car)
  println(json)


  case class Car(brand: String, series: String, displacement: Float,
                 price: Float, owners: Array[String], newCar: Boolean)


  class CarSerializer extends JsonSerializer[Car] {
    override def serialize(car: Car, typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      val ret = new JsonObject

      ret.addProperty("品 牌", car.brand)
      ret.addProperty("是否新车", if (car.newCar) "Yes" else "No")
      ret.addProperty("历任车主数量", car.owners.size)

      val jsonArr = new JsonArray
      for (owner <- car.owners)
        jsonArr.add(new JsonPrimitive(owner))

      ret.add("车主 (包含二手车主)", jsonArr)

      ret
    }
  }
}





