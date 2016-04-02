package cn.gridx.scala.aws.simpledb

import java.util

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.simpledb.AmazonSimpleDBClient
import com.amazonaws.services.simpledb.model._

/**
  * Created by tao on 3/30/16.
  */
object DataOps {

  def CassCluster = "dseCassandraCluster"

  def PriamPropertiesDomain = "PriamProperties"

  def main(args: Array[String]): Unit = {
    def AccessKey = args(0)
    def SecreteKey = args(1)

    val dbClient = new AmazonSimpleDBClient(new BasicAWSCredentials(AccessKey, SecreteKey))
    dbClient.setRegion(Region.getRegion(Regions.US_EAST_1))

    // PutData(dbClient)
    // ReadData(dbClient)
    // DeletePriamAttrs(dbClient)
    // WritePriamAttrs(dbClient)
    ReadPriamAttrs(dbClient)
  }

  /** *
    * 写入一些数据
    *
    * @param dbClient
    */
  def PutData(dbClient: AmazonSimpleDBClient, domain: String, item: String): Unit = {
    println("开始写入数据")
    val attrs = new java.util.ArrayList[ReplaceableAttribute]()
    attrs.add(new ReplaceableAttribute("N1", "V1", false))
    attrs.add(new ReplaceableAttribute("N2", "V2", false))

    dbClient.putAttributes(new PutAttributesRequest(domain, item, attrs))
    println("写入数据完毕")
  }


  /** *
    * 读取数据
    *
    * @param dbClient
    */
  def ReadData(dbClient: AmazonSimpleDBClient, domain: String, item: String) = {
    /*
    println("正在查询存在的domains ... ")
    val domainIt = dbClient.listDomains.getDomainNames.iterator
    while (domainIt.hasNext)
      println(domainIt.next)
    println("domain查询完毕...")
    */

    val result: GetAttributesResult = dbClient.getAttributes(new GetAttributesRequest(domain, item))
    val attrs: util.List[Attribute] = result.getAttributes
    val it = attrs.iterator
    println(s"item -> $item")
    while (it.hasNext) {
      val attr: Attribute = it.next
      println(s"${attr.getName} -> ${attr.getValue} ")
    }
    println("\n")
  }


  /**
    * 删除一些数据
    *
    * @param dbClient
    */
  def DeleteData(dbClient: AmazonSimpleDBClient, domain: String, item: String): Unit = {
    dbClient.deleteAttributes(new DeleteAttributesRequest(domain, item))
  }


