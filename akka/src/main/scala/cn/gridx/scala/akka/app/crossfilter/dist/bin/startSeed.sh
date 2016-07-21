#!/bin/bash

## 启动seed

SOURCE="${BASH_SOURCE[0]}"
BIN_DIR="$( dirname "$SOURCE" )"
cd $BIN_DIR/..

Target="bin/akka-1.0-RELEASE-all.jar"
Class="cn.gridx.scala.akka.app.crossfilter.Worker"
Lib="lib/*"
Log4j2Conf="conf/log4j2"
WorkerConf="conf/worker"
JvmOpt="-Xms5000m -Xmx5000m"
ActorType="seed"

java $JvmOpt -cp $Target:$Lib:$Log4j2Conf:$WorkerConf $Class --actorType $ActorType