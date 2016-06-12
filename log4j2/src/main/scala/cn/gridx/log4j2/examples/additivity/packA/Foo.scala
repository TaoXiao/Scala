package cn.gridx.log4j2.examples.additivity.packA

import org.apache.logging.log4j.LogManager

/**
  * Created by tao on 6/11/16.
  */
object Foo {
  val logger = LogManager.getLogger()

  def main(args: Array[String]): Unit = {
    for (i <- 0 until 3) {
      logger.trace("This is TRACE")
      logger.debug("This is DEBUG")
      logger.info("This is INFO")
      logger.warn("This is WARN")
      logger.error("This is ERROR")
      logger.fatal("This is FATAL")
    }
  }
}
