package cn.gridx.scala.akka.app.crossfilter

import java.util

import scala.collection.mutable
import scala.io.Source


/**
  * Created by tao on 6/12/16.
  */
class CrossFilter {
  private var source: Array[Record] = _


  /**
    * 开始加载文件中的部分数据
    *
    * @param path  - 文件的本地路径
    * @param start - 开始加载的数据的行数(从第0行开始计算)
    * @param range - 从第start行开始往后加载多少行
    * */
  def LoadSourceData(path: String, start: Int, range: Int): Unit = {
    source = new Array[Record](range)

    var count = 0
    for (line <- Source.fromFile(path).getLines()) {
      if (count >= start + range) // 后面的部分超出了指定的范围
        return

      if (count >= start && count < start + range) {
        source(count - start) = ProcessOneLine(line)
      }
      count += 1
    }
  }

  /**
    *
    * 本方法假定, 由若干个options,
    * 当options条件改变时, 计算唯一的dimension在指定统计区间上的分布
    *
    * @param optFilter - 针对option的filter条件
    * @param dimFilter - 针对dim的filter条件
    * @param targetDimIntervals - 目标维度数据在预置的区间中是怎样分布的, dimIntervals中每一个维度的intervals都是一个有序数组(从小到大)
    *                       对于每一个目标维度的interval:
    *                          在数轴上的形式为  a____b____c____d____e____f____g
    *                          即, 我们要求出, 目标维度上有多少个样本值小于a, 多少个在[a, b)之间,
    *                          多少个在[b, c)之间, ... , 多少个大于g
    * @return  mutable.HashMap[String, util.TreeMap[Int, Int]- 计算出的结果
    *          对于每一个维度: 第-1个值表示值小于a的样本数,第0个值表示值在[a, b)之间的样本数,
    *                        第1个值表示值在[b, c)之间的样本数, ..., 第N个值表示值大于g的样本数,
    *                        所以, 返回值数组的长度 = 对应的维度的interval数组长度 + 1
    *                        注: key从-1开始计数
    *
    *         `mutable.HashMap[String, util.HashMap[String, Int]]` - 每一个options上各个value的分布
    *
    * */
  def doFiltering(optFilter: mutable.HashMap[String, String],
                  dimFilter: mutable.HashMap[String, mutable.HashMap[String, Float]],
                  targetOptions: Array[String],
                  targetDimIntervals: mutable.HashMap[String, Array[Float]]) // 可以是部分的dimension
  : (util.HashMap[String, util.TreeMap[Int, Int]],
    util.HashMap[String, util.HashMap[String, Int]])= {

    // 最后要返回的结果
    val dimDistributions = new util.HashMap[String, util.TreeMap[Int, Int]]()
    for (dimName <- targetDimIntervals.keySet) {
      val distri = new util.TreeMap[Int, Int]()
      for (i <- 0 until targetDimIntervals.get(dimName).get.size + 1)
        distri.put(i - 1, 0)
      dimDistributions.put(dimName, distri)
    }

    val optDistributions = new util.HashMap[String, util.HashMap[String, Int]]()
    for (optName <- targetOptions) {
      optDistributions.put(optName, new util.HashMap[String, Int]())
    }


    // 针对每一条数据进行分析
    for (record <- source)
      if (satisfyOpts(record.opts, optFilter) && satisfyDims(record.dims, dimFilter) ) {
        // 计算每一个options的每一个value的分布
        for ((optName, optValue) <- record.opts if targetOptions.contains(optName)) {
          val optDistri = optDistributions.get(optName)
          if (!optDistri.containsKey(optValue))
            optDistri.put(optValue, 1)
          else
            optDistri.put(optValue, optDistri.get(optValue) + 1)
        }

        // 计算每一个维度上每一个interval的分布
        for ((dimName, dimValue) <- record.dims if targetDimIntervals.contains(dimName)) {
          val interval = targetDimIntervals.get(dimName).get
          val pos = calcDistribution(dimValue, interval)
          val dimDistri = dimDistributions.get(dimName)
          if (!dimDistri.containsKey(pos))
            dimDistri.put(pos, 1)
          else
            dimDistri.put(pos, dimDistri.get(pos) + 1)
        }
      }

    (dimDistributions, optDistributions)
  }


