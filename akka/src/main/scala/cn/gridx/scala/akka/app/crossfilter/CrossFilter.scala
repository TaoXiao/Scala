package cn.gridx.scala.akka.app.crossfilter

import org.slf4j.LoggerFactory

import scala.collection.immutable.TreeMap
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source


/**
  * Created by tao on 6/12/16.
  */
class CrossFilter {
  val logger = LoggerFactory.getLogger(this.getClass())
  private var source: Array[Record] = null


  def GetSourceData() = source


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
    * 本方法假定, 存在若干个options,
    * 当options条件改变时, 计算唯一的dimension在指定统计区间上的分布
    *
    * @param optFilter - 针对option的filter条件
    * @param dimFilter - 针对dim的filter条件
    * @param targetDimensions - 目标维度数据在预置的区间中是怎样分布的, dimIntervals中每一个维度的intervals都是一个有序数组(从小到大)
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
    *         `mutable.HashMap[String, util.HashMap[String, Int]]` - 这每一个options上各个value的分布
    *         `mutable.HashMap[String, ArrayBuffer[Double]]` - 这是经过过滤后的所有dimensions数据, 以便于当sorted poppulation histogram需要时使用
    *
    * */
  def doFiltering(optFilter: mutable.HashMap[String, String],
                  dimFilter: mutable.HashMap[String, mutable.HashMap[String, Double]],
                  targetOptions: Array[String],
                  targetDimensions: mutable.HashMap[String, Array[Double]])
  : (mutable.HashMap[String, TreeMap[Int, Int]],
    mutable.HashMap[String, mutable.HashMap[String, Int]],
    mutable.HashMap[String, ArrayBuffer[Double]])
  = {
    // 最后要返回的结果
    val dimDistributions = new mutable.HashMap[String, TreeMap[Int, Int]]()
    for (dimName <- targetDimensions.keySet) {
      var distri = TreeMap[Int, Int]()
      for (i <- 0 until targetDimensions.get(dimName).get.size + 1)
        distri += ((i - 1) -> 0)
      dimDistributions.put(dimName, distri)
    }

    val optDistributions = new mutable.HashMap[String, mutable.HashMap[String, Int]]()
    for (optName <- targetOptions) {
      optDistributions.put(optName, mutable.HashMap[String, Int]())
    }

    // 将source中经过过滤后的所有dimensions存储在filteredDimensionData内, 以便于当sorted poppulation histogram需要时使用
    val filteredDimensions = mutable.HashMap[String, ArrayBuffer[Double]]()

    // 针对每一条数据进行分析
    for (record <- source if satisfyOpts(record.opts, optFilter) && satisfyDims(record.dims, dimFilter) ) {
      // 计算每一个target option的分布情况
      for ((optName, optValue) <- record.opts if targetOptions.contains(optName)) {
        val optDistri = optDistributions.get(optName).get
        if (!optDistri.contains(optValue))
          optDistri.put(optValue, 1)
        else
          optDistri.put(optValue, optDistri.get(optValue).get + 1)
      }

      // 计算每一个维度上每一个interval的分布
      for ((dimName, dimValue) <- record.dims) {
        // 现在, 每一个record都通过了cross filter的条件, 我们要将其dimension data放入到`filteredDimensions`中
        if (!filteredDimensions.contains(dimName))
          filteredDimensions.put(dimName, ArrayBuffer[Double]())
        filteredDimensions.get(dimName).get.append(dimValue)

        // 再计算每一个target dimension的分布情况
        if (targetDimensions.contains(dimName)) {
          val interval: Array[Double] = targetDimensions.get(dimName).get
          val pos = calcDistribution(dimValue, interval)
          var dimDistri: TreeMap[Int, Int] = dimDistributions.get(dimName).get
          if (!dimDistri.contains(pos))
            dimDistri += (pos -> 1)
          else {
            val newValue = dimDistri.get(pos).get + 1
            dimDistri -= pos
            dimDistri += (pos -> newValue)
          }
          dimDistributions.put(dimName, dimDistri)
        }
      }
    }

    (dimDistributions, optDistributions, filteredDimensions)
  }


