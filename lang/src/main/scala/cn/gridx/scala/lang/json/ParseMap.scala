package cn.gridx.scala.lang.json

import com.codahale.jerkson.Json
import com.google.gson.{GsonBuilder, JsonParser}


/**
  * Created by tao on 11/12/16.
  */
object ParseMap {
  val GSon = new GsonBuilder().setPrettyPrinting.create
  val JP: JsonParser      = new JsonParser

  def generateJson(result:Any ,beautify: Boolean = true):String={
    val json = Json.generate(result) //gson.toJson(result)
    val t = beautify match {
      case true => GSon.toJson(JP.parse(json))
      case _ => json
    }
    t
  }

  def main(args: Array[String]): Unit = {
    val map = Map("A" -> "a", "B" -> "B")
    val a = A("Hello", 100)
    val str = """   akkaname            = null
                |   configName          = PGE_Stage
                |   meterAccount        = http://10.100.33.59:8025/calculate?fkey=ML_pge_2016_0512&writeBills=true&flexibleAccount=true&filter=year:2015,2016%3Bmeteraccount:s3//gridx-rule-debug/PGE/AG_50k.csv&batchOptions=mysql:3%3Btruncate:1%3Bcalibration:1%3BbatchId:ping_test
                |   startDate           = 2011-02-15
                |   endDate             = null
                |   meterSource         = ML_pge_2016_0512
                |   debug               = 0
                |   alternativeContract = null
                |   timezone            = None
                |   localMode           = false
                |   blocking            = false
                |   redisCache          = None
                |   flexibleAccount     = Some(true)
                |   writeBill           = Some(true)
                |   writeIcost          = None
                |   virtualMeter        = null
                |   filters             = year:2015,2016;meteraccount:s3//gridx-rule-debug/PGE/AG_50k.csv
                |   batchOptions        = mysql:3;truncate:1;calibration:1;batchId:ping_test
                |   computeInfo         = null
                |   writeDB             = false""".stripMargin
    val obj = Map("received" -> a)
    val json = generateJson(obj, true)

    println(json)
  }

}


final case class A(a1: String, a2: Int) {

}
