package cn.gridx.gradle.plugins.s3


import java.io.File

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.transfer.TransferManager
import org.gradle.api.{DefaultTask, Plugin, Project}
import org.gradle.api.tasks.TaskAction

import scala.collection.mutable.ArrayBuffer


/**
  * Created by tao on 9/22/16.
  * We can upload a local folder or a local file to S3
  */
class S3SyncTask extends DefaultTask {
  var accessKey   : String  = null
  var secreteKey  : String  = null

  val locations = ArrayBuffer[Location]()

  @TaskAction
  def upload(): Unit = {
    require(accessKey   != null && !accessKey.isEmpty   , "Please set accessKey")
    require(secreteKey  != null && !secreteKey.isEmpty  , "Please set secreteKey")

    if (locations.isEmpty) {
      println("No files to be uploaded")
    } else {
      val tx = new TransferManager(new BasicAWSCredentials(accessKey, secreteKey))
      for (loc <- locations)
        upload(tx, loc.srcPath, loc.dstBucket, loc.dstKey)
      tx.shutdownNow()
      println("All files are uploaded .")
    }
  }

  private def upload(tx: TransferManager, srcPath: String, dstBucket: String, dstKey: String): Unit = {
    val f = new File(srcPath)
    if (f.isFile)
      uploadFile(tx, srcPath, dstBucket, dstKey)
    else
      uploadFolder(tx, srcPath, dstBucket, dstKey)
  }


  private def uploadFile(tx: TransferManager, srcPath: String, dstBucket: String, dstKey: String): Unit = {
    val uploader = tx.upload(dstBucket, dstKey, new File(srcPath))

    print(s"""$srcPath -> s3://$dstBucket/$dstKey """)
    while (!uploader.isDone) {
    }

    println(" done")
  }

  private def uploadFolder(tx: TransferManager, srcPath: String, dstBucket: String, dstKey: String): Unit = {
    print(s"$srcPath -> s3://$dstBucket/$dstKey")

    val uploader = tx.uploadDirectory(dstBucket, dstKey, new File(srcPath), true)
    while (!uploader.isDone()) {
    }

    println(s" done")
  }

  def setAccessKey(key: String) = {
    accessKey = key
  }

  def setSecreteKey(key: String) = {
    secreteKey = key
  }


  def addLocation(srcPath: String, dstBucket: String, dstKey: String): Unit = {
    locations.append(Location(srcPath, dstBucket, dstKey))
  }
}


class S3SyncPlugin extends Plugin[Project] {
  override def apply(target: Project): Unit = {
    target.task("upload")
  }
}


case class Location(srcPath: String, dstBucket: String, dstKey: String)