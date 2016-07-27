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
case class AnalysisResult(dimDistributions: mutable.HashMap[String, TreeMap[Int, Int]],
                          optDistributions: mutable.HashMap[String, mutable.HashMap[String, Int]])






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

  def map2String[A, B](map: mutable.HashMap[A, B], indent: String): String = {
    val arr =
      for ((k, v) <- map) yield
        s"""${indent}$k -> $v"""

    arr.mkString("\n")
  }
}


/**
  * 查询sorted population histogram时的查询参数
  *
  * 查询过程:
  *   1) 根据 optFilter 及 dimFilter 过滤掉不满足要求的数据
  *   2) 对剩余的数据进行排序, 排序的目标维度是`targetDim`, 按照降序排列
  *   3) 从排序后的数据中均匀地取出`sampleSize`个样本
  *
  * 返回结果:
  *   返回`sampleSize`个样本, 每个样本的结构包括: 该样本在排序后数据中的位置, 以及该样本在`targetDim`维度上的值
  *
  * */
final case class PopHistParam(optFilter: mutable.HashMap[String, String],
                              dimFilter: mutable.HashMap[String, mutable.HashMap[String, Double]],
                              targetDim: String, sampleSize: Int)




