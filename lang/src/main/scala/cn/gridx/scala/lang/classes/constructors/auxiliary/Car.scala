package cn.gridx.scala.lang.classes.constructors.auxiliary

/**
 * Created by tao on 7/17/15.
 *
 * · Define the auxiliary constructors as methods in the class with the name `this`.
 * · You can define multiple auxiliary constructors, but they must have different signatures (parameter lists).
 * · Also, each constructor must call one of the previously defined constructors.
 */
class Car(name:String, brand:String) {
    println(s"Primary constructor => ${name}, ${brand} \n")

    // auxiliary constructor 1
    def this(name:String, brand:String, series: String) {
        this(name, brand)
        println(s"auxiliary constructor 1 => ${series}")
    }

    // auxiliary constructor 2
    def this() {
        this("undefined", "undefined")
        println("auxiliary constructor 2 => zero-arg constructor")
    }

    // auxiliary constructor 3
    def this(name:String, brand:String, series: String, volumn:Int) {
        this(name, brand, series)
        println(s"auxiliary constructor 3 => volumn = ${volumn}")
    }
}
