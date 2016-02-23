package cn.gridx.scala.lang.classes.constructors.privateConstructor

/**
 * Created by tao on 8/20/15.
 *
 * You want to make the primary constructor of a class private,
 * such as to enforce the Singleton pattern.
 */
class Person private (name:String, home:String) {
    var count = 0
    println(s"Name = $name, Home=$home")

    def addCount() = {
        count += 1
    }
}


/**
 * A simple way to enforce singleton pattern in Scala is to
 * make the primary constrcutor `private` and put a `getInstance`
 * method in the companion object of the class.
 *
 * Any method declared in a companion object will appear to be
 * a static method on the object
 */
object Person {
    val instance = new Person("肖", "南京")
    def getInstance = { instance.addCount(); instance }
}