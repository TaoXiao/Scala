#!/bin/bash
## 启动master


SOURCE="${BASH_SOURCE[0]}"
BIN_DIR="$( dirname "$SOURCE" )"
cd $BIN_DIR/..

Target="bin/akka-1.0-RELEASE-all.jar"
Class="cn.gridx.scala.akka.app.crossfilter.Master"
Lib="lib/*"
Log4j2Conf="conf/log4j2"
MasterConf="conf/master"

java -cp $Target:$Log4j2Conf:$MasterConf:$Lib $Class --dataPath /disk2/app/crossfilter/data/sample.impact --minWorkerActors 10