package cn.gridx.scala.lang.implicits.conversions

/**
 * Created by tao on 11/24/15.
 */
class Euro(private val v: Double) {
    def getValue(): Double = v
    def addEuro(euro: Euro): Euro = new Euro(getValue() + euro.getValue())
    def toRMB(): Double = Euro.rate2RMB*getValue()

    override
    def toString(): String = s"""Euro: ${getValue()},\tRMB: ${toRMB()}"""
}

object Euro {
    def rate2RMB = 10.0
}