  /**
    * 判定实际的options是否满足给定的条件
    * 如果optFilter中不含某个optionName, 则表示在realOptions中对这个option没有任何要求
    *
    * 对于optFilter中的每一个option, 要求 1) realOptions含有该option name  2) realOptions中该option的value满足条件
    * */
  def satisfyOpts(realOptions: mutable.HashMap[String, String], optFilter: mutable.HashMap[String, String]): Boolean = {
    if (null == optFilter || optFilter.isEmpty)
      return true

    for ((condName, condValue) <- optFilter) {
      if (!realOptions.contains(condName) || !realOptions.get(condName).get.equals(condValue))
        return false
    }

    return true
  }



  /**
    * 判定实际的dims是否满足给定的条件
    *
    * */
  def satisfyDims(realDims: mutable.HashMap[String, Float], dimFilter: mutable.HashMap[String, mutable.HashMap[String, Float]]): Boolean = {
    if (null == dimFilter || dimFilter.isEmpty)
      return true

    for ((k, v) <- dimFilter) {
      val dimName = k
      val min = v.get("min").get
      val max = v.get("max").get

      if (realDims.contains(dimName) && (realDims.get(dimName).get < min || realDims.get(dimName).get > max))
        return false
    }

    return true
  }


  /**
    * 计算目标值x在给定的统计区间上的分布情况
    *
    * @param x  - 待计算的目标值
    * @param interval - 给定的统计区间
    *
    *   a____b____c____d____e____f____g
    *   0    1    2    3    4    5    6
    * @return 返回值 - 计算出x应该位于哪个区间中
    *        如果 x < a, 则返回-1
    *        如果 c <= x < d, 则返回2
    *        如果 x >= g, 则返回 6
    * */
  def calcDistribution(x: Float, interval: Array[Float]): Int = {
    if (x < interval(0))
      return -1

    if (x >= interval(interval.size - 1))
      return interval.size - 1

    for (i <- 0 until interval.size - 1) {
      if (x >= interval(i) && x < interval(i+1))
        return i
    }

    throw new RuntimeException("错误的返回值")
    return -2 // 绝不应该出现
  }


  /**
    * process one line from source data file
    * */
  def ProcessOneLine(line: String): Record = {
    var key = "unknown"
    val optMap = mutable.HashMap[String, String]()
    val dimMap = mutable.HashMap[String, Float]()

    for (token <- line.split(",")) {
      if (token.startsWith("opt"))
        ParseOpt(token, optMap)
      else if (token.startsWith("dim"))
        ParseDim(token, dimMap)
      else
        key = token
    }

    Record(key, optMap, dimMap)
  }

  /**
    * Parse a given option in one line
    *
    * @param s - like the form : "opt:opt_name:opt_value"
    *
    * */
  def ParseOpt(s: String, optMap: mutable.HashMap[String, String]) = {
    val parts = s.split(":", 3)
    optMap.put(parts(1), parts(2))
  }


  /**
    * Parse a given dimension in one line
    *
    * @param s - like the form of "dim:dim_name:dim_value"
    *
    * */
  def ParseDim(s: String, dimMap: mutable.HashMap[String, Float]) = {
    val parts = s.split(":", 3)
    dimMap.put(parts(1), parts(2).toFloat)
  }

}

case class Record(key: String, opts: mutable.HashMap[String, String], dims: mutable.HashMap[String, Float])


/*
object CrossFilter {
  val handler = new CrossFilter()

  def main(args: Array[String]): Unit = {
    handler.LoadSourceData(args(0))
    // handler.LoadSourceData("/Users/tao/IdeaProjects/Scala/lang/data_1.txt")

    // options的限制条件
    val optFilter =  mutable.HashMap[String, String]()
    optFilter.put("care", "Y")
    optFilter.put("fera", "Y")

    // dimension的限制条件
    val dimFilter = mutable.HashMap[String, mutable.HashMap[String, Float]]()
    dimFilter.put("E1", mutable.HashMap[String, Float]("min" -> 300f, "max" -> 900f))

    // 目标dimension的阈值区间
    val dimIntervals = mutable.HashMap[String, Array[Float]]()
    val interval_E1 = new Array[Float](10)
    for (i <- 0 until 10)
      interval_E1(i) = 150 * i
    dimIntervals.put("E1", interval_E1)


    // 目标options
    val targetOptions = Array[String]("care", "mb", "da", "cca", "tbs")
    val (dimDistribution, optDistributions) = handler.doFiltering(optFilter, dimFilter, targetOptions, dimIntervals)

    println(s"${new Date()} 计算完毕")
    println(dimDistribution)
    println(optDistributions)

  }
}

*/