package cn.gridx.scala.akka

/**
  * Created by tao on 7/31/16.
  */
package object management {
  trait MSG
  case class UnsupportedMsg() extends MSG

  // type MsgProcessor[T <: MSG] = (T) => Unit

  // def MessageHandler()

}
