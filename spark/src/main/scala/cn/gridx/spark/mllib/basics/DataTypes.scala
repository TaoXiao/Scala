package cn.gridx.spark.mllib.basics

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.{Vector, Vectors,Matrix, Matrices}
import org.apache.spark.mllib.linalg.distributed.{RowMatrix, IndexedRow, IndexedRowMatrix, CoordinateMatrix, MatrixEntry, BlockMatrix}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD


/**
 * Created by tao on 11/28/15.
 */
object DataTypes {
    def main(args: Array[String]): Unit = {
        val logger = Logger.getRootLogger
        logger.setLevel(Level.ERROR)
        
        //Test_LocalVector
        //Test_LabeledPoint
        //Test_LocalMatrix

        val sc = new SparkContext(new SparkConf().setAppName("using mllib"))
        //Test_LoadLibSvmFile(sc, "/user/tao/mllib/data/libsvm.file")

        Test_DistributedMatrix(sc)
        sc.stop
    }

    /**
     * local vector存储在一台机器上
     * index是从0开始的Int，值是Double
     */
    def Test_LocalVector(): Unit = {
        val dv:  Vector = Vectors.dense(1.0, 2.0, 3.0)
        val sv1: Vector = Vectors.sparse(5, Array(0, 4), Array(0.0, 4.4)) //
        val sv2: Vector = Vectors.sparse(4, Seq((1, 1.1), (3, 3.3)))    //

        println(s"dv  -> ${dv}")
        println(s"sv1 -> ${sv1}")
        println(s"sv2 -> ${sv2}")
    }


    /**
     * labeld point 是 local vector再加一个label
     * label的类型是Double
     * 对于binary classification，label选择0.0或者1.1
     * 对于multiclass classification，label选择0.0, 1.0, 2.0, ···
     *
     * LabeledPoint是一个case class
     */
    def Test_LabeledPoint(): Unit = {
        val pos = LabeledPoint(1.0, Vectors.dense(1.0, 2.0, 3.0))
        println(pos)
    }


    /**
     * [LIBSVM](http://www.csie.ntu.edu.tw/~cjlin/libsvm/)与[LIBLINEAR](http://www.csie.ntu.edu.tw/~cjlin/liblinear/)
     * 这两个库所使用的默认的数据文件，每行数据的格式为：
     *      label index1:value1 index2:value2 ...
     *      这里的空白分隔符只能是空格（1个或多个），不能是\t
     *  即每一行是一个labeled sparse feature vector
     *  这里的index从1开始，且是递增的
     *
     * @param sc
     * @param path
     */
    def Test_LoadLibSvmFile(sc: SparkContext, path: String): Unit = {
        val data: RDD[LabeledPoint] = MLUtils.loadLibSVMFile(sc, path)
        data.collect.foreach(println)
    }


    /**
     * local matrix的索引是Int类型的，元素的值都是Double类型的
     *
     * local matrix的基类是Matrix，它的一个“稠密矩阵”的实现类是DenseMatrix
     *
     * Matrix中的数据按照列向量的次序排列。
     * 例如，对于下面的 3x2 的矩阵
     *    1.0   2.0
     *    3.0   4.0
     *    5.0   6.0
     *
     *  其矩阵内容的表达式为[1.0, 3.0, 5.0, 2.0, 4.0, 6.0]，矩阵维度的表达式为[3, 2]
     *
     */
    def Test_LocalMatrix(): Unit ={
        val dm: Matrix = Matrices.dense(3, 2, Array(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))
        println(dm)
        println(s"(0, 0) -> ${dm.apply(0,0)}")
        println(s"(2, 1) -> ${dm.apply(2,1)}")
    }


    /**
     * distributed matrix的索引是Long类型的，元素的值是Double类型的。
     *
     * distributed matrix有3种实现形式，分别是`RowMatrix`， `BlockMatrix`, `IndexedRowMatrix`
     *
     * RowMatrix：
     *      A `RowMatrix` is a row-oriented distributed matrix without meaningful row indices,
     *      backed by an RDD of its rows, where each row is a local vector.
     *      Since each row is represented by a local vector, the number of columns is limited
     *      by the integer range but it should be much smaller in practice.
     *
     *
     * IndexedRowMatrix：
     *      An `IndexedRowMatrix` is similar to a RowMatrix but with meaningful row indices.
     *      It is backed by an RDD of indexed rows, so that each row is represented by
     *      its index (long-typed) and a local vector.
     *
     *      An `IndexedRowMatrix` can be created from an RDD[IndexedRow] instance,
     *      where `IndexedRow` is a wrapper over (Long, Vector). An IndexedRowMatrix can be converted
     *      to a RowMatrix by dropping its row indices.
     *
     *
     *
     * CoordinateMatrix:
     *      A `CoordinateMatrix` is a distributed matrix backed by an RDD of its entries. Each entry is
     *      a tuple of (i: Long, j: Long, value: Double), where i is the row index, j is the column index,
     *      and value is the entry value. A `CoordinateMatrix` should be used only when both dimensions
     *      of the matrix are huge and the matrix is very sparse.
     *
     */
    def Test_DistributedMatrix(sc: SparkContext): Unit = {
        /************************  RowMatrix  *************************************/
        println("-"*20 + " `RowMatrix` " + "-"*20)
        val rows: RDD[Vector] = sc.parallelize(
                Array(Vectors.dense(1.0, 2.0, 3.0),
                      Vectors.dense(4.0, 5.0, 6.0),
                      Vectors.dense(7.0, 8.0, 9.0),
                      Vectors.dense(10.0, 11.0, 12.0)))

        val mt = new RowMatrix(rows)
        val m = mt.numRows
        val n = mt.numCols
        println(s"m -> ${m},  n -> ${n}")


        /************************  IndexedRow  *************************************/
        println("-"*20 + " `IndexedRow` " + "-"*20)
        val indexedRows: RDD[IndexedRow] = sc.parallelize(
                Array(IndexedRow(0, Vectors.dense(1.1, 2.2, 3.3)),
                        IndexedRow(1, Vectors.dense(4.4, 5.5, 6.6))))

        val mat: IndexedRowMatrix = new IndexedRowMatrix(indexedRows)
        println(s"# of rows -> ${mat.numRows()}")
        println(s"# of columns -> ${mat.numCols()}")

        val rowMatrix: RowMatrix = mat.toRowMatrix


        /************************  CoordinateMatrix  *************************************/
        println("-"*20 + " `CoordinateMatrix` " + "-"*20)
        val entries: RDD[MatrixEntry] = sc.parallelize(
                Array(MatrixEntry(0, 0, 0.0),
                        MatrixEntry(0, 1, 1.0),
                        MatrixEntry(0, 2, 2.0),
                        MatrixEntry(1, 0, 0.1),
                        MatrixEntry(1, 1, 0.2),
                        MatrixEntry(1, 2, 0.3)))
        val coordinateMatrix: CoordinateMatrix = new CoordinateMatrix(entries)
        val indexedRowMatrix: IndexedRowMatrix = coordinateMatrix.toIndexedRowMatrix()



        /************************  BlockMatrix  *************************************/
        println("-"*20 + " `BlockMatrix` " + "-"*20)
        val blockMatrix: BlockMatrix = coordinateMatrix.toBlockMatrix.cache
        blockMatrix.validate
        val ata: BlockMatrix = blockMatrix.transpose.multiply(blockMatrix)
        println(s"nRows -> ${ata.numRows},  nCols -> ${ata.numCols}")
    }


}
