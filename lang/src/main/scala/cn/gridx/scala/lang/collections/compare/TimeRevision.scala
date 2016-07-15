package cn.gridx.scala.lang.collections.compare

/**
  * Created by tao on 7/9/16.
  */
trait TimeRevision extends Serializable {
  def startDate: Long

  def endDate: Long

  //real datetime after covertion
  def start_dateTime = startDate * 1000

  def end_dateTime = if (endDate == 0) 2145916800000L else endDate * 1000 //if (endDate == null || endDate.isEmpty) DateTime.parse("3000-1-1") else SlickTypes2.startEndFormater.parseDateTime(endDate)

}
