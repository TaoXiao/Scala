package cn.gridx.scala.pooling.basic

import com.redis.RedisClient
import org.apache.commons.pool2.ObjectPool

/**
  * Created by tao on 7/14/16.
  */
class RedisConnManager(pool: ObjectPool[RedisClient]) {
  def borrowConn(): RedisClient = pool.borrowObject()
  def returnConn(client: RedisClient) = pool.returnObject(client)
}
