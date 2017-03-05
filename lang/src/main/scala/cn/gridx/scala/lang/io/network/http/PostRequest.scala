package cn.gridx.scala.lang.io.network.http

import scalaj.http.{Http, HttpOptions}

/**
  * Created by tao on 11/22/16.
  */
object PostRequest extends App {
  val json = """{
               |	"userName"		: "tao",
               |	"region" 		: "new-region",
               |	"price"			: 0.1,
               |	"slaveNodes" 	: 3,
               |	"instanceType" 	: "c3.xlarge",
               |	"amiID"			: "AMI-ID",
               |	"launchGroup" 	: "akka_spark",
               |	"securityGroup"	: "d",
               |	"subnetID"		:	"e"
               |}""".stripMargin
  val result = Http("http://127.0.0.1:4021/v1/requestSparkCluster")
    .postData(json)
    .header("Content-Type", "application/json")
    .header("Charset", "UTF-8")
    .option(HttpOptions.readTimeout(10000))
    .asString

  println(result)
}
