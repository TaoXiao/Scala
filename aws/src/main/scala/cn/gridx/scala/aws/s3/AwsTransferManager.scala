package cn.gridx.scala.aws.s3

import java.io.File

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.transfer.TransferManager

/**
  * Created by tao on 9/22/16.
  */
object AwsTransferManager extends App {
  var srcPath     : String  = "/Users/tao/Dropbox/realtime/src"
  var dstBucket   : String  = "gridx-realtime"
  var dstKey      : String  = "crossfilter/xxx/yyyy/yili"

  val credentials = new ProfileCredentialsProvider()
  val tx = new TransferManager(credentials)
  val uploader = tx.uploadDirectory(dstBucket, dstKey, new File(srcPath), true)

  while (!uploader.isDone()) {
    println(s"""Transfer: ${uploader.getDescription()}
      | - State: ${uploader.getState()}
      | - Progress: ${uploader.getProgress().getBytesTransferred()}""".stripMargin)

    Thread.sleep(3000)
  }


  println("Transfer done !")

  tx.shutdownNow()

}
