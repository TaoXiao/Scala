package cn.gridx.scala.lang.traits.This

/**
  * Created by tao on 3/11/16.
  *
  * 运行输出为:
    [Engine] Starting ... (fuel = Diesel)
    [Engine] Ending ... (fuel = Diesel)

    [Driver] Hello , I am Female
    [Engine] Starting ... (fuel = Diesel)
    [Driver] Bye bye
    [Engine] Ending ... (fuel = Diesel)
  */
object AutoWorks extends App {
  val car = new Accord()
  car.drive
  car.park

  println("\n")

  val bus: JingLong.type = JingLong
  bus.drive
  bus.park
}

/////////////////////     Engine   //////////////////////////////
object FuelType extends Enumeration {
  val Gas, Diesel, Petrol = Value
}

trait Engine {
  def start(): Unit = { println(s"[Engine] Starting ... (fuel = $fuel)")  }
  def end(): Unit = { println(s"[Engine] Ending ... (fuel = $fuel)") }
  def fuel: FuelType.Value
}

trait DieselEngine extends Engine {
  override def fuel = FuelType.Diesel
}

trait PetrolEngine extends Engine {
  override def fuel = FuelType.Petrol
}


///////////////////        Driver     /////////////////////////////////////////\
object Gender extends Enumeration {
  val Male, Female = Value
}

trait Driver {
  def greeting = { println(s"[Driver] Hello , I am $gender") }
  def bye = { println(s"[Driver] Bye bye") }
  def gender: Gender.Value
}

trait Lucy extends Driver {
  override def gender = Gender.Female
}



///////////////////        Car     /////////////////////////////////////////
trait Car {
  this: Engine => // self type

  def drive = { start }
  def park = { end }
}

trait Sedan {
  // self type can be structure
  engine : {
    def start: Unit
    def end: Unit
    // def fuel: FuelType.Value
  } =>

  def drive = { engine.start }
  def park = { engine.end }
}


trait Bus {
  this: Engine with Driver => // trait可以要求多个dependencies (用`with`连接)

  def drive = { greeting; start }
  def park = { bye; end }
}

class Accord extends Car with PetrolEngine {

}

object JingLong extends Bus with DieselEngine with Lucy {

}


