package cn.gridx.scala.lang.test

import java.util
import java.util.Date

import org.joda.time.{DateTime, DateTimeZone}


/**
  * Created by tao on 2/23/16.
  */
object Test {

  def main(args: Array[String]): Unit = {
    val x  = "1"
    println(if (x.equals("1")) true else false)
  }

  /**
    * array是已经按照从小到大的顺序排列好的
    * 找出一个子区间,使得其中的每一个数据x都满足  min <= x <= max
    * */
  def findNearest(array: Array[Float], min: Float, max: Float): Option[(Int, Int)] = {
    var (start, end)  = (0, array.size - 1)

    while (array(start) < min)
      start += 1

    while (array(end) > max)
      end -= 1

    if (start > end)  None
    else Some((start, end))
  }


}
