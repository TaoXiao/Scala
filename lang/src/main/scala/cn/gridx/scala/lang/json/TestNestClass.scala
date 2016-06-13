package cn.gridx.scala.lang.json

import cn.gridx.scala.lang.json.jsonbean.{JavaDT, JavaST}
import com.google.gson.GsonBuilder

/**
  * Created by tao on 6/13/16.
  */
object TestNestClass {
  def main(args: Array[String]): Unit = {
    val builder = new GsonBuilder().serializeNulls()
    val gson = builder.create()


    val out =  OuterST("msg", new JavaST("Jack", 200))
    println(gson.toJson(out, classOf[OuterST]))


    val javaDT = new JavaDT("i am java dt", ScalaDT("", 100))
    println(gson.toJson(javaDT))
  }
}


// 在Scala中引用一个Java中定义的class
case class OuterST(info: String, st: JavaST)


// ScalaDT将被JavaDT引用
case class ScalaDT(name: String, age: Int)
