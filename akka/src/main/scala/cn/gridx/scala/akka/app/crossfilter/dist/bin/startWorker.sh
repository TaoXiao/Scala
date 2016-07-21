#!/bin/bash

## 启动workers

SOURCE="${BASH_SOURCE[0]}"
BIN_DIR="$( dirname "$SOURCE" )"
cd $BIN_DIR/..

Target="bin/akka-1.0-RELEASE-all.jar"
Class="cn.gridx.scala.akka.app.crossfilter.Worker"
Lib="lib/*"
Log4j2Conf="conf/log4j2"
JvmOpt="-Xms5000m -Xmx5000m"
WorkerConf="conf/worker"
ActorType="nonSeed"
ActorNum="3"


java $JvmOpt -cp $Target:$Log4j2Conf:$WorkerConf:$Lib  $Class --actorType $ActorType --actorNumber $ActorNum