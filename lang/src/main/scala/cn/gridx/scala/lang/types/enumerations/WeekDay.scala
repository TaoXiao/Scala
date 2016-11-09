package cn.gridx.scala.lang.types.enumerations

/**
  * Created by tao on 7/30/16.
  */
object WeekDay extends Enumeration {
  type WeekDay = Value
  val Mon = Value(0, "Monday")
  val Tue = Value(1, "Tuesday")
  val Wed = Value(2, "Wednesday")
  val Thu = Value(3, "Tuesday")
  val Fri = Value(4, "Friday")
  val Sat = Value(5, "Saturday")
  val Sun = Value(6, "Sunday")
}
