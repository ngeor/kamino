#!/usr/bin/env bash
set -e
# sort pom
mvn -Psortpom validate
# sort root pom file (does not currently have a sortpom configuration)
mvn -N com.github.ekryd.sortpom:sortpom-maven-plugin:sort -Dsort.sortModules=true -Dsort.createBackupFile=false
# remove pom.bak files etc
git clean -f -X
# build
mvn clean test
# spotless (allowed to fail for now)
mvn spotless:apply || true
# build internal tools
mvn -Pshade -am -pl internal-tooling/changes package -DskipTests
mvn -Pshade -am -pl internal-tooling/sifo package -DskipTests
if [[ "$1" == "--native" ]]; then
    mvn -Pnative -am -pl internal-tooling/changes package -DskipTests
    mvn -Pnative -am -pl internal-tooling/sifo package -DskipTests
fi
# generate templates
java -jar internal-tooling/sifo/target/sifo-1.0-SNAPSHOT.jar
# print what is unreleased
java -jar internal-tooling/changes/target/changes-1.0-SNAPSHOT.jar
