package cn.gridx.scala.lang.json

import com.google.gson.{GsonBuilder, JsonObject}

/**
  * Created by tao on 5/23/16.
  */
object GsonUsage extends App {
  val builder = new GsonBuilder().serializeNulls()
  val gson = builder.create()

  val arr = new java.util.ArrayList[Bean]()
  arr.add(Bean("a", true))
  arr.add(Bean("b", false))

  val jRoot = new JsonObject
  jRoot.add("root", gson.toJsonTree(arr))

  println(gson.toJson(jRoot))

}

case class Bean(win: String, lose: Boolean)
