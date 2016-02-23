package cn.gridx.scala.lang.classes.case_class

/**
 * Created by tao on 7/17/15.
 *
 *  Case class constructor parameters are `val` by default.
 *
 *  A case class is a special type of class that generates a lot of
 *  boilerplate code for you. Their "constructors" are actually their
 *  `apply` methods in the companion object of the class.
 *
 */
case class Person(name: String, age: Int) {
    println(s"Person(String, int):  name=${name},  age=${age}")
}


/**
 * If you want to add new "constructors" to your case classes,
 * you write new `apply` methods in the companion object of
 * the case class.
 */
object Person {
    def apply() = new Person("[Unknown Name]", -1)
    def apply(name: String) = new Person(name, -1)
    def apply(age: Int) = new Person("[Unknown Name]", age)
}
