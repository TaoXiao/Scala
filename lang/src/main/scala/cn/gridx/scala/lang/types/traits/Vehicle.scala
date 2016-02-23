package cn.gridx.scala.lang.types.traits

/**
 * Created by tao on 11/20/15.
 */
trait Vehicle {
    def getBrand():String
    def getType(): String = "Type <vehicle>"
    def getProduct():String
}
