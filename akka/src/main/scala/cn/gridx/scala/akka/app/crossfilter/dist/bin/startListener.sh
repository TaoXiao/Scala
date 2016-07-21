#!/bin/bash
## 启动listener


SOURCE="${BASH_SOURCE[0]}"
BIN_DIR="$( dirname "$SOURCE" )"
cd $BIN_DIR/..

Target="bin/akka-1.0-RELEASE-all.jar"
Class="cn.gridx.scala.akka.app.crossfilter.Listener"
Lib="lib/*"
Log4j2Conf="conf/log4j2"
ListenerConf="conf/listener"

java -cp $Target:$Log4j2Conf:$ListenerConf:$Lib $Class