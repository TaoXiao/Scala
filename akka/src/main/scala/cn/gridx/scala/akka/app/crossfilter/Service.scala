package cn.gridx.scala.akka.app.crossfilter


import scala.concurrent.duration._
import akka.actor.{Actor, ActorRef, Address, RootActorPath}
import akka.cluster.{Cluster, ClusterEvent, Member}
import akka.cluster.ClusterEvent.{MemberUp, UnreachableMember}
import akka.util.Timeout
import org.slf4j.LoggerFactory
import spray.routing.HttpService

import scala.concurrent.{Await, Future}
import akka.pattern.ask
import cn.gridx.scala.akka.app.crossfilter.Master.CalcResult
import cn.gridx.scala.akka.app.crossfilter.ServiceActor.{JavaAnalysisParam, MSG_SM_RELOAD, MSG_SM_STARTCALC}
import cn.gridx.scala.akka.app.crossfilter.Worker.MSG_WM_SM_TEST
import com.google.gson.GsonBuilder

import scala.collection.mutable

/**
  * Created by tao on 7/17/16.
  */

class ServiceActor extends Actor with HttpService {
  override def actorRefFactory = context
  override def receive = runRoute(route) orElse msgProcessor

  val logger = LoggerFactory.getLogger(this.getClass())
  val cluster = Cluster(context.system)
  var master: Option[ActorRef] = None
  implicit val timeout = Timeout(60 seconds)


