package cn.gridx.scala.aws.s3

import java.io.File
import java.util.Date

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest

/**
  * Created by tao on 4/15/16.
  *
  * 将本地的一个文件upload到s3的一个bucket中
  */
object UploadSingleFile extends App {
  def BucketName = "gridx-meter-coverage-filter"  // 必须事先存在
  def S3ObjKey   = "2016/生产许可.docx"   // s3上的目标路径(在bucket内)
  def AccessKey  = "xxxx"
  def AccessSecret = "yyyy"
  def LocalFilePath = "zzzz.txt"  // 欲上传的本地文件的路径

  println(s"[${new Date()}] 开始")

  // 上传文件至s3时不需要指定region
  val s3Client = new AmazonS3Client(
    new BasicAWSCredentials(AccessKey, AccessSecret))
  s3Client.putObject(new PutObjectRequest(BucketName, S3ObjKey, new File(LocalFilePath)))

  println(s"[${new Date()}] 结束")
}
