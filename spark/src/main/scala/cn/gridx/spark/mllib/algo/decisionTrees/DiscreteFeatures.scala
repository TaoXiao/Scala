package cn.gridx.spark.mllib.algo.decisionTrees

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by tao on 11/30/15.
 *
 *  对MLlib中的决策树工具的使用例子(Spark 1.3)
 *
 *  参考：   http://web.cs.ucla.edu/~linmanna/cs239/
 *          https://spark.apache.org/docs/1.3.0/mllib-decision-tree.html
 *          https://databricks.com/blog/2014/09/29/scalable-decision-trees-in-mllib.html
 *
 */
object DiscreteFeatures {
    def main(args: Array[String]): Unit = {
        Logger.getRootLogger.setLevel(Level.ERROR)
        val sc = new SparkContext(new SparkConf())

        Test_SmallCategoricalDataset_1(sc)
        println("\n\n\n")

        Test_SmallCategoricalDataset_2(sc)
        println("\n\n\n")

        sc.stop

    }


    /** 用一个小数据集来测试决策树
     * 数据集中的tuple的全部feature都是categorical
     * dataset = [
                [1, 1, 'yes'],
                [1, 1, 'yes'],
                [1, 0, 'no'],
                [0, 1, 'no'],
                [0, 1, 'no']
            ]

            'yes'记为1, 'no'记为1
     */
    def Test_SmallCategoricalDataset_1(sc: SparkContext): Unit = {
        val trainingData: RDD[LabeledPoint] = sc.parallelize(Array(
            LabeledPoint(1, Vectors.dense(1, 1)),
            LabeledPoint(1, Vectors.dense(1, 1)),
            LabeledPoint(0, Vectors.dense(1, 0)),
            LabeledPoint(0, Vectors.dense(0, 1)),
            LabeledPoint(0, Vectors.dense(0, 1))))
        trainingData.cache

        val numClasses = 2
        val categoricalFeatureInfo = Map(0 -> 2, 1 -> 2) // categorical feature info，为空则认为所有的feature都是连续性变量
        val impurity = "entropy" // 或者是gini
        val maxDepth = 5
        val maxBins = 32
        val model: DecisionTreeModel = DecisionTree.trainClassifier(trainingData, numClasses, categoricalFeatureInfo, impurity, maxDepth, maxBins)

        // 将训练出的模型持久化到HDFS文件中，然后再将其读出来使用
        def modelPath = "/user/tao/mllib/model/tree.model"
        model.save(sc, modelPath)
        val sameModel = DecisionTreeModel.load(sc, modelPath)

        /**
         * 输出
         * DecisionTreeModel classifier of depth 2 with 5 nodes
              If (feature 0 in {0.0})
               Predict: 0.0
              Else (feature 0 not in {0.0})
               If (feature 1 in {0.0})
                Predict: 0.0
               Else (feature 1 not in {0.0})
                Predict: 1.0
         */
        println("="*30 + " full model in the form of a string " + "="*30)
        println(sameModel.toDebugString)


        println("="*30 + " testing prediction " + "="*30)
        println("(1, 1) -> " + sameModel.predict(Vectors.dense(1,1)))
        println("(0, 1) -> " + sameModel.predict(Vectors.dense(0,1)))
    }


