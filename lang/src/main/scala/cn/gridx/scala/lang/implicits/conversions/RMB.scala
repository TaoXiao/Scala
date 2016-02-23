package cn.gridx.scala.lang.implicits.conversions

/**
 * Created by tao on 11/24/15.
 */
class RMB(private val v: Double) {
    def getValue(): Double = v
    def toDollar(): Double = RMB.rate2Dollar*v
    def addDollar(doll: Dollar): RMB = new RMB(getValue + Dollar.rate2RMB*doll.getValue)

    override
    def toString() = s"""RMB: ${v},\tDollar: ${toDollar()}"""
}

object RMB {
    def rate2Dollar: Double = 1/8.0     // 人民币兑美元的汇率为1/8

    // 这个implicit conversion可以位于`object RMB`中，也可以位于`object Euro`中
    implicit def rmb2Euro(rmb: RMB):Euro = new Euro(rmb.getValue()/Euro.rate2RMB)
}


