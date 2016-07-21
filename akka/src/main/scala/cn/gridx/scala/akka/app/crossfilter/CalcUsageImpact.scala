package cn.gridx.scala.akka.app.crossfilter

import java.io.PrintWriter

import scala.io.Source

/**
  * Created by tao on 7/20/16.
  */
object CalcUsageImpact {
  def TokenSep = ","

  def main(args: Array[String]): Unit = {
    calcByDiff(args(0), args(1), null)
  }

  /**
    * 按照绝对值差额进行usage impact的计算
    *
    * 输入形如:
    *  9541774782:6682884447,opt:da:N,opt:cca:N,opt:mb:N,opt:fera:N,opt:ee:N,opt:nems:N,opt:sc:N,opt:sr:N,opt:care:Y,opt:tbs:N,dim:ETOUA:1530.00,dim:E1:1430.00,dim:ETOUB:1485.00
    *
    * `dim1`和`dim2`分别是要计算的目标dimension
    * 例如, 如果dim1 = ETOUA, dim2 = ETOUB, 则我们要计算出`ETOUA - ETOUB`的结果
    *      如果dim1 = ETOUC, dim2 = ETOUC, 由于上面的这条数据中没有ETOUC属性, 所以该记录丢弃不输出
    *
    * */
  def calcByDiff(inPath: String, outPath: String, targetDimNames: List[(String, String)]): Unit = {
    val writer = new PrintWriter(outPath)

    for (line <- Source.fromFile(inPath).getLines()) {
      val tokens = line.split(TokenSep)
      // dimension : Map[dimName -> dimValue]
      val dimension: Map[String, Double] = tokens.filter(_.startsWith("dim")).map(_.split(":")).map(t => (t(1), t(2).toDouble)).toMap
      // reserved : 需要原样保留并输出到目标文件
      val reserved  = tokens.filter(!_.startsWith("dim"))
      var result = reserved.mkString(TokenSep)

      // 我们需要为dimNamePairs中的每一个dimension pair计算其diff
      var dimNamePairs: Seq[(String, String)] = null

      // 如果targetDimNames中存在有效内容(targetDimNames != null 且 !targetDimNames.isEmpty)
      if (null != targetDimNames &&  !targetDimNames.isEmpty)
        dimNamePairs = targetDimNames.filter(p => dimension.contains(p._1) && dimension.contains(p._2))
      // 则为每一行数据的所有维度生成全部的两两组合
      else {
        val dimArr = dimension.keySet.toArray
        dimNamePairs =
          for (i <- 0 until dimArr.size ; j <- 0 until dimArr.size if i != j) yield
            (dimArr(i), dimArr(j))
      }

      // 开始计算diff
      for (p <- dimNamePairs)
        result += s",dim:${p._1}-${p._2}:${dimension.get(p._1).get - dimension.get(p._2).get}"

      writer.println(result)
    }

    writer.close()
  }
}
