package cn.gridx.scala.lang.concurrency.actors

import scala.actors.Actor
import scala.actors.Actor._

/**
  * Created by tao on 2/25/16.
  */
object MessageExample {
  def main(args: Array[String]): Unit = {
    println("+++++++ main开始 +++++++")

    //test_receive

    test_return

    println("+++++++ main结束 +++++++")
  }


  /**
    * 测试 `receive` 的基本用法
    * */
  def test_receive(): Unit = {
    self ! 100
    self ! "你好"

    self.receive{case x: String => println(s"String内容为 [$x]")}
    self.receive{case x: Int => println(s"Int内容为 [$x]")}

    println("结束 ...")
  }


  /**
    * 测试 `receive` 的返回值
    * */
  def test_return(): Unit = {

    val theActor: Actor = actor {
      while (true) {
        receive {
          case x: Int =>
            print(s"整型")
            x*1000
          case y: String =>
            print(s"字符串")
            y + "_后缀"
        }
      }
    }

    val a = 33
    val b = "他好,我也好"

    val ret1 = theActor ! a
    val ret2 = theActor ! b

    // 返回的是 [()]
    println(s"ret1 = [$ret1]")
    println(s"ret2 = [$ret2]")
  }

}



