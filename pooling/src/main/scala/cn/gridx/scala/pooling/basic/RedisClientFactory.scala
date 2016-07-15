package cn.gridx.scala.pooling.basic

import com.redis.RedisClient
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.{BasePooledObjectFactory, PooledObject}

/**
  * Created by tao on 7/14/16.
  */
class RedisClientFactory(host: String, port: Int) extends BasePooledObjectFactory[RedisClient] {
  override def wrap(client: RedisClient): PooledObject[RedisClient] =
    new DefaultPooledObject[RedisClient](client)

  override def create(): RedisClient = new RedisClient(host, port)
}
