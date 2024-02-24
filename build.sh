#!/usr/bin/env bash
set -e

# Collect options
BUILD_NATIVE=0
RUN_REWRITE=0
SKIP_TESTS=0
while [[ "$1" =~ ^- && ! "$1" == "--" ]]; do case $1 in
    -n | --native )
        BUILD_NATIVE=1
        ;;
    -r | --rewrite )
        RUN_REWRITE=1
        ;;
    -s | --skip-tests )
        SKIP_TESTS=1
        ;;
esac; shift; done
if [[ "$1" == '--' ]]; then shift; fi

# sort pom
mvn -Psortpom validate
# sort root pom file (does not currently have a sortpom configuration)
mvn -N com.github.ekryd.sortpom:sortpom-maven-plugin:sort -Dsort.sortModules=true -Dsort.createBackupFile=false
# remove pom.bak files etc
git clean -f -X
# build
if [[ $SKIP_TESTS -eq 1 ]]; then
    mvn clean test -DskipTests
else
    mvn clean test
fi
if [[ $RUN_REWRITE -eq 1 ]]; then
    # open rewrite (excluding the aggregator root)
    mvn -pl '!:kamino' rewrite:run
fi
# spotless (excluding the aggregator root)
mvn -pl '!:kamino' spotless:apply
# build internal tools
mvn -Pshade -am -pl internal-tooling/changes package -DskipTests
mvn -Pshade -am -pl internal-tooling/sifo package -DskipTests
if [[ $BUILD_NATIVE -eq 1 ]]; then
    mvn -Pnative -am -pl internal-tooling/changes package -DskipTests
    mvn -Pnative -am -pl internal-tooling/sifo package -DskipTests
fi
# generate templates
java -jar internal-tooling/sifo/target/sifo-1.0-SNAPSHOT.jar
# print what is unreleased
java -jar internal-tooling/changes/target/changes-1.0-SNAPSHOT.jar
