PWD=`pwd`
cd ../../../
PWD=`$pwd`
echo "$PWD"
Class="cn.gridx.scala.lang.system.GetMetrics"
JvmOpts="-Xmx2000m -Xms2000m"
Jars=`echo build/libs/*.jar | tr ' ' ':'`
echo "Jars=$Jars"

java ${JvmOpts} -cp ${Jars} ${Class}