  /**
    * 判定实际的options是否满足给定的条件
    * 如果optFilter中不含某个optionName, 则表示在realOptions中对这个option没有任何要求
    *
    * 对于optFilter中的每一个option, 要求 a).realOptions含有该option name ; b).realOptions中该option的value满足条件
    * */
  private def satisfyOpts(realOptions: mutable.HashMap[String, String], optFilter: mutable.HashMap[String, String]): Boolean = {
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
  def satisfyDims(realDims: mutable.HashMap[String, Double], dimFilter: mutable.HashMap[String, mutable.HashMap[String, Double]]): Boolean = {
    if (null == dimFilter || dimFilter.isEmpty)
      return true

    for ((k, v) <- dimFilter) {
      val dimName = k
      val min = v.get("min").get
      val max = v.get("max").get

      if (realDims.contains(dimName) && (realDims.get(dimName).get < min || realDims.get(dimName).get >= max))
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
  def calcDistribution(x: Double, interval: Array[Double]): Int = {
    if (x < interval(0))
      return -1

    if (x >= interval(interval.size - 1))
      return interval.size - 1

    for (i <- 0 until interval.size - 1) {
      if (x >= interval(i) && x < interval(i+1))
        return i
    }

    throw new RuntimeException("错误的返回值")
  }


  /**
    * process one line from source data file
    * */
  private def ProcessOneLine(line: String): Record = {
    var key = "unknown"
    val optMap = mutable.HashMap[String, String]()
    val dimMap = mutable.HashMap[String, Double]()

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
  private def ParseOpt(s: String, optMap: mutable.HashMap[String, String]) = {
    val parts = s.split(":", 3)
    optMap.put(parts(1), parts(2))
  }


  /**
    * Parse a given dimension in one line
    *
    * @param s - like the form of "dim:dim_name:dim_value"
    *
    * */
  private def ParseDim(s: String, dimMap: mutable.HashMap[String, Double]) = {
    val parts = s.split(":", 3)
    dimMap.put(parts(1), parts(2).toDouble)
  }

}

case class Record(key: String, opts: mutable.HashMap[String, String], dims: mutable.HashMap[String, Double])



/*
object CrossFilter {
  val handler = new CrossFilter()

  def main(args: Array[String]): Unit = {
    println(s"${new Date()} 开始加载数据")
    var ts = System.currentTimeMillis()
    handler.LoadSourceData("/Users/tao/Documents/data_1.txt", 0, 3651707)
    println(s"${new Date()} 加载数据完毕, 耗时 ${(System.currentTimeMillis() - ts)/1000} 秒")

    // options的限制条件
    val optFilter =  mutable.HashMap[String, String]()
    optFilter.put("care", "Y")
    optFilter.put("fera", "Y")

    // dimension的限制条件
    val dimFilter = mutable.HashMap[String, mutable.HashMap[String, Double]]()
    dimFilter.put("E1", mutable.HashMap[String, Double]("min" -> 300f, "max" -> 900f))

    // 目标dimension的阈值区间
    val dimIntervals = mutable.HashMap[String, Array[Double]]()
    val interval_E1 = new Array[Double](10)
    for (i <- 0 until 10)
      interval_E1(i) = 150 * i
    dimIntervals.put("E1", interval_E1)


    // 目标options
    val targetOptions = Array[String]("care", "mb", "da", "cca", "tbs")

    println(s"${new Date()} 开始分析")
    ts = System.currentTimeMillis()
    val (dimDistribution, optDistributions) = handler.doFiltering(optFilter, dimFilter, targetOptions, dimIntervals)
    println(s"${new Date()} 数据分析完毕, 耗时 ${(System.currentTimeMillis() - ts)/1000} 秒")

    println(s"${new Date()} 计算完毕")
    println(dimDistribution)
    println(optDistributions)

  }
}
*/

