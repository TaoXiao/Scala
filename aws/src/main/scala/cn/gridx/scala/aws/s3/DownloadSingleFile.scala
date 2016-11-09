package cn.gridx.scala.aws.s3

import java.io._

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest

/**
  * Created by tao on 8/29/16.
  */
object DownloadSingleFile extends App {
  def Bucket = "gridx-realtime"
  def Key = "crossfilter/tariffs/cal_20160526_e1_impact_0.9570"

  val s3client = new AmazonS3Client(new ProfileCredentialsProvider())
  val s3Obj = s3client.getObject(
    new GetObjectRequest(Bucket, Key))

  val localFile: String = {
    if (Key.contains("/"))
      Key.substring(Key.lastIndexOf("/")+1, Key.length)
    else
      Key
  }

  val f = new File(localFile)

  if (!f.exists()) {
    println("downloading ...")
    val reader = new BufferedReader(new InputStreamReader(s3Obj.getObjectContent()))
    val writer = new PrintWriter(f)
    var line: String = reader.readLine()
    var count = 0

    do  {
      writer.println(line)
      count += 1
      if (count % 500 == 0)
        println(s"#$count")
      line = reader.readLine()
    } while (null != line )

    writer.close()
    println(s"写入了 $count 行")

  } else {
    println("already exists !")
  }

}
