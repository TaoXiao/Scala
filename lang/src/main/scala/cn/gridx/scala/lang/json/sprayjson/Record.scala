package cn.gridx.scala.lang.json.sprayjson

import spray.json.DefaultJsonProtocol

/**
  * Created by tao on 11/22/16.
  */
case class Record(id: Int, description: String)

case class MoreRecord(ip: String, r: Option[Record])


object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val recordFormat       = jsonFormat2(Record)
  implicit val moreRecordFormat   = jsonFormat2(MoreRecord)
}
