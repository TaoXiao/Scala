package cn.gridx.scala.akka.tutorial

import akka.actor.{Props, ActorRef, Actor}
import akka.routing.RoundRobinRouter


/**
  * Created by tao on 2/25/16.
  */
object Pi {
  def calcPiFor(start: Int, num: Int): Double = {
    var sum:Double = 0.0
    for (i <- start until (start + num))
      sum += 4.toDouble * (i % 2 * 2 - 1) / (2 * i - 1)
    sum
  }


  sealed trait PiMsg
  case object Msg_Calculate extends PiMsg
  case class Msg_Work(start: Int, numElements: Int) extends PiMsg
  case class Msg_Result(value: Double) extends PiMsg
  case class Msg_PiApprox(value: Double, duration: Long) extends PiMsg



  class Worker extends Actor {
    def receive = {
      case Msg_Work(start, numElements) =>
        sender ! Msg_Result(Pi.calcPiFor(start, numElements))
    }
  }
}

