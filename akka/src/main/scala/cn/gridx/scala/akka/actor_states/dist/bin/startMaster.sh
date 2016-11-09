#!/bin/bash

## 启动master

SOURCE="${BASH_SOURCE[0]}"
BIN_DIR="$( dirname "$SOURCE" )"
cd $BIN_DIR/..

Base="/Users/tao/code/akka"
Target="$Base/akka-1.0-RELEASE.jar"
Class="cn.gridx.scala.akka.monitor.Master"
Lib=`echo $Base/dist/lib/*.jar | tr ' ' ':' `
Conf="$Base/dist/conf/master"

java -cp $Target:$Lib:$Conf $Class