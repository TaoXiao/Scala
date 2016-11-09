package cn.gridx.scala.lang.log.backlog

import org.slf4j.LoggerFactory

/**
  * Created by tao on 11/5/16.
  */
object Test {
  def main(args: Array[String]): Unit = {
    val logger = LoggerFactory.getLogger(getClass)
    logger.info("hello")
  }
}
