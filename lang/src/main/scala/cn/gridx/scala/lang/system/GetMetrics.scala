package cn.gridx.scala.lang.system

/**
  * Created by tao on 11/4/16.
  */
object GetMetrics {
  val runtime = Runtime.getRuntime

  def main(args: Array[String]): Unit = {
    println("#1. ###################################")
    getJvmMemInfo()
    
    val set1 = {
      for (i <- 0 until 199999) yield
        "AAAAAA" * 100
    }
    println("\n#2. ###################################")
    getJvmMemInfo()

    println("\n#3. ###################################")
    val set2 = {
      for (i <- 0 until 199999) yield
        "AAAAAA" * 100
    }
    getJvmMemInfo()

    println("\n#2. ###################################")
    val set3 = {
      for (i <- 0 until 199999) yield
        "AAAAAA" * 100
    }
    getJvmMemInfo()
  }

  def getJvmMemInfo(): Unit = {
    def MB = 1024*1024
    val freeMem    = runtime.freeMemory()/1024/1024
    val maxMem     = runtime.maxMemory()/1024/1024
    /* Total memory currently available to the JVM */
    val totalMem   = runtime.totalMemory()/1024/2014
    val processors = runtime.availableProcessors()


    println(s"free memory of this JVM  = $freeMem MB")
    println(s"max memory of this JVM   = $maxMem MB")
    println(s"total memory of this JVM = $totalMem MB")
    println(s"Used memory = ${(runtime.totalMemory() - runtime.freeMemory())/MB}")
    println(s"processors = $processors")
  }
}