  /**
    * 订阅系统的消息
    * */
  override def preStart(): Unit = {
    cluster.subscribe(self, ClusterEvent.initialStateAsEvents, classOf[MemberUp], classOf[UnreachableMember])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  private def msgProcessor: PartialFunction[Any, Unit] = {
    case MemberUp(member) =>
      onMemberUp(member)
    case UnreachableMember(member) =>
      onUnreachableMember(member)
  }

  private def onMemberUp(member: Member): Unit = {
    logger.info(s"消息[MemberUp], member = ${RootActorPath(member.address)}")

    // 如果是maste, 则要找到并记住这个master
    if (member.hasRole(Roles.MASTER))
      lookupMaster(member.address)
  }

  private def onUnreachableMember(member: Member): Unit = {
    logger.info(s"消息[MemberUnreachable], member = ${RootActorPath(member.address)}")
    if (member.hasRole(Roles.MASTER)) {
      logger.info("消息[UnreachableMember], master下线 (${RootActorPath(member.address)})")
      master = None
    }
  }

  // 寻找master, 并向master注册自己
  private def lookupMaster(address: Address): Unit = {
    val f: Future[ActorRef] = context.system.actorSelection(RootActorPath(address)/"user"/"master").resolveOne()
    master = Some(Await.result(f, timeout.duration))
    logger.info("找到了Master: " + master.get.path.toString)
  }


  // Service的路由
  val route = pathPrefix("crossfilter") {
    path ("test") {
      get {
        complete {
          onTest()
        }
      }
    } ~ path ("reload") {
      get {
        parameters('path.as[String]) {
          (path) => {
            complete {
              onReload(path)
            }
          }
        }
      }
    } ~ path ("calc") {
      post {
        entity(as[String]) {
          payload => {
            complete {
              logger.info(s"收到的payload为: \n$payload")
              onAnalyze(payload)
            }
          }
        }
      }
    }
  }


  private def onTest(): String = {
    if (master.isEmpty)
      return s"""{"succeed": false, "message" : "还未找到master actor! 请稍后再试"}"""

    val future: Future[Any] = master.get ? MSG_WM_SM_TEST
    Await.result(future, timeout.duration).toString
  }

  /**
    * 要求重新加载数据
    * */
  private def onReload(path: String): String = {
    if (master.isEmpty)
      return s"""{"succeed": false, "message" : "还未找到master actor! 请稍后再试"}"""

    val future = master.get ? MSG_SM_RELOAD(path)
    Await.result(future, timeout.duration).toString
  }


  /**
    * 要求actor system开始分析数据
    * */
  private def onAnalyze(payload: String): String = {
    if (master.isEmpty)
      return s"""{"succeed": false, "message" : "还未找到master actor! 请稍后再试"}"""

    val future: Future[Any] = master.get ? MSG_SM_STARTCALC(parsePayload(payload))
    val result = Await.result(future, timeout.duration).asInstanceOf[CalcResult]
    result.toJson()
  }

  /*
    输入的参数以json payload的形式传进来, 例如
    {
      "optionFilter" : {
        "care" : "Y",
        "fera" : "Y"
      },
      "dimensionFilter" : {
        "ETOUA-ETOUB" : {
          "min" : 100,
          "max" : 1400
        },
        "E1-ETOUA" : {
          "min" : 300,
          "max" : 2000
        }
      },
      "dimensionIntervals" : {
        "ETOUA-ETOUB" : [0, 200, 400, 600, 800, 1000, 1500, 2000, 2500, 3000],
        "ETOUB-ETOUA" : [300, 500, 800, 2000],
        "E1-ETOUA" : [100, 400, 600, 1000, 2000]
      },
      "targetOptions" : ["care", "mb", "da", "cca", "tbs"]
    }

    我们需要将这个string解析并转换为
    AnalysisParam(optFilter: mutable.HashMap[String, String],
                  dimFilter: mutable.HashMap[String, mutable.HashMap[String, Double]],
                  targetOptions: Array[String],
                  targetDimensions: mutable.HashMap[String, Array[Double]])
   */
  private def parsePayload(json: String): AnalysisParam = {
    val gson = new GsonBuilder().serializeNulls().create()
    val javaParam = gson.fromJson(json, classOf[JavaAnalysisParam])
    javaParam.toAnalysisParam()
  }

}


object ServiceActor {
  /**
    * 我们需要将外界传入的JSON转为为一个case class, 但是Gson不支持Scala Collections
    * 所以我们首先将JSON转换为`JavaAnalysisParam`, 然后再将`JavaAnalysisParam`转换为`AnalysisParam`
    *
    * `AnalysisParam`是一个Scala case class
    * */
  final case class JavaAnalysisParam(optionFilter: java.util.HashMap[String, String],
                                     dimensionFilter: java.util.HashMap[String, java.util.HashMap[String, Double]],
                                     targetOptions: Array[String],
                                     targetDimensions: java.util.HashMap[String, Array[Double]]) {
    def toAnalysisParam(): AnalysisParam = {
      val scalaOptFilter = Commons.JavaMap2ScalaMap(optionFilter)

      val scalaDimensionFilter = mutable.HashMap[String, mutable.HashMap[String, Double]]()
      var kIt = dimensionFilter.keySet().iterator()
      while (kIt.hasNext) {
        val key = kIt.next()
        val value = dimensionFilter.get(key)
        scalaDimensionFilter.put(key, Commons.JavaMap2ScalaMap(value))
      }

      val scalaTargetDimension = mutable.HashMap[String, Array[Double]]()
      kIt = targetDimensions.keySet().iterator()
      while (kIt.hasNext) {
        val key = kIt.next()
        val value = targetDimensions.get(key)
        scalaTargetDimension.put(key, value)
      }

      AnalysisParam(scalaOptFilter, scalaDimensionFilter, targetOptions, scalaTargetDimension)
    }
  }


  final case class MSG_SM_RELOAD(path: String)


  final case class MSG_SM_STARTCALC(analysisParam: AnalysisParam)

  /**
    * 要求master开始计算 sorted population histogram
    * 计算的基础是总体的数据, 计算的对象是`targetDimName`
    *
    * 也就是说, 我们要计算`targetDimName`在全体数据中是怎样分布的, 返回结果要按照`targetDimName`对应的值得降序排列
    * */
  final case class MSG_SM_SortedPopHist(targetDimName: String) {

  }
}



