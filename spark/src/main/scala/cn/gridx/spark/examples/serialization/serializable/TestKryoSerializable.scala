package cn.gridx.spark.examples.serialization.serializable

import cn.gridx.spark.examples.serialization.beans.{CustomBean, UnserializableBean}
import com.twitter.chill.KryoSerializer
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.spark.{SparkContext, SparkConf}


/**
 * Created by tao on 1/1/16.
 */
object TestKryoSerializable {
    def main(args: Array[String]): Unit = {
        test3
    }

    /**
     *
     */
    def test1(): Unit = {
        val conf = new SparkConf()
            .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .registerKryoClasses(Array(classOf[UnserializableBean]))
        val sc = new SparkContext(conf)

        /**
         * 将ImmutableBytesWritable 实例放入广播变量中可以被序列化
         * */
        val bytes = new ImmutableBytesWritable("Hello".getBytes)
        val bc    = sc.broadcast(bytes)

        sc.parallelize(Range(0, 1000), 20)
            .map(i => new UnserializableBean(bc.value.get, (i%3).toString))
            .map(x => (x.tag, x)) // bean.tag 只有 [0, 1, 2]三种值
            .aggregateByKey(List[UnserializableBean]())(
                (xs:List[UnserializableBean], x:UnserializableBean) =>  x::xs,
                (xs:List[UnserializableBean], ys:List[UnserializableBean]) => xs:::ys)
            .map(t => (t._1, t._2.size)) // 取出List中的第一个UnserializableBean实例
            .collect
            .foreach(t => println(s"${t._1} => ${t._2}"))


        sc.stop
    }


    def test2(): Unit = {
        val conf = new SparkConf()
                //.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                //.registerKryoClasses(Array(classOf[CustomBean]))
        val sc = new SparkContext(conf)

        val bean = new CustomBean("captured bean", 1001)

        sc.parallelize(Range(0, 1000), 10)
            .map(i => new CustomBean(bean.msg, i%4))
            .map(b => (b.id, b))
            .aggregateByKey(List[CustomBean]())(
                (xs:List[CustomBean], x:CustomBean) => x::xs,
                (xs:List[CustomBean], ys:List[CustomBean]) => xs:::ys)
            .map(t => (t._1, t._2.size))
            .collect
            .foreach(println)

        sc.stop

    }


    def test3(): Unit = {
        val conf = new SparkConf()
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .set("spark.kryo.registrationRequired", "true")
                .registerKryoClasses(Array(classOf[CustomBean],
                    scala.collection.immutable.Nil.getClass,
                    classOf[scala.collection.immutable.Range],
                    Class.forName("scala.collection.immutable.$colon$colon"),
                    classOf[scala.Tuple2[_,_]]))

        val sc = new SparkContext(conf)

        sc.parallelize(Range(0, 1000), 20)
            .map(i => new CustomBean(i.toString, i%5))
            .map(t => (t.id, t))
            .aggregateByKey(List[CustomBean]())(
                (xs:List[CustomBean], x:CustomBean) => x::xs,
                (xs:List[CustomBean], ys:List[CustomBean]) => xs:::ys)
            .map(t => (t._1, t._2.head))
            .collect
            .foreach(println)

        sc.stop

    }


    /** *
      * 在Spark 1.6 之前不能调用SparkConf#set("spark.kryo.registrationRequired", "true")
      * 因为有一些Scala class没有被注册，例如 scala.Tuple2, scala.collection.immutable.Nil, scala.collection.immutable.Range
      * 详见 https://issues.apache.org/jira/browse/SPARK-10251
      */
    def test4(): Unit = {
        val conf = new SparkConf()
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                //.set("spark.kryo.registrationRequired", "true")
                .registerKryoClasses(Array(classOf[CustomBean]))


        val sc = new SparkContext(conf)

        sc.parallelize(Range(0, 1000), 20)
                .map(i => new CustomBean(i.toString, i%5))
                .map(t => (t.id, t))
                .aggregateByKey(List[CustomBean]())(
                    (xs:List[CustomBean], x:CustomBean) => x::xs,
                    (xs:List[CustomBean], ys:List[CustomBean]) => xs:::ys)
                .map(t => (t._1, t._2.head))
                .collect
                .foreach(println)

        sc.stop
    }


}
