package cn.gridx.scala.lang.classes.classutils

import org.clapper.classutil.ClassFinder


/**
  * Created by tao on 1/3/17.
  */
object FindSubClass extends App {
  val finder = ClassFinder()
  val classes = finder.getClasses()
  val classMap = ClassFinder.classInfoMap(classes)
  val plugins = ClassFinder.concreteSubclasses("cn.gridx.scala.lang.classes.classutils.Base", classMap)
  plugins.foreach(println)
}

trait Base

class A extends Base

class B extends Base
