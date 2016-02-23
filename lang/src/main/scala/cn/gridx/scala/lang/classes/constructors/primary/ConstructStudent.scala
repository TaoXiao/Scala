package cn.gridx.scala.lang.classes.constructors.primary

import org.apache.hadoop.hbase.util.Bytes

/**
 * Created by tao on 7/16/15.
 */
object ConstructStudent {
    def main(args: Array[String]): Unit = {
        /*
          调用` new Student("小明", Bytes.toBytes("字节数组")) `的输出为

            start constructing!
            Name = 小明, bytes = 字节数组,age = 18 homeDir = /Users/tao, timestamp = 1437123199063
            finish constructing!

        * */
        val stu = new Student("小明", Bytes.toBytes("字节数组"), 18)

        // 下面两句话是等价的
        stu.age = 20
        stu.age_$eq(20)

        // println(stu.bytes) ;;  错误！因为`bytes`既不是val也不是var，所以它不能读也不能写

    }
}
