package cn.gridx.scala.lang.classes.constructors.fields.basics

/**
 * Created by tao on 12/30/15.
 */
private [basics] class Audi(
    model: String,
    volume: Double) {

    // pricr是类Audi的public property
    var price = model match {
        case "A6L" => 35.5
        case "A4L" => 29.8
        case "A8L" => 86.8
        case _     => -1.0
    }
}

object Audi extends App {
    val audi = new Audi("A6L", 2.0)

    // println(audi.model)  // 不能访问 model 和 volume
    println(audi.price)     // 可以访问 price
    audi.price = 100        // 可以修改price
}
