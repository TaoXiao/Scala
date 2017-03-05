package cn.gridx.scala.lang.traits.plugins

import scala.collection.mutable

/**
  * Created by tao on 1/3/17.
  */
trait Modifier {
  def modify(): Unit = ???
}


object Modifier {
  val map = mutable.HashMap[String, Object]()

  def registerModifier(name: String): Unit = {
    val clz = Class.forName(name + "Modifier").asSubclass(classOf[Modifier])
    map.put(name, clz.newInstance())
  }

  def apply(name: String): Unit = {
    val obj = map.get(name)
    if (obj.isDefined)
      println("找到了")
    else
      println("没找到")
  }
}