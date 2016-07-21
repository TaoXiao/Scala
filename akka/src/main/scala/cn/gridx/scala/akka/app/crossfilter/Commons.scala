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


case class CmdParams(actorType: String = "unknown", actorNumber: Int = -1)

case class MSG_SM_STARTCALC(analysisParam: AnalysisParam)


// 每一个worker对于自己数据的计算结果
case class AnalysisResult(dimDistributions: mutable.HashMap[String, TreeMap[Int, Int]],
                          optDistributions: mutable.HashMap[String, mutable.HashMap[String, Int]])


case class CalcResult(succeed: Boolean, msg: String, analysisResult: AnalysisResult) {
  def toJson(): String = {
    val gson: Gson = new GsonBuilder().serializeNulls().create()
    val jObj = new JsonObject()
    jObj.addProperty("succeed", succeed)
    jObj.addProperty("message", msg)

    if (succeed) {
      val jDim = new JsonObject()
      for ((k, v) <- analysisResult.dimDistributions) {
        jDim.add(k, Map2Json(v))
      }

      val jOpt = new JsonObject()
      for ((k, v) <- analysisResult.optDistributions) {
        jOpt.add(k, Map2Json(v))
      }

      jObj.add("dimensions", jDim)
      jObj.add("options", jOpt)
    }

    jObj.toString
  }

  def Map2Json[A, B](map: Map[A, B]): JsonObject = {
    val jMap = new JsonObject()
    for ((k, v) <- map)
      jMap.addProperty(k.toString, v.toString)
    jMap
  }

  def Map2Json[A, B](map: mutable.Map[A, B]): JsonObject = {
    val jMap = new JsonObject()
    for ((k, v) <- map)
      jMap.addProperty(k.toString, v.toString)
    jMap
  }

}


case class MSG_MW_STARTLOAD(path: String, start: Int, range: Int)

case class WM_SM_TEST()

case class MSG_SM_RELOAD(path: String)

case class MSG_START_CALCULATION()


case class MSG_MW_STARTANALYSIS(optFilter: mutable.HashMap[String, String],
                                dimFilter: mutable.HashMap[String, mutable.HashMap[String, Double]],
                                targetOptions: Array[String],
                                dimIntervals: mutable.HashMap[String, Array[Double]])



// 要求worker计算时的查询参数
case class AnalysisParam(optFilter: mutable.HashMap[String, String],
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


case class JavaAnalysisParam(optionFilter: java.util.HashMap[String, String],
                             dimensionFilter: java.util.HashMap[String, java.util.HashMap[String, Double]],
                             targetOptions: Array[String],
                             targetDimensions: java.util.HashMap[String, Array[Double]]) {
  def toAnalysisParam(): AnalysisParam = {
    val scalaOptFilter = Commons.JavaMap2ScalaMap(optionFilter)

    val scalaDimensionFilter = mutable.HashMap[String, mutable.HashMap[String, Double]]()
    var kIt = dimensionFilter.keySet().iterator()
    while (kIt.hasNext) {
      val key = kIt.next()
      val value = dimensionFilter.get(key)
      scalaDimensionFilter.put(key, Commons.JavaMap2ScalaMap(value))
    }

    val scalaTargetDimension = mutable.HashMap[String, Array[Double]]()
    kIt = targetDimensions.keySet().iterator()
    while (kIt.hasNext) {
      val key = kIt.next()
      val value = targetDimensions.get(key)
      scalaTargetDimension.put(key, value)
    }

    AnalysisParam(scalaOptFilter, scalaDimensionFilter, targetOptions, scalaTargetDimension)
  }
}


case class MSG_WM_REGISTER()

case class MSG_WM_LOAD_FINISHED(elapsed: Long)

case class MSG_WM_ANALYSIS_FINISHED(actorPath: ActorPath, elapsed: Long, analysisResult: AnalysisResult)

case class MSG_MS_TEST()