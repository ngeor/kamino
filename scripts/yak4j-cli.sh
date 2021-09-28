#!/usr/bin/env bash
# This script is automatically generated, do not edit manually.
# Re-generate the script with mvn package -pl apps/yak4j-cli -am
set -e -o pipefail
JAR_NAME=yak4j-cli-0.11.0-SNAPSHOT.jar
SOURCE_JAR=apps/yak4j-cli/target/${JAR_NAME}
if [[ ! -r ${SOURCE_JAR} ]]; then
    mvn package -pl apps/yak4j-cli -am
fi
# Use jar from a temp location, to be able to clean the folder of yak4j-cli
TEMP_JAR=${TMP}/${JAR_NAME}
# Delete the temp file upon exit
trap "{ rm ${TEMP_JAR}; }" EXIT
cp ${SOURCE_JAR} ${TEMP_JAR}
java -jar ${TEMP_JAR} $*
