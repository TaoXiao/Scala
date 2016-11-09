package cn.gridx.scala.aws.ec2

import java.util
import collection.JavaConversions._


import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.ec2.{AmazonEC2, AmazonEC2Client}
import com.amazonaws.services.ec2.model._

/**
  * Created by tao on 8/9/16.
  */
class EC2Manager {
  var ec2: AmazonEC2 = _

  // 初始化
  init("us-west-2")

  private def init(region: String) = {
    // 将从本地目录 ~/.aws/credentials 中读取配置好的access key 和 secrete key
    ec2 = new AmazonEC2Client(new ProfileCredentialsProvider())
    ec2.setRegion(Region.getRegion(Regions.fromName(region)))
  }


  /**
    *
    * 结果实例:
    *
      launch group = launch-group-azkaban-dev_daily_end_to_end_automation_meter, state = active, status = {Code: fulfilled,UpdateTime: Sun Aug 14 10:02:34 CST 2016,Message: Your Spot request is fulfilled.}
      launch group = launch-group-azkaban-dev_daily_end_to_end_automation_meter, state = active, status = {Code: fulfilled,UpdateTime: Sun Aug 14 10:02:34 CST 2016,Message: Your Spot request is fulfilled.}
      launch group = tao_ami_nodes, state = active, status = {Code: fulfilled,UpdateTime: Sun Aug 14 23:31:58 CST 2016,Message: Your Spot request is fulfilled.}
      launch group = tao_ami_nodes, state = active, status = {Code: fulfilled,UpdateTime: Sun Aug 14 23:31:57 CST 2016,Message: Your Spot request is fulfilled.}
      launch group = tao-akka, state = cancelled, status = {Code: instance-terminated-by-user,UpdateTime: Mon Aug 15 08:19:30 CST 2016,Message: Spot Instance terminated due to user-initiated termination.}
      launch group = tao-akka, state = cancelled, status = {Code: instance-terminated-by-user,UpdateTime: Mon Aug 15 08:19:15 CST 2016,Message: Spot Instance terminated due to user-initiated termination.}
    * */
  def getInstanceGroups(launchGroupPrefix: String) = {
    // 找出我们想查看的所有requests
    val descResult: DescribeSpotInstanceRequestsResult =
      ec2.describeSpotInstanceRequests(new DescribeSpotInstanceRequestsRequest())
    val descResponses: util.List[SpotInstanceRequest] = descResult.getSpotInstanceRequests()

    // 查看每一个request,看看他们的状态是不是active
    for (resp <- descResponses) {
      val launchGroup = resp.getLaunchGroup
      if (launchGroup == null || !launchGroup.startsWith(launchGroupPrefix))
        println(s"Not our target: launch group = ${resp.getLaunchGroup}, state = ${resp.getState}, status = ${resp.getStatus}")
      else
        println(s"Target: launch group = ${resp.getLaunchGroup}, state = ${resp.getState}, status = ${resp.getStatus}")
    }
  }



  /**
    * 发出申请spot instance 的请求
    *
    * An Amazon Machine Image (AMI) provides the information required to launch an instance,
    * which is a virtual server in the cloud. You specify an AMI when you launch an instance,
    * and you can launch as many instances from the AMI as you need.
    * You can also launch instances from as many different AMIs as you need.
    *
    *
    * When you launch an instance in Amazon EC2, you have the option of passing user data to the instance
    * that can be used to perform common automated configuration tasks and even run scripts after the instance starts.
    * You can pass two types of user data to Amazon EC2: shell scripts and cloud-init directives.
    * You can also pass this data into the launch wizard as plain text, as a file (this is useful for launching instances
    * via the command line tools), or as base64-encoded text (for API calls).
    * */
  def submitRequest(price: String, instanceType: String, instanceNum: Int, groupName: String, securityGroup :String,
                    imageId: String, keyName: String, subnetId: String): IndexedSeq[String] = {
    val request = new RequestSpotInstancesRequest()
    request.setSpotPrice(price)
    request.setInstanceCount(instanceNum)
    request.setLaunchGroup(groupName)

    val spec = new LaunchSpecification()
    spec.setImageId(imageId)
    spec.setInstanceType(instanceType)
    spec.setKeyName(keyName)

    if (null != subnetId && !subnetId.isEmpty)
      spec.setSubnetId(subnetId)
    else
      spec.setSecurityGroups(List(securityGroup))

    // 可以传入一段初始化的代码/脚本
    // val userData = "#!/bin/bash\n su - ec2-user -c \" /home/ec2-user/boot.sh -m worker -c " + akkaName + " \""
    // spec.setUserData(new String(Base64.encodeBase64(userData.getBytes)))

    request.setLaunchSpecification(spec)
    val result: RequestSpotInstancesResult = ec2.requestSpotInstances(request)
    val responses: util.List[SpotInstanceRequest] = result.getSpotInstanceRequests

    val spotInstanceRequestIDs =
      for (i <- 0 until responses.size()) yield {
        responses.get(i).getSpotInstanceRequestId
      }

    spotInstanceRequestIDs
  }



}

object EC2Manager extends App {
  val ec2Manager = new EC2Manager()

  val price = "0.6"
  val instance_type = "m3.xlarge"
  val instance_num = 2
  val launch_group_prefix = "xiaotao"
  val security_group = "sg-dc60aba5"
  val imageID = "ami-824597e2"
  val keyName = "akka_oregon"
  val vpc = "vpc-925c96f6"
  val subnet = "subnet-973e3dce"

  // 发出创建spot instance的请求
  // val spotInstanceRequestIDs: IndexedSeq[String] = ec2Manager.submitRequest(price, instance_type, instance_num, launch_group, security_group, imageID, keyName, subnetID)
  // spotInstanceRequestIDs.foreach(id => println(s"spot instance request id = $id"))

  // 能不能看到spot instance的IP ?
  ec2Manager.getInstanceGroups(launch_group_prefix)
}
