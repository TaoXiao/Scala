package cn.gridx.scala.akka.app.crossfilter

import scala.util.Random

/**
  * Created by tao on 7/27/16.
  *
  * 用于运行OPAQ算法的工具类
  *
  * OPAQ算法参考 https://www.zybuluo.com/xtccc/note/294664
  *
  */
object OPAQ {
  /**
    * 为长度为m的某个run生成个sample point list
    *
    * 将数组A[start, end]分为若干个partitions, 并保证第i个partition中的任意数据都不大于第i+1个partition中的任意数据。
    * 此外,还要为第i个partition生成一个代表s, 并保证s不小于该partition中的任意数据, 以及s不大于第i+1个partition中的任意数据
    *
    * 例如，如果两个sub-list为 L1 = {3, 5, 7, 7} 以及 L2 = {8, 9, 10, 11}，
    * 则可以选择 s1 = 7, s2 = 11；也可以选择 s1 = 7.5, s2 = 11; 还可以选择 s1 = 8, s2 = 11.6 。
    *
    * 本算法为了简单起见, 将每一个partition中的最大值作为该partition的代表
    *
    * 我们尽量让每一个partitions的长度一致
    *
    *
    * 例如: 原始的数据集为S =  {1, 5, 3, 2, 4, 4, 9, 0, 5}, minParSize = 2
    *
    * 那么首先, 它分为 S = P1 + P2 = {0, 1, 2, 3}) + {4, 4, 5, 5, 9}
    * 接着  P1 = P11 + P12 = {0, 1} + {2, 3},   P2 = P21 + P22 = {4, 4} + {5, 5, 9}
    * P11, P12, P21 不能继续划分, P22 = P221 + P222 = {5} + {5, 9}
    *
    * 因此, 最终S = {0, 1} + {2, 3} + {4, 4} + {5} + {5, 9}
    * 且它们的代表分别为 1, 3, 4, 5, 9
    *
    * @param sampleListMinSize - sample list的最小长度
    * @param run - 本地数据数组, 不保证是有序的
    *
    * @return 所有sample points的数值
    * */
  def genSamples(sampleListMinSize:Int, run: Array[Double], start: Int, end: Int): List[Double] = {
    if (end < start)
      throw new RuntimeException("Error ")

    if (end - start + 1 <= sampleListMinSize)
      return List(Max(run, start, end)._2)

    val medianIdx = select(run, start, end, (end - start + 1)/2)._1 // 找出run的中位数

    genSamples(sampleListMinSize, run, start, medianIdx) ++ genSamples(sampleListMinSize, run, medianIdx + 1, end)
  }


  /**
    *
    * @param A - 原始的数据集
    * @param start - 数据的开始index(包含)
    * @param end - 数据的终止index(包含)
    * @param pivotIdx - 进行比较的基准元素的位置
    *
    * @return - 经过partition之后, 数组A[start, end]将被划分为两个区间, 返回的结果是目标pivot在最终数组A[start, end]中的位置和值
    *
    * */
  def partition(A: Array[Double], start: Int, end: Int, pivotIdx: Int): (Int, Double) = {
    if (end - start + 1 < 2)
      throw new RuntimeException("Can not partition array with size shorter than 2")

    val pivotValue = A(pivotIdx)

    swap(A, pivotIdx, end)

    var q = start
    for (i <- start until end) {
      if (A(i) < pivotValue) {
        swap(A, i, q)
        q += 1
      }
    }

    swap(A, q, end)

    (q, A(q))
  }


  /**
    * 从数组A[start, end]中寻找出第n大的数字(n从1开始), 并且该数字左边的任意元素都不大于右边的任意元素
    * A中的内容的次序会改变
    *
    * 例如, A = (10, 2, 11, 21, 33, 5, 3, 4),
    * 调用select(A, 0, s.size - 1, 4)返回的结果是(3, 5.0)
    * 此时A的内容为(2.0  4.0  3.0  5.0  10.0  21.0  11.0  33.0)
    *
    * */
  def select(A: Array[Double], start: Int, end: Int, n: Int): (Int, Double) = {
      if (n < 1 || n > end - start + 1)
        throw new RuntimeException(s"error n = $n")

      if (start == end)
        return (start, A(start))

      val pivotIdx = partition(A, start, end, start + Random.nextInt(end - start + 1))._1
      if (pivotIdx == n + start - 1) {
        val result = n + start - 1
        return (result, A(result))
      }
      else if (pivotIdx < n + start - 1) {
        val result = select(A, pivotIdx + 1, end, n - (pivotIdx - start + 1))._1
        return (result, A(result))
      }
      else {
        val result = select(A, start, pivotIdx, n)._1
        return (result, A(result))
      }
   }




  /**
    * 交换数组A中位置i和位置j的数据
    * */
  def swap[T](A: Array[T], i: Int, j: Int): Unit = {
    val tmp: T = A(i)
    A(i) = A(j)
    A(j) = tmp
  }


  /**
    * 找出数组A[start, end]之间的最大值
    *
    * 返回最大值的index及value
    * */
  private def Max(A: Array[Double], start: Int, end: Int): (Int, Double) = {
    if (start > end || start < 0 || end >= A.size)
      throw new RuntimeException(s"Error: array size = ${A.size}, start = $start, end = $end ")

    var max = (start, A(start))

    for (i <- start + 1 to end)
      if (max._2 < A(i))
        max = (i, A(i))

    max
  }
}
