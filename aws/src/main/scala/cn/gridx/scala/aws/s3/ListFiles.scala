package cn.gridx.scala.aws.s3

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{ListObjectsRequest, ObjectListing}

import scala.collection.JavaConversions._

/**
  * Created by tao on 8/29/16.
  */
object ListFiles {
  def main(args: Array[String]): Unit = {
    listObjects("gridx-realtime", "crossfilter/tariffs")
  }

  /**
    * 列出某个路径下的全部Objects文件名
    * 参考 【http://stackoverflow.com/questions/23217951/aws-s3-listing-all-objects-inside-a-folder-without-the-prefix】
    * */
  def listObjects(bucket: String, folder: String): Unit = {
    val s3client = new AmazonS3Client(new ProfileCredentialsProvider())
    val req = new ListObjectsRequest().withBucketName(bucket).withPrefix(folder).withMarker(folder).withMaxKeys(20)

    val results: ObjectListing = s3client.listObjects(req)
    for (res <- results.getObjectSummaries)
      println(res.getKey)
  }
}
