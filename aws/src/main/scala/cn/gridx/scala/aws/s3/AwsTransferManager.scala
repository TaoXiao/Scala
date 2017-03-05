package cn.gridx.scala.aws.s3

import java.io.File

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.transfer.TransferManager

/**
  * Created by tao on 9/22/16.
  */
object AwsTransferManager extends App {
  var srcPath     : String  = "/Users/tao/IdeaProjects/gridx/pipeline-common/build/deploy/akka/conf"
  var dstBucket   : String  = "zk-realtime-configs"
  var dstKey      : String  = "SA-ID/test"

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
