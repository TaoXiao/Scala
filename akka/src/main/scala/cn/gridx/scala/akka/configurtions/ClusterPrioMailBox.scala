package cn.gridx.scala.akka.configurtions

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}

/**
  * Created by tao on 8/3/16.
  */
class ClusterPrioMailbox(settings: ActorSystem.Settings, config: com.typesafe.config.Config)
  extends UnboundedPriorityMailbox(
    PriorityGenerator {
      case PoisonPill => 9

      case (ActorRef, Int, Long) => 1

      case otherwise => 7
    })