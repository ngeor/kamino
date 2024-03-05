.PHONY: all gs build clean sortpom fmt package verify run run-changes run-sifo
SKIP_TESTS_OPTS=-Denforcer.skip=true -Dcheckstyle.skip=true -Dmaven.javadoc.skip=true -DskipTests
MVN=mvnd
INTERNAL_TOOLS=internal-tooling/changes,internal-tooling/sifo

all: gs

gs:
	git st

build: sortpom fmt package run

clean:
	$(MVN) clean
	git clean -f -X -d

sortpom:
	$(MVN) -Psortpom -pl !:kamino $(SKIP_TESTS_OPTS) validate
	$(MVN) -N -Dsort.sortModules=true -Dsort.createBackupFile=false $(SKIP_TESTS_OPTS) com.github.ekryd.sortpom:sortpom-maven-plugin:sort

fmt:
	$(MVN) -pl !:kamino $(SKIP_TESTS_OPTS) spotless:apply

package:
	$(MVN) -Pshade -am -pl $(INTERNAL_TOOLS) $(SKIP_TESTS_OPTS) package

verify:
	$(MVN) -Pshade -am -pl $(INTERNAL_TOOLS) verify

run: run-changes run-sifo

run-changes:
	java -jar internal-tooling/changes/target/changes-1.0-SNAPSHOT.jar

run-sifo:
	java -jar internal-tooling/sifo/target/sifo-1.0-SNAPSHOT.jar
