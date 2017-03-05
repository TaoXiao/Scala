package cn.gridx.scala.lang.traits.plugins

/**
  * Created by tao on 1/3/17.
  */
class SolarModifier extends Modifier {
  override
  def modify(): Unit = {
    println("SolarModifier::modify")
  }
}


object SolarModifier {
  def main(args: Array[String]): Unit = {
    Modifier.registerModifier("cn.gridx.scala.lang.traits.plugins.Solar")
  }
}
