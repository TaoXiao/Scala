package cn.gridx.scala.aws.elb

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient
import com.amazonaws.services.elasticloadbalancing.model.{Instance, RegisterInstancesWithLoadBalancerRequest}
import scala.collection.JavaConverters._

/**
  * Created by tao on 3/5/17.
  */
object RegisterInstances {
  def main(args: Array[String]): Unit = {
    val region = Regions.US_WEST_2.getName
    val instanceIDs = List("i-036707a13d12dd2bc", "i-0ab71d63e08b5afe8")
    val lbName = "tao-load-balancer"
    RegisterInstancesToLoadBalancer(region, instanceIDs, lbName)
  }



  /**
    * 把若干个给定的instance加入到一个已有的load balancer中
    * 前提是这个load balancer已经配置好了(例如listner已经配置好),且正在运行
    *
    * 即使提供的instance已经在load balancer中了, 重复注册也没关系
    * */
  private def RegisterInstancesToLoadBalancer(regionName: String, instanceIds: List[String], loadBalancerName: String): Unit = {
    val credentials = new ProfileCredentialsProvider()
    val elbClient   = new AmazonElasticLoadBalancingClient(credentials)
    /** 必须为 elbClient 指定正确的region, 否则后面会报: no active load balancer  */
    elbClient.withRegion(Region.getRegion(Regions.fromName(regionName)))

    // String(instance id) => com.amazonaws.services.elasticloadbalancing.model.Instance(instance)
    val instanceList = instanceIds.map(id => {
      val instance = new Instance()
      instance.setInstanceId(id)
      instance
    }).asJavaCollection

    val elbRegiReq = new RegisterInstancesWithLoadBalancerRequest()
    elbRegiReq.setLoadBalancerName(loadBalancerName)
    elbRegiReq.setInstances(instanceList)

    val result = elbClient.registerInstancesWithLoadBalancer(elbRegiReq)
    println(s"result = \n$result")
  }

}
