package cn.gridx.scala.lang.classes.singleton

/**
  * Created by tao on 5/7/16.
  */
object SingleObject {
  println(s"object [SingleObject] is accessed")
  val msg = "I'm a singleton"

  val instance1 = new SingleObject("Tom")
  val instance2 = new SingleObject("Jack")
}

class SingleObject(val name: String) {
  println(s"SingleObject [$name] is instantiated ")
  val msg = "I'm NOT singleton"
}


