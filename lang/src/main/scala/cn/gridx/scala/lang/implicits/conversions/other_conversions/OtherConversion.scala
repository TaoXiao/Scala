package cn.gridx.scala.lang.implicits.conversions.other_conversions

import cn.gridx.scala.lang.implicits.conversions.{Dollar, RMB}

/**
 * Created by tao on 11/24/15.
 */
object OtherConversion {
    implicit def RMB2Dollar(rmb: RMB): Dollar = new Dollar(rmb.getValue()*RMB.rate2Dollar)
}
