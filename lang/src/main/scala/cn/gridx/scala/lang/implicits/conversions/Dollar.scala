package cn.gridx.scala.lang.implicits.conversions;

/**
 * Created by tao on 11/24/15.
 */
class Dollar(private val v: Double) {
    def getValue() :Double = v
    def toRMB(): Double = Dollar.rate2RMB*getValue

    def addRMB(rmb: RMB) :Dollar = new Dollar(v + RMB.rate2Dollar*rmb.getValue)

    override
    def toString() = s"""Dollar: ${getValue()},\tRMB: ${toRMB()}"""
}


object Dollar {
    def rate2RMB: Double = 8.0    // 美元兑人民币的汇率为1：8
    implicit def Dollar2RMB(dollar: Dollar): RMB = new RMB(Dollar.rate2RMB*dollar.getValue)
}
