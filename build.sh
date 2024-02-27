#!/usr/bin/env bash
set -e

MVN="${MVN:-mvnd}"

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

SKIP_TESTS_OPTS="-Denforcer.skip=true -Dcheckstyle.skip=true -Dmaven.javadoc.skip=true -DskipTests"

# sort pom
${MVN} -Psortpom ${SKIP_TESTS_OPTS} validate
# sort root pom file (does not currently have a sortpom configuration)
${MVN} -N -Dsort.sortModules=true -Dsort.createBackupFile=false ${SKIP_TESTS_OPTS} com.github.ekryd.sortpom:sortpom-maven-plugin:sort
# remove pom.bak files etc
git clean -f -X
# build
if [[ $SKIP_TESTS -eq 1 ]]; then
    ${MVN} ${SKIP_TESTS_OPTS} clean test
else
    ${MVN} clean test
fi
if [[ $RUN_REWRITE -eq 1 ]]; then
    # open rewrite (excluding the aggregator root)
    ${MVN} -pl '!:kamino' ${SKIP_TESTS_OPTS} rewrite:run
fi
# spotless (excluding the aggregator root)
${MVN} -pl '!:kamino' ${SKIP_TESTS_OPTS} spotless:apply
# build internal tools
${MVN} -Pshade -am -pl internal-tooling/changes,internal-tooling/sifo ${SKIP_TESTS_OPTS} package
if [[ $BUILD_NATIVE -eq 1 ]]; then
    ${MVN} -Pnative -am -pl internal-tooling/changes,internal-tooling/sifo ${SKIP_TESTS_OPTS} package
fi
# generate templates
java -jar internal-tooling/sifo/target/sifo-1.0-SNAPSHOT.jar
# print what is unreleased
java -jar internal-tooling/changes/target/changes-1.0-SNAPSHOT.jar
