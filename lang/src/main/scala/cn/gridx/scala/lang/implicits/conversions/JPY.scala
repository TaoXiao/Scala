package cn.gridx.scala.lang.implicits.conversions

/**
 * Created by tao on 11/24/15.
 */
class JPY(private val v: Double) {
    def getValue(): Double = v
    //def + (x: Double): JPY = new JPY(x + getValue())
    def + (x: JPY): JPY = new JPY(x.getValue() + getValue())

    override
    def toString(): String = s"""JPY: ${getValue()},\tRMB: ${getValue()/JPY.rate2RMB}"""
}

object JPY {
    def rate2RMB: Double = 20.0
    implicit def double2JPY(x: Double): JPY = new JPY(x)
}
