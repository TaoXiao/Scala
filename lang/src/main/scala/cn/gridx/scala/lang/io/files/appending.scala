package cn.gridx.scala.lang.io.files

import java.io.{FileWriter, PrintWriter}

/**
  * Created by tao on 9/1/16.
  */
object appending extends App {
  var writer = new PrintWriter(new FileWriter("append.txt", true))
  writer.append("hello\n")
  writer.append("world\n")
  writer.close()

  writer = new PrintWriter(new FileWriter("append.txt", true))
  writer.append("bye bye\n")
  writer.append("world!\n")
  writer.close()

  for (x <- 100 to 99 by 1)
    println(x)
}
