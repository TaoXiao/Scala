package cn.gridx.spark.mllib.basics

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.random.RandomRDDs
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.{Matrix, Vectors, Vector}
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}

/**
 * Created by tao on 11/30/15.
 */
object StatisticsExample {
    def main(args: Array[String]): Unit = {
        Logger.getRootLogger.setLevel(Level.ERROR)
        val sc = new SparkContext(new SparkConf())
        // Test_ColumnStatistics(sc)
        //Test_Correlations(sc)
        Test_StratifiedSampling(sc)
        sc.stop
    }

    /**
     * `colStats()` returns an instance of `MultivariateStatisticalSummary`,
     * which contains the column-wise max, min, mean, variance, and number of nonzeros,
     * as well as the total count.
     * @param sc
     */
    def Test_ColumnStatistics(sc: SparkContext): Unit = {
        println("\n" + "="*30 + " `Statistics` " + "="*30)

        val vec = sc.parallelize(Array(
                Vectors.dense(0.0, 1.0, 2.0),
                Vectors.dense(0.1, 1.1, 2.1),
                Vectors.dense(0.2, 1.2, 2.2),
                Vectors.dense(0.3, 1.3, 2.3)))

        val summary: MultivariateStatisticalSummary = Statistics.colStats(vec)

        // 所有的统计都是column wise
        println("min -> " +  summary.min)       //  [0.0,1.0,2.0]
        println("max -> " +  summary.max)       //  [0.3,1.3,2.3]
        println("mean -> " +  summary.mean)     //  [0.15000000000000002,1.15,2.15]
        println("variance -> " +  summary.variance) //  [0.016666666666666666,0.016666666666666666,0.016666666666666687]
        println("numNonzeros -> " +  summary.numNonzeros)   //  [3.0,4.0,4.0]
        println("count -> " + summary.count)    //  4
    }


    /**
     * 计算相关度，包括： Pearson Correlation 以及
     *
     * `Statistics` provides methods to calculate correlations between series.
     * Depending on the type of input, two RDD[Double]s or an RDD[Vector], the output
     * will be a Double or the correlation Matrix respectively.
     *
     * 关于Pearson correlation，参考：http://www.statisticshowto.com/what-is-the-pearson-correlation-coefficient/
     * 关于
     * @param sc
     */
    def Test_Correlations(sc: SparkContext): Unit = {
        println("\n" + "="*30 + " `Correlation` " + "="*30)

        val X: RDD[Vector] = sc.parallelize(Array(
            Vectors.dense(0.0, 1.0, 2.0),
            Vectors.dense(0.1, 1.1, 2.1),
            Vectors.dense(0.2, 1.2, 2.2),
            Vectors.dense(0.3, 1.3, 2.3)))

        // calculate the correlation matrix using Pearson's method. Use "spearman" for Spearman's method.
        // If a method is not specified, Pearson's method will be used by default.
        val corrMat: Matrix = Statistics.corr(X, "pearson")
        println(s"corrMat:\n ${corrMat}")


        // compute the correlation using Pearson's method. Enter "spearman" for Spearman's method. If a
        // method is not specified, Pearson's method will be used by default.
        val d1 = sc.parallelize(Array(1.1, 2.2, 3.3, 4.4))
        val d2 = sc.parallelize(Array(5.5, 6.6, 7.7, 8.8))
        val corr: Double = Statistics.corr(d1, d2, "pearson")
        println(s"corr -> ${corr}" )
    }


    /**
     * The `sampleByKey` method will flip a coin to decide whether an observation will be sampled or not,
     * therefore requires one pass over the data, and provides an expected sample size.
     *
     * `sampleByKeyExact` requires significant more resources than the per-stratum simple random sampling used
     * in `sampleByKey`, but will provide the exact sampling size with 99.99% confidence.
     *
     * Similar to `RDD.sample`, `sampleByKey` applies Bernoulli sampling or Poisson sampling to
     * each item independently, which is cheap but DOES NOT guarantee the exact sample size for each stratum
     * (the size of the stratum times the corresponding sampling probability). `sampleByKeyExact` utilizes scalable
     * sampling algorithms that guarantee the exact sample size for each stratum with high probability, but it
     * requires multiple passes over the data. We have a separate method name to underline the fact that it is significantly more expensive.
     */
    def Test_StratifiedSampling(sc:SparkContext): Unit = {
        val data = sc.parallelize(Range(0,100))
                .map(x => (x, x*200))

        // 注意：frac是data中 key -> probability 的集合
        // frac中的key必须能覆盖data中的全部的key，否则运行时在data中会找不到未被覆盖的key
        val frac = scala.collection.mutable.Map[Int, Double]()
        for ((k, v) <- data.collect) {
            frac.put(k, 0.1)
        }

        // 参数`withReplacement`为true是放回抽样
        // 参数`withReplacement`为false是不放回抽样
        val newSample = data.sampleByKey(false, frac)
        newSample.collect.foreach(println)
    }


    /**
     * 产生服从一定分布的随机数据
     * MLlib supports generating random RDDs with i.i.d. values drawn from a given distribution: uniform, standard normal, or Poisson.
     *
     * i.i.d. means "Independent and Identically Distributed"
     *
     * `RandomRDDs` provides factory methods to generate random double RDDs or vector RDDs.
     */
    def Test_RandomDataGeneration(sc: SparkContext): Unit = {
        // 生成100万条Double数据，均匀地分布在20个partition中
        // `RandomRDDs.normalRDD`生成的数据会服从标准正态分布的随机数据 X ~ N(0, 1)
        val rdd: RDD[Double] = RandomRDDs.normalRDD(sc, 1000000L, 20)

        // 从标准正态分布N(0, 1)，转换到正态分布N(1, 4)
        val normalRDD = rdd.map(x => 1.0 + 2.0*x)
    }

}
