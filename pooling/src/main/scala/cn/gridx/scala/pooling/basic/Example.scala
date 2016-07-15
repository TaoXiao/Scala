package cn.gridx.scala.pooling.basic

import com.redis.{RedisClient, Seconds}
import org.apache.commons.pool2.impl.GenericObjectPool

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

/**
  * Created by tao on 7/14/16.
  */
object Example {
  def Host = "localhost"

  def Port = 6379

  def main(args: Array[String]): Unit = {

    val pool = new GenericObjectPool[RedisClient](new RedisClientFactory(Host, Port))
    pool.setMaxTotal(30)
    //pool.setMaxIdle(0)
    pool.setBlockWhenExhausted(true)

    val manager = new RedisConnManager(pool)

    val tasks =
      for (i <- Range(0, 5000)) yield Future {
        val client = manager.borrowConn()
        val v = client.get("kk")

        if (v.isEmpty)
          client.set("kk", 1)
        else {
          println(i + " : " + v.get)
          client.set("kk", v.get.toInt + 1)
        }

        Thread.sleep(100)

        manager.returnConn(client)
      }

    Await.result(Future.sequence(tasks), 2000 seconds)
    println("down")


  }
}
