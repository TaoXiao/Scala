package cn.gridx.scala.lang.system

/**
  * Created by tao on 11/4/16.
  */
object GetMetrics {
  val runtime = Runtime.getRuntime

  def main(args: Array[String]): Unit = {
    getJvmMemInfo()


  }

  def getJvmMemInfo(): Unit = {
    val freeMem   = runtime.freeMemory()/1024/1024
    val maxMem    = runtime.maxMemory()/1024/1024

    /* Total memory currently available to the JVM */
    val totalMem  = runtime.totalMemory()/1024/2014


    println(s"free memory of this JVM = $freeMem MB")
    println(s"max memory of this JVM = $maxMem MB")
    println(s"total memory of this JVM = $totalMem MB")
  }
}
