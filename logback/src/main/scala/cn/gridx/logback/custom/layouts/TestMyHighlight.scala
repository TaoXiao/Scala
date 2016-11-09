package cn.gridx.logback.custom.layouts

import org.slf4j.LoggerFactory

/**
  * Created by tao on 10/9/16.
  */
object TestMyHighlight extends App {
  println("开始")
  val log = LoggerFactory.getLogger(this.getClass)
  println(log)

  var c = 0;
  for (i <- Range(0, 2)) {
    c += 1
    log.debug(s"$c : " + "DEBUG")
    log.info(s"$c : " + "Info")
    log.warn(s"$c : " + "WARN")
    log.error(s"$c : " + "Error")
  }
}
