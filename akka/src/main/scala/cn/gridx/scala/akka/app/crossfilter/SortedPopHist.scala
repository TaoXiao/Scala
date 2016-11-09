package cn.gridx.scala.akka.app.crossfilter

import cn.gridx.scala.akka.app.crossfilter.SortedPopHist.PopHistDataSource.PopHistDataSource
import com.google.gson.GsonBuilder

import scala.collection.mutable

/**
  * Created by tao on 7/29/16.
  *
  * 将与生成 sorted population histogram 相关的业务逻辑代码都放在这里
  */
object SortedPopHist {
  /** 一个quantile的数据结构 */
  final case class Quantile(rank: Int, value: Double)


  /**
    * 要求master开始计算 sorted population histogram
    * 计算的基础是总体的数据, 或者是由最近的cross filter条件限定的
    * 计算的对象是`targetDimName`
    * */
  final case class MSG_SM_QUERY_POPHIST(popHistParam: PopHistParam)


  /*
  * master将各个worker的中间结果汇总后, 计算出的最终的sorted population histogram的结果, 结构为
    {
      "succeed": true,
      "msg": "计算成功",
      "seconds": 1.3,
      "quantiles" : [
        {"rank": 9000, "value": 19877.4},
        {"rank": 8000, "value": 11877.4},
        {"rank": 7000, "value": 5877.24},
        {"rank": 6000, "value": 5800.24},
        {"rank": 5000, "value": 877.304}
      ]
    }
  * */
  final case class MasterResult(succeed: Boolean, msg: String, seconds: Long,
                                quantiles: List[Quantile]) {
    def toJson() = ""
  }



  /*
    * 解析要求生成sorted population histogram时的post参数
      只有当source = 2时, 参数optionFilter和dimensionFilter才是有效的, 否则忽略它们

      {
        "dataSource" : 2,
        "targetDimName" : "ETOUA-ETOUB",
        "quantileNum": 500，
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
        }
      }
    * */
  def parseQueryPayload(payload: String): PopHistParam = {
    val gson = new GsonBuilder().serializeNulls().create()
    val javaParam = gson.fromJson(payload, classOf[PopHistJavaParam])
    javaParam.toPopHistParam()
  }


  /**
    * 为了方便GSON的支持,需要一个临时的Java collection来转换
    * */
  final case class PopHistJavaParam(dataSource: String, targetDimName: String, quantileNum: Int,
                                    optionFilter: java.util.HashMap[String, String],
                                    dimensionFilter: java.util.HashMap[String, java.util.HashMap[String, Double]])
  {
    def toPopHistParam(): PopHistParam = {
      val scalaOptFilter = Commons.JavaMap2ScalaMap(optionFilter)

      val scalaDimensionFilter = mutable.HashMap[String, mutable.HashMap[String, Double]]()
      val kIt = dimensionFilter.keySet().iterator()
      while (kIt.hasNext) {
        val key = kIt.next()
        val value = dimensionFilter.get(key)
        scalaDimensionFilter.put(key, Commons.JavaMap2ScalaMap(value))
      }

      PopHistParam(dataSource match {
        case "AllData" => PopHistDataSource.AllData
        case "ProvidedFilter" => PopHistDataSource.ProvidedFilter
        case "UseExistingFilter" => PopHistDataSource.UseExistingFilter
        case _ => throw new RuntimeException("Error, 非法的dataSource")
      }, targetDimName, quantileNum, scalaOptFilter, scalaDimensionFilter)
    }
  }


  // master要求worker开始计算local samples时发送的消息
  final case class MSG_MW_QUERY_POPHIST(param: PopHistParam)


  /**
    * 当与cross filter查询共同使用时, 可以使用本class作为要求查询sorted population histogram的参数
    * @param targetDimName - 要展示的目标维度名称
    * @param quantilesNum - 要求计算出多少个quantiles
    * @param dataSource - 基础数据来源, 必须是 `PopHistDataSource`中的某一个值
    *                   只有当dataSource == PopHistDataSource.ProvideFilter时, 下面的两个参数才有效, 否则忽略它们
    * @param optFilter - 提供的过滤条件
    * @param dimFilter - 提供的过滤条件
    *
    * */
  final case class PopHistParam(dataSource: PopHistDataSource, targetDimName: String, quantilesNum: Int,
                                optFilter: mutable.HashMap[String, String],
                                dimFilter: mutable.HashMap[String, mutable.HashMap[String, Double]]) {
    override def toString(): String = {
      var msg = s"targetDimName = $targetDimName\n"
      msg += s"quantilesNum = $quantilesNum\n"
      msg += s"dataSource = $dataSource\n"

      msg += "optFilter = \n"
      val optFilterArr =
        for ((k, v) <- optFilter) yield
          s"\t$k -> $v"
      msg += optFilterArr.mkString("\n")

      msg += "\ndimFilter = \n"
      val dimFilterArr =
        for ((k, v) <- dimFilter) yield
          s"""\t$k -> \n${map2String(v, "\t\t")}"""
      msg += dimFilterArr.mkString("\n")

      msg += "\n"
      msg
    }


    private def map2String[A, B](map: mutable.HashMap[A, B], indent: String): String = {
      val arr = for ((k, v) <- map) yield
          s"""${indent}$k -> $v"""

      arr.mkString("\n")
    }
  }



  /**
    * 当我们要求为某个dimension计算出它的sorted population histogram时, 源数据可以有三类
    * 1).  全部的原始输入数据, 这表示我们要的是full population histogram;
    * 2).  在参数中提供条件参数, 以满足条件的数据作为源数据;
    * 3).  直接使用最近的cross filter结果作为源数据
    * */
  object PopHistDataSource extends Enumeration{
    type PopHistDataSource = Value
    val AllData, ProvidedFilter, UseExistingFilter = Value
  }

}
