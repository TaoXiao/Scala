package cn.gridx.scala.akka.app.crossfilter


import akka.actor.ActorPath
import com.google.gson.{Gson, GsonBuilder, JsonObject}

import scala.collection.immutable.TreeMap
import scala.collection.mutable

object Commons  {
  // 将 java.util.HashMap 转换为 scala.collection.mutable.HashMap
  def JavaMap2ScalaMap[A, B](javaMap: java.util.HashMap[A, B]): mutable.HashMap[A, B] = {
    val scalaMap = mutable.HashMap[A, B]()
    val kIt = javaMap.keySet().iterator()
    while (kIt.hasNext) {
      val key = kIt.next()
      val value = javaMap.get(key)
      scalaMap.put(key, value)
    }
    scalaMap
  }
}

/**
  * Created by tao on 7/19/16.
  */
object Roles {
  def MASTER    = "MASTER"
  def WORKER    = "WORKER"
  def LISTENER  = "LISTENER"
  def SPRAY     = "SPRAY"
}




// 每一个worker对于自己数据的计算结果
case class WorkerAnalysisResult(dimDistributions: mutable.HashMap[String, TreeMap[Int, Int]],
                                optDistributions: mutable.HashMap[String, mutable.HashMap[String, Int]])


// 各个worker对于自己的数据计算出的sample list (针对sorted pop histogram需求)
case class WorkerSampleList(list: List[Double])


/**
  * 要求worker计算时的查询参数
  * */
final case class AnalysisParam(optFilter: mutable.HashMap[String, String],
                         dimFilter: mutable.HashMap[String, mutable.HashMap[String, Double]],
                         targetOptions: Array[String],
                         targetDimensions: mutable.HashMap[String, Array[Double]]) {
  override def toString(): String = {
    var msg = "optFilter = \n"
    val optFilterArr =
      for ((k, v) <- optFilter) yield
        s"\t$k -> $v"
    msg += optFilterArr.mkString("\n")

    msg += "\ndimFilter = \n"
    val dimFilterArr =
      for ((k, v) <- dimFilter) yield
        s"""\t$k -> \n${map2String(v, "\t\t")}"""
    msg += dimFilterArr.mkString("\n")

    msg += s"""\ntargetOptions = \n\t ${targetOptions.mkString("[", ", ", "]")}"""

    msg += "\ntargetDimensions = \n"
    val targetDimensionsArr =
      for ((k, v) <- targetDimensions) yield
        s"""\t$k -> ${v.mkString("[", ", ", "]")}"""
    msg += targetDimensionsArr.mkString("\n")

    msg += "\n"
    msg
  }

  private def map2String[A, B](map: mutable.HashMap[A, B], indent: String): String = {
    val arr =
      for ((k, v) <- map) yield
        s"""${indent}$k -> $v"""

    arr.mkString("\n")
  }
}