    /**
     * 另一个数据集，来自 Data Mining: Concepts and Techniques, 3rd edition
     *  dataset = [
            ['youth',       'high',     'no',   'fair',         'no'],
            ['youth',       'high',     'no',   'excellent',    'no'],
            ['middle_aged', 'high',     'no',   'fair',         'yes'],
            ['senior',      'medium',   'no',   'fair',         'yes'],
            ['senior',      'low',      'yes',  'fair',         'yes'],
            ['senior',      'low',      'yes',  'excellent',    'no'],
            ['middle_aged', 'low',      'yes',  'excellent',    'yes'],
            ['youth',       'medium',   'no',   'fair',         'no'],
            ['youth',       'low',      'yes',  'fair',         'yes'],
            ['senior',      'medium',   'yes',  'fair',         'yes'],
            ['youth',       'medium',   'yes',  'excellent',    'yes'],
            ['middle_aged', 'medium',   'no',   'excellent',    'yes'],
            ['middle_aged', 'high',     'yes',  'fair',         'yes'],
            ['senior',      'medium',   'no',   'excellent',    'no']
        ]

        labels = ['age', 'income', 'student', 'credit_rating', 'buys_computer']
     * @param sc
     */
    def Test_SmallCategoricalDataset_2(sc: SparkContext): Unit = {
        val originalDataset = Array(
                ("youth",       "high",     "no",   "fair",         "no"),
                ("youth",       "high",     "no",   "excellent",    "no"),
                ("middle_aged", "high",     "no",   "fair",         "yes"),
                ("senior",      "medium",   "no",   "fair",         "yes"),
                ("senior",      "low",      "yes",  "fair",         "yes"),
                ("senior",      "low",      "yes",  "excellent",    "no"),
                ("middle_aged", "low",      "yes",  "excellent",    "yes"),
                ("youth",       "medium",   "no",   "fair",         "no"),
                ("youth",       "low",      "yes",  "fair",         "yes"),
                ("senior",      "medium",   "yes",  "fair",         "yes"),
                ("youth",       "medium",   "yes",  "excellent",    "yes"),
                ("middle_aged", "medium",   "no",   "excellent",    "yes"),
                ("middle_aged", "high",     "yes",  "fair",         "yes"),
                ("senior",      "medium",   "no",   "excellent",    "no"))

        // 对数据集进行转换
        val dataSet: Array[LabeledPoint]
            = originalDataset.map(t => LabeledPoint(
                t._5 match {    // class tag
                    case "no"  => 0
                    case "yes" => 1
                },
                Vectors.dense(  // features
                    t._1 match {
                        case "youth"       => 0
                        case "middle_aged" => 1
                        case "senior"      => 2
                    },
                    t._2 match {
                        case "high"   => 0
                        case "medium" => 1
                        case "low"    => 2
                    },
                    t._3 match {
                        case "no"  => 0
                        case "yes" => 1
                    },
                    t._4 match {
                        case "excellent" => 0
                        case "fair"      => 1
                    })))

        println("="*30 + " dataset " + "="*30)
        dataSet.foreach(println)
        val trainingData = sc.parallelize(dataSet)
        trainingData.cache

        val numClasses = 2
        val categoricalFeatureInfo: Map[Int, Int] = Map(0 -> 3, 1 -> 3, 2 -> 2, 3 -> 2)
        val impurity = "entropy"
        val maxDepth = 20
        val maxBin = 32

        val model = DecisionTree.trainClassifier(trainingData, numClasses, categoricalFeatureInfo, impurity, maxDepth, maxBin)

        /**
         * DecisionTreeModel classifier of depth 4 with 13 nodes
              If (feature 2 in {0.0})
               If (feature 0 in {0.0})
                Predict: 0.0
               Else (feature 0 not in {0.0})
                If (feature 0 in {2.0})
                 If (feature 3 in {0.0})
                  Predict: 0.0
                 Else (feature 3 not in {0.0})
                  Predict: 1.0
                Else (feature 0 not in {2.0})
                 Predict: 1.0
              Else (feature 2 not in {0.0})
               If (feature 0 in {0.0,1.0})
                Predict: 1.0
               Else (feature 0 not in {0.0,1.0})
                If (feature 3 in {0.0})
                 Predict: 0.0
                Else (feature 3 not in {0.0})
                 Predict: 1.0

            但是，这与书上的例子计算出的Model的结构不同！！！
         */
        println("="*30 + " full model in the form of a string " + "="*30)
        println(model.toDebugString)


        println("="*30 + " testing prediction " + "="*30)
        println("(2, 1, 0, 0) -> " + model.predict(Vectors.dense(2, 1, 0, 0)))
    }
}
