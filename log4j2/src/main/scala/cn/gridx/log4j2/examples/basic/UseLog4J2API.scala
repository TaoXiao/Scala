package cn.gridx.log4j2.examples.basic

import cn.gridx.log4j2.examples.additivity.UseAdditivity
import cn.gridx.log4j2.examples.additivity.packA.Foo
import org.apache.logging.log4j.LogManager

/**
  * Created by tao on 6/11/16.
  *
  * 直接使用 Log4j2的API
  *
  */
object UseLog4J2API {
  def main(args: Array[String]): Unit = {
    val logger = LogManager.getLogger(this.getClass)
    for (i <- 0 until 2) {
      logger.trace("这是 TRACE")
      logger.debug("这是DEBUG")
      logger.info("这是INFO")
      logger.warn("这是WARN")
      logger.error("这是ERROR")
      logger.fatal("这是FATAL")
      logger.info("*"*50)
    }

    UseAdditivity.main(new Array[String](4))
    Foo.main(new Array[String](4))
  }
}
