package cn.gridx.scala.lang.patterns

/**
  * Created by tao on 2/25/16.
  */
object partial_function {
  /**
    * 用 case sequences 来定义一个 partial function
    *
    * 一个 case sequence 可以有多个entry points,
    * 每个entry point都可以有自己的参数列表
    *
    * 这就实现了类似于C++中的[函数重载]功能,而Java中是没有函数重载的
    * */
  val withDefault: Option[Int] => Int = {
    case Some(x) => {
      println(s"传给`withDefault`的参数是 [${x}]")
      x
    }
    case None => {
      println(s"传给`withDefault`的参数是 [不存在]")
      -100
    }
  }


  /**
    * 取出list中的第2个数据
    * */
  val second: List[Int] => Int = {
    case x :: y :: _ => {
      println(s"第2个元素是 [$y]")
      y
    }
  }

  /**
    * PartialFunction 类型有一个方法: `isDefinedAt`
    *
    * `isDefinedAt` can test whether a partial function
    * is defined at a particular value
    * */
  val second_v2: PartialFunction[List[Int], Int] = {
    case x :: y :: _ => {
      println(s"第2个元素是 [$y]")
      y
    }
  }


  def main(args: Array[String]): Unit = {
    withDefault(None)
    withDefault(Some(20))
    second(List(100, 200, 300))
    second(List(100, 200))
    // second(List(100)) // 异常:

    if (second_v2.isDefinedAt(List(100))) println("`second_v2` is defined at the value `List(100)`")
    else println("`second_v2` is NOT defined at the value `List(100)`")

    if (second_v2.isDefinedAt(List(100, 200))) println("`second_v2` is defined at the value `List(100, 200)`")
    else println("`second_v2` is NOT defined at the value `List(100, 2O0)`")
  }
}
