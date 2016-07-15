package cn.gridx.scala.database.redis.basic

import java.util.Date

import com.redis._

import scala.collection.immutable.IndexedSeq
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by tao on 7/14/16.
  */
object Test {
  def main(args: Array[String]): Unit = {
    val redisClient = new RedisClient("localhost", 6379)
    println(redisClient.get("mykey"))

    val clients = new RedisClientPool("localhost", 6379)
    val numbers = Range(1, 2000).toList



    val tasks =
      for (i <- Range(0, 10000)) yield Future {
        clients.withClient {
          client => {
            val v = client.get("kk")
            if (v.isEmpty)
              client.set("kk", 1)
            else {
              println(i + " : " + v.get)
              client.set("kk", v.get.toInt + 1)
            }
          }
        }
      }

    Await.result(Future.sequence(tasks), 10 seconds)

    println("结束")
  }
}
