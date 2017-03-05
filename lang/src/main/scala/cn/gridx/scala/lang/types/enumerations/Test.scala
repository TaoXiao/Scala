package cn.gridx.scala.lang.types.enumerations

import cn.gridx.scala.lang.types.enumerations.WeekDay.WeekDay

/**
  * Created by tao on 7/30/16.
  */
object Test {
  def main(args: Array[String]) {
    val x = MT(WeekDay.Fri)
    x.day match {
      case WeekDay.Mon => println("Monday")
      case WeekDay.Tue => println("Tuesday")
      case WeekDay.Wed => println("Wednesday")
      case WeekDay.Thu => println("Thursday")
      case WeekDay.Fri => println("Friday")
      case WeekDay.Sat => println("Saturday")
      case WeekDay.Sun => println("Sunday")
      case _ => print("unknown")
    }

    println(isWorkingDay(x.day))

    println(x.day)

    println(WeekDay.withName("Sunday"))
  }


  def isWorkingDay(d: WeekDay): Boolean = ! (d == WeekDay.Sat || d == WeekDay.Sun)
}

final case class MT(day: WeekDay)
