#!/usr/bin/env sh

# all projects must use the parent pom (directly or indirectly)
# all projects must use the correct version of parent pom
# utility to bootstrap a new project
# all projects must be registered as modules in the parent pom (directly or indirectly)

set -e

APP=yak4j-cli
DIR=apps/$APP
JAR=$DIR/target/$APP-0.1.0-SNAPSHOT.jar
mvn package -pl $DIR -q
java -jar $JAR  $*
