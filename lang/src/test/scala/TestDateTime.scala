import org.joda.time.{Days, DateTime}

/**
  * Created by tao on 1/12/17.
  */
object TestDateTime {
  def main(args: Array[String]): Unit = {
    println(testIntervalDays("2016-11-01", "2017-01-10"))
    //println(minusDays("2017-01-10", 365 ))
    // println(testIntervalDays("2016-01-01T00:00:00.000-08:00", "2016-01-02T00:00:00.000-08:00"))

  }


  def testIntervalDays(d1: String, d2: String): Int = {
    val day1 = DateTime.parse(d1)
    val day2 = DateTime.parse(d2)
    Days.daysBetween(day1, day2).getDays
  }


  def minusDays(d: String, delta: Int): DateTime = {
    val day = DateTime.parse(d)
    day.minusDays(delta)
  }
}
