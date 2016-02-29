package cn.gridx.scala.lang.test


/**
  * Created by tao on 2/23/16.
  */
object Test {

  def main(args: Array[String]): Unit = {
    val A = new Array[Float](7)
    A(0) = 0f
    A(1) = 1f
    A(2) = 1f
    A(3) = 1f
    A(4) = 4f
    A(5) = 4f
    A(6) = 6f

    A.slice(0, 7).foreach(println)

    /*
    val ret = findNearest(A, 0, 14.5f)
    if (ret.isDefined)
      println(ret.get._1 + ", " + ret.get._2)
    else
      println("不存在")
      */
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
