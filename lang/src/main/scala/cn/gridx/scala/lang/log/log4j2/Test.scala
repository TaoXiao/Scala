package cn.gridx.scala.lang.log.log4j2

import org.slf4j.LoggerFactory

/**
  * Created by tao on 6/28/16.
  */
object Test extends App {
  val logger = LoggerFactory.getLogger(this.getClass)

  logger.info("hello")
  logger.error("bye")

}
