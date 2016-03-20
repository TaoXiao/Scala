package cn.gridx.spark.mllib.algo.decisionTrees

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by tao on 12/1/15.
 *
 * 测试连续性数据的决策树
 */
object ContinuousFeatures {
    def main(args:Array[String]): Unit = {
        Logger.getRootLogger.setLevel(Level.WARN)
        Test_ContinuousAttributes_2
    }

    /**
     * 对连续型数据进行测试
     * 数据来源于
     *
     *  https://github.com/apache/spark/blob/master/data/mllib/sample_tree_data.csv
     *
     *  每行是一条记录，其中第一列是class tag，其余的都是feature，且都是continuous-valued
     *  每行有29个属性，共有569行记录，形如
     *      1,17.99,10.38,122.8,1001,0.1184,0.2776,0.3001,0.1471,0.2419,0.07871,1.095,0.9053,8.589,153.4,0.006399,0.04904,0.05373,0.01587,0.03003,0.006193,25.38,17.33,184.6,2019,0.1622,0.6656,0.7119,0.2654,0.4601
     */
    def Test_ContinuousAttributes_1(): Unit = {
        def DatasetFilePath = "/user/tao/mllib/data/sample_tree_data.csv"
        val sc = new SparkContext(new SparkConf().setAppName("连续性变量的决策树构建"))

        val dataSplits: Array[RDD[LabeledPoint]]
            = sc.textFile(DatasetFilePath, 10)
                .map( line => {
                    val idx = line.indexOf(",")
                    (line.substring(0, idx), line.substring(idx+1).split(",")) })
                .map(t => (t._1.toDouble, t._2.map(_.toDouble)))
                .map(t => LabeledPoint(t._1, Vectors.dense(t._2)))
                .randomSplit(Array(0.8, 0.3)) // 将原始的数据集分为训练集和测试集

        val (trainingData, testData) = (dataSplits(0), dataSplits(1))


        val numClass = 2 // class tag为0或者1
        val impurity = "gini"
        val maxDepth = 10
        val maxBins  = 60

        val model: DecisionTreeModel = DecisionTree.trainClassifier(trainingData, numClass, Map[Int, Int](), impurity, maxDepth, maxBins)

        //println("="*30 + " 模型结构 " + "="*30)
        //println(model.toDebugString)

        // 计算测试集上分类的错误率
        val labelAndPreds: RDD[(Double, Double)] = testData.map(point =>(point.label, model.predict(point.features)))
        val totalError: Double = labelAndPreds.map(t => if (t._1 != t._2) 1 else 0).sum
        val errorRate: Double = totalError/labelAndPreds.count

        println(s"\n测试集上的分类错误率为 ${errorRate}")

        sc.stop
    }



    /**
     * 对连续型数据进行测试
     * 数据来源于
     *
     *  http://kdd.ics.uci.edu/databases/kddcup99/kddcup99.html
     *
     *  源数据文件中，每一行的前41列是属性，最后一列是class tag
     *  其中大部分的属性的数据类型是continuous-valued，部分是discrete-valued
     *  离散型的属性列分别是（列索引从0开始计算）:
     *      1(protocol_type), 2(service), 3(flag),
     *      6(land), 11(logged_in), 20(is_host_login), 21(is_guest_login)
     *
     *  class-tag有多种种，分别是：back,buffer_overflow,ftp_write,guess_passwd,imap,
     *  ipsweep,land,loadmodule,multihop,neptune,nmap,normal,perl,phf,pod,portsweep,
     *  rootkit,satan,smurf,spy,teardrop,warezclient,warezmaster.
     *
     * 所以这是一个multiclass classification
     */
    def Test_ContinuousAttributes_2(): Unit = {
        val conf = new SparkConf().setAppName("混合型属性（连续与离散数据）的决策树构建")
        conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        val sc = new SparkContext(conf)

        val data: RDD[(Array[String], String)] = sc.textFile("/user/tao/mllib/data/kddcup-1999.data")
                .map(line => { val idx = line.lastIndexOf(",")
                        (line.substring(0, idx).split(","), line.substring(idx+1))})    // (feature-array, class-tag)
        data.persist

        val bcProtocolType  = sc.broadcast(data.map(_._1(1)).distinct.collect)
        val bcService       = sc.broadcast(data.map(_._1(2)).distinct.collect)
        val bcFlag          = sc.broadcast(data.map(_._1(3)).distinct.collect)
        val bcLand          = sc.broadcast(data.map(_._1(6)).distinct.collect)
        val bcLoggedIn      = sc.broadcast(data.map(_._1(11)).distinct.collect)
        val bcIsHostLogin   = sc.broadcast(data.map(_._1(20)).distinct.collect)
        val bcIsGuestLogin  = sc.broadcast(data.map(_._1(21)).distinct.collect)
        val bcClassTag      = sc.broadcast(data.map(_._2).distinct.collect)
        val numClasses      = bcClassTag.value.size

        val categoricalFeatureInfo = Map(1 -> bcProtocolType.value.size, 2 -> bcService.value.size, 3 -> bcFlag.value.size,
                6 -> bcLand.value.size, 11 -> bcLoggedIn.value.size, 20 -> bcIsHostLogin.value.size, 21 -> bcIsGuestLogin.value.size)

        val dataSplits: Array[RDD[LabeledPoint]] = data.map(t => {
                t._1(1)  = bcProtocolType.value.indexOf(t._1(1)).toString
                t._1(2)  = bcService.value.indexOf(t._1(2)).toString
                t._1(3)  = bcFlag.value.indexOf(t._1(3)).toString
                t._1(6)  = bcLand.value.indexOf(t._1(6)).toString
                t._1(11) = bcLoggedIn.value.indexOf(t._1(11)).toString
                t._1(20) = bcIsHostLogin.value.indexOf(t._1(20)).toString
                t._1(21) = bcIsGuestLogin.value.indexOf(t._1(21)).toString
                (bcClassTag.value.indexOf(t._2).toString, t._1) })
            .map(t => LabeledPoint(t._1.toDouble, Vectors.dense(t._2.map(_.toDouble))))
            .randomSplit(Array(0.9, 0.1))

        val trainingSet = dataSplits(0);    trainingSet.cache()
        val testingSet  = dataSplits(1);    testingSet.cache()

        val model = DecisionTree.trainClassifier(trainingSet, numClasses, categoricalFeatureInfo, "entropy", 15, 100)

        val labelAndPreds: RDD[(Double, Double)] = testingSet.map(p => (p.label, model.predict(p.features)))
        val totalError: Double = labelAndPreds.map(t => if (t._1 != t._2) 1 else 0).sum
        val errorRate: Double = totalError/data.count

        // 实际测试的错误率为：6.124410040684456E-6
        println("在测试集上的错误率为 " + errorRate)

        sc.stop
    }

}
