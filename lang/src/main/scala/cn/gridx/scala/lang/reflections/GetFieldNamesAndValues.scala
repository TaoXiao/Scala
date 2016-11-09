package cn.gridx.scala.lang.reflections

import java.lang.reflect.Field

import org.joda.time.DateTime

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.tools.scalap.scalax.rules.scalasig.MethodSymbol

/**
  * Created by tao on 8/11/16.
  *
  * http://stackoverflow.com/questions/1226555/case-class-to-map-in-scala
  */
object GetFieldNamesAndValues {
  /** 这个写法不能保证name与value匹配的正确性 */
  def getCCParams(cc: Product): Map[String, Any] = {
    val values = cc.productIterator
    cc.getClass.getDeclaredFields.map(_.getName -> values.next).toMap
  }


  /** 这种方法很好
    * https://www.zybuluo.com/xtccc/note/221183
    * */
  def mapFields[T: ClassTag](clz: Class[T], obj: T): Array[(String, String)] = {
    val fields: Array[Field] = clz.getDeclaredFields
    val fieldMap: Array[(String, String)] =
      for (field <- fields) yield {
        field.setAccessible(true)
        field.getName  -> field.get(obj).asInstanceOf[String]
      }
    fieldMap
  }

  def main(args: Array[String]): Unit = {
    println(new DateTime(2145916800000L))
    val car = Car(null, null)
    // val params = getCCParams(car)
    // 输出 Map(brand -> Honda, price -> 200,000)
    // println(params)

    val result = mapFields(classOf[Car], car)
    result.foreach(println)
  }
}


case class Car(brand: String, price: String)
