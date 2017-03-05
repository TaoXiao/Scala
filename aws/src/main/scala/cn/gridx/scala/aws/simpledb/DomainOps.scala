package cn.gridx.scala.aws.simpledb

import com.amazonaws.auth.{PropertiesCredentials, BasicAWSCredentials}
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.simpledb.AmazonSimpleDBClient
import com.amazonaws.services.simpledb.model.{CreateDomainRequest, DeleteDomainRequest, ListDomainsResult}

/**
  * Created by tao on 3/30/16.
  *
  * 在 AWS SimpleDB 中创建 Domain,
  * 参考 http://stackoverflow.com/questions/15793602/java-create-domain-in-amazon-simpledb
  *
  */
object DomainOps {

  def main(args: Array[String]): Unit = {
    def AccessKey  = "AKIAIF6BEVAR6YJSEWWA" // args(0)
    def SecreteKey = "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" // args(1)

    val credentials = new BasicAWSCredentials(AccessKey, SecreteKey)
    //val credentials = new PropertiesCredentials(getClass.getResourceAsStream())
    val dbClient = new AmazonSimpleDBClient(credentials)
    dbClient.setRegion(Region.getRegion(Regions.US_EAST_1))

    // 这两个 domains 是为了使用 Netflix Priam 而创建的
    // 必须位于 US_WEST_1
    /*
    CreateDomain(dbClient, "InstanceIdentity")
    CreateDomain(dbClient, "PriamProperties")
    */
    ListDomains(dbClient)
  }

  def CreateDomain(dbClient: AmazonSimpleDBClient, domainName: String): Unit = {
    dbClient.createDomain(new CreateDomainRequest(domainName))
  }

  def ListDomains(dbClient: AmazonSimpleDBClient): Unit = {
    val domains: ListDomainsResult = dbClient.listDomains
    val it = domains.getDomainNames.iterator
    while (it.hasNext)
      println(it.next)
  }

  def DeleteDomain(dbClient: AmazonSimpleDBClient, domainName: String): Unit = {
    dbClient.deleteDomain(new DeleteDomainRequest(domainName))
  }

}
