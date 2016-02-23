package cn.gridx.scala.lang.implicits.conversions.other_conversions

import cn.gridx.scala.lang.implicits.conversions.{Drink, Prompt}

/**
 * Created by tao on 11/24/15.
 */
object TaoPrefences {
    implicit def prompt = new Prompt("$")
    implicit def drink  = new Drink("Coca-cola")
}
