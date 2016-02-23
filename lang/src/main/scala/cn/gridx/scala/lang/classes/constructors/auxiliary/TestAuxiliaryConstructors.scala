package cn.gridx.scala.lang.classes.constructors.auxiliary

/**
 * Created by tao on 7/17/15.
 */
object TestAuxiliaryConstructors {
    /**
     * 输出为

        Primary constructor => Tom, Audi

        ----------------
        Primary constructor => Kim, BMW

        auxiliary constructor 1 => 5
        ----------------
        Primary constructor => undefined, undefined

        auxiliary constructor 2 => zero-arg constructor
        ----------------
        Primary constructor => Kim, BMW

        auxiliary constructor 1 => 5
        auxiliary constructor 3 => volumn = 3

     */
    def main(args: Array[String]): Unit = {
        new Car("Tom", "Audi")
        print("----------------\n")

        new Car("Kim", "BMW", "5")
        print("----------------\n")

        new Car()
        print("----------------\n")

        new Car("Kim", "BMW", "5", 3)
    }
}
