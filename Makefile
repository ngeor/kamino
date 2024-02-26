SKIP_TESTS_OPTS=-Denforcer.skip=true -Dcheckstyle.skip=true -Dmaven.javadoc.skip=true -DskipTests
MVN=mvnd

all: gs

gs:
	git st

rebuild: clean sortpom fmt package run

clean:
	$(MVN) clean
	git clean -f -X

fmt:
	$(MVN) -pl !:kamino $(SKIP_TESTS_OPTS) spotless:apply

sortpom:
	$(MVN) -Psortpom -pl !:kamino $(SKIP_TESTS_OPTS) validate
	$(MVN) -N -Dsort.sortModules=true -Dsort.createBackupFile=false $(SKIP_TESTS_OPTS) com.github.ekryd.sortpom:sortpom-maven-plugin:sort

package:
	$(MVN) -Pshade -am -pl internal-tooling/changes,internal-tooling/sifo $(SKIP_TESTS_OPTS) package

run:
	java -jar internal-tooling/sifo/target/sifo-1.0-SNAPSHOT.jar
	java -jar internal-tooling/changes/target/changes-1.0-SNAPSHOT.jar