  /** *
    * 写入Netflix Priam所需的属性
    *
    * @param dbClient
    */
  def WritePriamAttrs(dbClient: AmazonSimpleDBClient): Unit = {
    def AppId = "ASG_CassCluster"

    println("正在查询存在的domains ... ")
    val domainIt = dbClient.listDomains.getDomainNames.iterator
    while (domainIt.hasNext)
      println(domainIt.next)
    println("domain查询完毕...")

    var attrs = new java.util.ArrayList[ReplaceableAttribute]()
    attrs.add(new ReplaceableAttribute("appId", AppId, false))
    attrs.add(new ReplaceableAttribute("property", "priam.clustername", false))
    attrs.add(new ReplaceableAttribute("value", CassCluster, false))
    dbClient.putAttributes(new PutAttributesRequest(PriamPropertiesDomain, s"${CassCluster}.priam.clustername", attrs))


    attrs = new java.util.ArrayList[ReplaceableAttribute]()
    attrs.add(new ReplaceableAttribute("appId", AppId, false))
    attrs.add(new ReplaceableAttribute("property", "priam.cass.home", false))
    attrs.add(new ReplaceableAttribute("value", "/usr/share/dse/cassandra", false))
    dbClient.putAttributes(new PutAttributesRequest(PriamPropertiesDomain, s"${CassCluster}.priam.cass.home", attrs))

    attrs = new java.util.ArrayList[ReplaceableAttribute]()
    attrs.add(new ReplaceableAttribute("appId", AppId, false))
    attrs.add(new ReplaceableAttribute("property", "priam.cass.startscript", false))
    attrs.add(new ReplaceableAttribute("value", "sudo /etc/init.d/dse start", false))
    dbClient.putAttributes(new PutAttributesRequest(PriamPropertiesDomain, s"${CassCluster}.priam.cass.startscript", attrs))

    attrs = new java.util.ArrayList[ReplaceableAttribute]()
    attrs.add(new ReplaceableAttribute("appId", AppId, false))
    attrs.add(new ReplaceableAttribute("property", "priam.cass.stopscript", false))
    attrs.add(new ReplaceableAttribute("value", "sudo /etc/init.d/dse stop", false))
    dbClient.putAttributes(new PutAttributesRequest(PriamPropertiesDomain, s"${CassCluster}.priam.cass.stopscript", attrs))

    attrs = new java.util.ArrayList[ReplaceableAttribute]()
    attrs.add(new ReplaceableAttribute("appId", AppId, false))
    attrs.add(new ReplaceableAttribute("property", "priam.data.location", false))
    attrs.add(new ReplaceableAttribute("value", "/data/cassandra/data", false))
    dbClient.putAttributes(new PutAttributesRequest(PriamPropertiesDomain, s"${CassCluster}.priam.data.location", attrs))

    attrs = new java.util.ArrayList[ReplaceableAttribute]()
    attrs.add(new ReplaceableAttribute("appId", AppId, false))
    attrs.add(new ReplaceableAttribute("property", "priam.cache.location", false))
    attrs.add(new ReplaceableAttribute("value", "/data/cassandra/saved_caches", false))
    dbClient.putAttributes(new PutAttributesRequest(PriamPropertiesDomain, s"${CassCluster}.priam.cache.location", attrs))

    attrs = new java.util.ArrayList[ReplaceableAttribute]()
    attrs.add(new ReplaceableAttribute("appId", AppId, false))
    attrs.add(new ReplaceableAttribute("property", "priam.commitlog.location", false))
    attrs.add(new ReplaceableAttribute("value", "/data/cassandra/commitlog", false))
    dbClient.putAttributes(new PutAttributesRequest(PriamPropertiesDomain, s"${CassCluster}.priam.commitlog.location", attrs))

    attrs = new java.util.ArrayList[ReplaceableAttribute]()
    attrs.add(new ReplaceableAttribute("appId", AppId, false))
    attrs.add(new ReplaceableAttribute("property", "priam.s3.bucket", false))
    attrs.add(new ReplaceableAttribute("value", "gridx-cassandra-archive", false))
    dbClient.putAttributes(new PutAttributesRequest(PriamPropertiesDomain, s"${CassCluster}.priam.s3.bucket", attrs))

    attrs = new java.util.ArrayList[ReplaceableAttribute]()
    attrs.add(new ReplaceableAttribute("appId", AppId, false))
    attrs.add(new ReplaceableAttribute("property", "priam.yamlLocation", false))
    attrs.add(new ReplaceableAttribute("value", "/etc/dse/cassandra/cassandra.yaml", false))
    dbClient.putAttributes(new PutAttributesRequest(PriamPropertiesDomain, s"${CassCluster}.priam.yamlLocation", attrs))
  }


  /**
    * 查看 `PriamProperties` domain 中的相关配置
    **/
  def ReadPriamAttrs(dBClient: AmazonSimpleDBClient): Unit = {
    ReadData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.clustername"       )
    ReadData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.cass.home"         )
    ReadData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.cass.startscript"  )
    ReadData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.cass.stopscript"   )
    ReadData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.data.location"     )
    ReadData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.cache.location"    )
    ReadData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.commitlog.location")
    ReadData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.s3.bucket"         )
    ReadData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.yamlLocation"      )
  }

  def DeletePriamAttrs(dBClient: AmazonSimpleDBClient): Unit = {
    DeleteData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.clustername"       )
    DeleteData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.cass.home"         )
    DeleteData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.cass.startscript"  )
    DeleteData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.cass.stopscript"   )
    DeleteData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.data.location"     )
    DeleteData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.cache.location"    )
    DeleteData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.commitlog.location")
    DeleteData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.s3.bucket"         )
    DeleteData(dBClient, PriamPropertiesDomain, s"${CassCluster}.priam.yamlLocation"      )
  }

  /** *
    * 查看 `InstanceIdentity` domaim 中的相关配置
    */
  def ReadInstanceIdentity(dBClient: AmazonSimpleDBClient): Unit = {

  }
}