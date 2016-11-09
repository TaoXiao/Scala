package cn.gridx.scala.akka.ha

/**
  * Created by tao on 9/20/16.
  */
object Messages {
  final case class DoHeavyWork()
  final case class HeavyWorkDone()
  final case class Register()
  final case class HeartBeat(no: Int)
  final case class PoolingMaster(no: Int)
  final case class SyncUp()
}
