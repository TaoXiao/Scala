package cn.gridx.log4j2.examples.basic

import org.slf4j.LoggerFactory

/**
  * Created by tao on 6/11/16.
  *
  * 使用 Slf4j, 但是让slf4将日志route到log4j2
  */
object UseSLF4JAPI {
  def main(args: Array[String]): Unit = {
    val logger  = LoggerFactory.getLogger(this.getClass)
    logger.info("hello , i am slf4j")
    logger.error("hi, i am slf4j")
  }
}
