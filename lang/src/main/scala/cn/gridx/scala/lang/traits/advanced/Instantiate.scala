package cn.gridx.scala.lang.traits.advanced

/**
  * Created by tao on 3/9/16.
  *
  * 输出为
  *
    [cn.gridx.scala.lang.traits.advanced.Clz] Print : hello
    [cn.gridx.scala.lang.traits.advanced.Clz] Show : world


    [null] Print : 你好
    [null] Print : 世界

    [null] Print : aaaa

  */
object Instantiate extends App {
  val c = new Clz()
  c.Print("hello")
  c.Show("world")
  println("\n")

  // trait不能被实例化,
  // 这里看起来是实例化,其实是创建了一个anonymous class
  val x = new SimpleTrait {
    override def Show(msg: String) =
      println(s"[${this.getClass.getCanonicalName}] Print : $msg")
  }
  x.Print("你好")
  x.Show("世界")
  println("\n")


  val j: Jack with JTrait = new Jack("阿法狗击败了李师师") with JTrait
  j.Print("aaaa")  // 无法访问`j.msg`, 因为`j`的类型并不是 `Jack`
}


/**
  * trait可以包含未实现/已实现的方法
  * */
trait SimpleTrait {
  // 未实现的方法
  def Show(msg: String)

  // 已实现的方法
  def Print(msg: String): Unit = {
    println(s"[${this.getClass.getCanonicalName}] Print : $msg")
  }
}

class Clz extends SimpleTrait {
  override def Show(msg: String): Unit = {
    println(s"[${this.getClass.getCanonicalName}] Show : $msg")
  }
}


class Jack(msg: String) {

}

trait JTrait {
  def Print(msg: String): Unit = {
    println(s"[${this.getClass.getCanonicalName}] Print : $msg")
  }
}
