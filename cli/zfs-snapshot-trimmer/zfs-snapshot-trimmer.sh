#!/bin/sh
JAVA_HOME=/opt/jdk1.8
PATH=$JAVA_HOME/bin:/sbin:$PATH

# calculate jar file
SCRIPT=$0
SCRIPT_PATH=`dirname $SCRIPT`
JAR="$SCRIPT_PATH/zfs-snapshot-trimmer-1.0-SNAPSHOT.jar"

# run app
java -jar $JAR $*
