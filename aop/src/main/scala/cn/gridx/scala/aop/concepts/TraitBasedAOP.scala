package cn.gridx.scala.aop.concepts

/**
  * Created by tao on 12/6/16.
  */

trait Action {
  def doAction
}

trait BeforeAndAfter extends Action {
  abstract override def doAction {
    println("Before ...")
    super.doAction
    println("After ...")
  }
}

class Work extends Action {
  override def doAction = {
    println("I am working!")
  }
}


/**
  * 输出是
    Before ...
    I am working!
    After ...
  * */
object Test extends App {
  val work = new Work with BeforeAndAfter
  work.doAction
}