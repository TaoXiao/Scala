package cn.gridx.scala.spray.request.send

import org.slf4j.LoggerFactory

/**
  * Created by tao on 11/29/16.
  */
object Processor {
  val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    sendGetRequest
  }

  def sendGetRequest(): Unit = {
    val url = "http://127.0.0.1:9040/get/request"
    logger.info("即将发送请求")
    val resp = scalaj.http.Http(url)
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .asString
    logger.info(resp.toString)
  }
}
