package cn.gridx.scala.lang.test

/**
  * Created by tao on 11/16/16.
  */
object test4 {
  def main(args: Array[String]): Unit = {
    val x = false
    val o = x.asInstanceOf[Object]
    val y = o.asInstanceOf[Boolean]

    val map = new java.util.HashMap[String, Object]()
    map.put("o", o)
    println(map.get("o").asInstanceOf[Boolean])
  }
}
