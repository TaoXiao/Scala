package cn.gridx.scala.lang.implicits.classes

import cn.gridx.java.lang.context.CalculationContext
import org.joda.time.DateTimeZone

/**
  * Created by tao on 12/7/16.
  */

class Car(implicit ctx: CalculationContext) {
  println(s"ctx = $ctx")
  val v = ctx.getOptionString("key-1")
  println(v)
}


object Test extends App {
  val tz = DateTimeZone.forID("America/Los_Angeles")
  implicit val ctx = new CalculationContext(tz, null, false)
  ctx.setOption("key-1", "value-1")

  val car = new Car()
}
