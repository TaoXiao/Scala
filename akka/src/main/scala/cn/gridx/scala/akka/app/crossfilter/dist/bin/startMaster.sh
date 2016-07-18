#!/bin/bash
## 启动master

## 不再需要这个脚本, 现在master和spray server在同一台机器上, 并且master将由spray来启动
echo "Don't call me!"
echo "现在master和spray server在同一台机器上, 并且master将由spray来启动"
exit

SOURCE="${BASH_SOURCE[0]}"
BIN_DIR="$( dirname "$SOURCE" )"
cd $BIN_DIR/..

Target="bin/akka-1.0-RELEASE-all.jar"
Class="cn.gridx.scala.akka.app.crossfilter.Master"
Lib="lib/*"
Log4j2Conf="conf/log4j2"
MasterConf="conf/master"
JvmConf="-Xms1000m -Xmx1000m"

# 以下内容请填写
DataPath="/Users/tao/Documents/data_2.txt"
LineCount="3651707"
WorkingActors="3" # 要求有3个worker来向master注册


java $JvmConf -cp $Target:$Log4j2Conf:$MasterConf:$Lib $Class $DataPath $LineCount $WorkingActors