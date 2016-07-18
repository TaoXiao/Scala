#!/bin/bash

## 启动spray server

SOURCE="${BASH_SOURCE[0]}"
BIN_DIR="$( dirname "$SOURCE" )"
cd $BIN_DIR/..

Target="bin/akka-1.0-RELEASE-all.jar"
Class="cn.gridx.scala.akka.app.crossfilter.Boot"
Lib="lib/*"
Log4j2Conf="conf/log4j2"

java -cp $Target:$Lib:$Log4j2Conf $Class