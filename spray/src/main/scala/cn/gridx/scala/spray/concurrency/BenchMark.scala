package cn.gridx.scala.spray.concurrency

import java.util.concurrent.atomic.AtomicInteger

import scalaj.http.{Http, HttpOptions, HttpResponse}

/**
  * Created by tao on 7/12/16.
  */
object BenchMark {
  var  count: AtomicInteger = new AtomicInteger(0)

  def main(args: Array[String]): Unit = {
    Boot.main(null)
    Thread.sleep(3000)

    for (i <- 0 until 200) {
      val thread = new Thread(new Runnable {
        override def run(): Unit = {
          // Thread.sleep(2)
          // println(count.incrementAndGet())
          query()
        }
      })

      thread.start()
    }
  }

  def query(): Unit = {
    println("发起请求")
    val url = "http://localhost:8813/spray/test/concurrency"
    val resp: HttpResponse[String] = Http(url)
      .option(HttpOptions.connTimeout(60000))
      .option(HttpOptions.readTimeout(60000))
      .asString
    val body: String = resp.body
    println(s"#${count.incrementAndGet()}: 收到响应")
  }
}
