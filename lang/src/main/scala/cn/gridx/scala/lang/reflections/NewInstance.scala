package cn.gridx.scala.lang.reflections

/**
  * Created by tao on 1/4/17.
  */
object NewInstance {
  def main(args: Array[String]): Unit = {
    val clz = Class.forName("cn.gridx.scala.lang.reflections.A")
    val con = clz.getConstructor(classOf[Map[String, String]])
    val obj = con.newInstance(Map("1" -> "100", "2" -> "200")).asInstanceOf[Base]
    obj.show()
  }
}


trait Base {
  def show(): Unit = ???
}

class A(attribute: Map[String, String]) extends Base {
  override
  def show(): Unit = {
    println(s"A: attribute = $attribute")
  }
}
