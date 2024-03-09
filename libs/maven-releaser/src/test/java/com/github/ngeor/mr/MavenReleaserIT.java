package com.github.ngeor.mr;

import static com.github.ngeor.mr.Util.VALID_CHILD_POM_CONTENTS;
import static com.github.ngeor.mr.Util.VALID_PARENT_POM_CONTENTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.git.Commit;
import com.github.ngeor.git.Git;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.versions.SemVer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@SuppressWarnings("MagicNumber")
class MavenReleaserIT {
    @TempDir
    private Path remoteRoot;

    @TempDir
    private Path monorepoRoot;

    private Git git;

    @SuppressWarnings("FieldCanBeLocal")
    private final String validChildPomWithParentContents =
            """
        <project>
            <modelVersion>4.0.0</modelVersion>
            <groupId>com.acme</groupId>
            <artifactId>foo</artifactId>
            <version>1.0-SNAPSHOT</version>
            <name>foo</name>
            <description>The library</description>
            <parent>
                <groupId>com.acme</groupId>
                <artifactId>monorepo</artifactId>
                <version>1.0-SNAPSHOT</version>
                <relativePath>..</relativePath>
            </parent>
            <licenses>
                <license>
                    <name>MIT</name>
                    <url>https://opensource.org/licenses/MIT</url>
                </license>
            </licenses>
            <developers>
                <developer>
                    <name>Nikolaos Georgiou</name>
                    <email>nikolaos.georgiou@gmail.com</email>
                </developer>
            </developers>
            <scm>
                <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                <tag>HEAD</tag>
                <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
            </scm>
        </project>
        """;

    @BeforeEach
    void beforeEach() throws IOException, ProcessFailedException {
        Util.createRemoteGit(remoteRoot, monorepoRoot);
        git = new Git(monorepoRoot.toFile());
        Files.createDirectory(monorepoRoot.resolve("lib"));
    }

    private void act() throws IOException, ProcessFailedException {
        Options options = ImmutableOptions.builder()
                .monorepoRoot(monorepoRoot.toFile())
                .path("lib")
                .formatOptions(Defaults.defaultFormatOptions())
                .nextVersion(new SemVer(1, 0, 0))
                .push(false)
                .xmlIndentation("    ")
                .build();
        new MavenReleaser(options).prepareRelease();
    }

    @Test
    void testHappyFlow() throws IOException, ProcessFailedException {
        // arrange
        Files.writeString(monorepoRoot.resolve("pom.xml"), VALID_PARENT_POM_CONTENTS);
        Path childPomPath = monorepoRoot.resolve("lib").resolve("pom.xml");
        Files.writeString(childPomPath, VALID_CHILD_POM_CONTENTS);
        git.addAll();
        git.commit("chore: Added pom.xml");
        git.push();

        // act
        act();

        // assert
        new DocumentAssertions(childPomPath).hasCoordinates(new MavenCoordinates("com.acme", "foo", "1.1.0-SNAPSHOT"));
        List<Commit> commits = git.revList(null).toList();
        assertThat(commits.stream().map(Commit::summary))
                .containsExactly(
                        "release(lib): switching to development version 1.1.0-SNAPSHOT",
                        "release(lib): releasing 1.0.0",
                        "chore: Added pom.xml");
        assertThat(commits.stream().map(Commit::tag)).containsExactly(null, "lib/v1.0.0", null);
        git.checkout("lib/v1.0.0");
        new DocumentAssertions(childPomPath).hasCoordinates(new MavenCoordinates("com.acme", "foo", "1.0.0"));
    }

    @Test
    void testHappyFlowWithParent() throws IOException, ProcessFailedException {
        // arrange
        Files.writeString(monorepoRoot.resolve("pom.xml"), VALID_PARENT_POM_CONTENTS);
        Path childPomPath = monorepoRoot.resolve("lib").resolve("pom.xml");
        Files.writeString(childPomPath, validChildPomWithParentContents);
        git.addAll();
        git.commit("chore: Added pom.xml");
        git.push();

        // act
        act();

        // assert
        new DocumentAssertions(childPomPath)
                .hasCoordinates(new MavenCoordinates("com.acme", "foo", "1.1.0-SNAPSHOT"))
                .hasParentPom()
                .doesNotHaveModules()
                .hasScmTag("HEAD");
        List<Commit> commits = git.revList(null).toList();
        assertThat(commits.stream().map(Commit::summary))
                .containsExactly(
                        "release(lib): switching to development version 1.1.0-SNAPSHOT",
                        "release(lib): releasing 1.0.0",
                        "chore: Added pom.xml");
        assertThat(commits.stream().map(Commit::tag)).containsExactly(null, "lib/v1.0.0", null);
        git.checkout("lib/v1.0.0");
        new DocumentAssertions(childPomPath)
                .hasCoordinates(new MavenCoordinates("com.acme", "foo", "1.0.0"))
                .doesNotHaveParentPom()
                .doesNotHaveModules()
                .hasScmTag("lib/v1.0.0");
    }

    @Test
    void testSnapshotsAreNotAllowed() throws IOException, ProcessFailedException {
        // arrange
        String parentContents =
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>parent</artifactId>
                <version>1.0-SNAPSHOT</version>
                <packaging>pom</packaging>
                <modules>
                    <module>lib</module>
                </modules>
                <licenses>
                    <license>
                        <name>MIT</name>
                        <url>https://opensource.org/licenses/MIT</url>
                    </license>
                </licenses>
                <developers>
                    <developer>
                        <name>Nikolaos Georgiou</name>
                        <email>nikolaos.georgiou@gmail.com</email>
                    </developer>
                </developers>
            </project>
            """;
        String childContents =
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <artifactId>lib</artifactId>
                <name>lib</name>
                <description>Cool library</description>
                <version>1.1-SNAPSHOT</version>
                <parent>
                    <groupId>com.acme</groupId>
                    <artifactId>parent</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    <relativePath>..</relativePath>
                </parent>
                <scm>
                    <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                    <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                    <tag>HEAD</tag>
                    <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
                </scm>
                <dependencies>
                    <dependency>
                        <groupId>com.acme</groupId>
                        <artifactId>express</artifactId>
                        <version>0.1.2-SNAPSHOT</version>
                    </dependency>
                </dependencies>
            </project>
            """;
        Files.writeString(monorepoRoot.resolve("pom.xml"), parentContents);
        Path childPomPath = monorepoRoot.resolve("lib").resolve("pom.xml");
        Files.writeString(childPomPath, childContents);
        git.addAll();
        git.commit("chore: Added pom.xml");
        git.push();

        // act and assert
        assertThatThrownBy(this::act).hasMessageContaining("Snapshot version 0.1.2-SNAPSHOT is not allowed");
    }

    @Test
    void resolveReactorSnapshots() throws IOException, ProcessFailedException {
        // arrange
        Files.writeString(
                monorepoRoot.resolve("pom.xml"),
                """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.acme</groupId>
                    <artifactId>aggregator</artifactId>
                    <version>1.0.0-SNAPSHOT</version>
                    <packaging>pom</packaging>
                    <modules>
                        <module>foo</module>
                        <module>bar</module>
                    </modules>
                </project>
                """);
        Files.createDirectory(monorepoRoot.resolve("foo"));
        Files.writeString(
                monorepoRoot.resolve("foo").resolve("pom.xml"),
                """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <version>2.0.0-SNAPSHOT</version>
                    <packaging>pom</packaging>
                    <name>foo</name>
                    <description>foo</description>
                    <licenses>
                        <license>
                            <name>MIT</name>
                            <url>https://opensource.org/licenses/MIT</url>
                        </license>
                    </licenses>
                    <developers>
                        <developer>
                            <name>Nikolaos Georgiou</name>
                            <email>nikolaos.georgiou@gmail.com</email>
                        </developer>
                    </developers>
                    <scm>
                        <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                        <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                        <tag>HEAD</tag>
                        <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
                    </scm>
                </project>
                """);
        Files.createDirectory(monorepoRoot.resolve("bar"));
        Files.writeString(
                monorepoRoot.resolve("bar").resolve("pom.xml"),
                """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.acme</groupId>
                    <artifactId>bar</artifactId>
                    <version>3.0.0-SNAPSHOT</version>
                    <packaging>pom</packaging>
                    <name>bar</name>
                    <description>bar</description>
                    <licenses>
                        <license>
                            <name>MIT</name>
                            <url>https://opensource.org/licenses/MIT</url>
                        </license>
                    </licenses>
                    <developers>
                        <developer>
                            <name>Nikolaos Georgiou</name>
                            <email>nikolaos.georgiou@gmail.com</email>
                        </developer>
                    </developers>
                    <scm>
                        <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                        <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                        <tag>HEAD</tag>
                        <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
                    </scm>
                    <dependencies>
                        <dependency>
                            <groupId>com.acme</groupId>
                            <artifactId>foo</artifactId>
                            <version>2.0.0-SNAPSHOT</version>
                        </dependency>
                    </dependencies>
                </project>
                """);
        git.addAll();
        git.commit("Initial import");
        git.push();

        new MavenReleaser(ImmutableOptions.builder()
                        .monorepoRoot(monorepoRoot.toFile())
                        .path("foo")
                        .formatOptions(Defaults.defaultFormatOptions())
                        .nextVersion(new SemVer(2, 0, 0))
                        .push(true)
                        .xmlIndentation("    ")
                        .build())
                .prepareRelease();

        git.push();

        new MavenReleaser(ImmutableOptions.builder()
                        .monorepoRoot(monorepoRoot.toFile())
                        .path("bar")
                        .formatOptions(Defaults.defaultFormatOptions())
                        .nextVersion(new SemVer(3, 0, 0))
                        .push(true)
                        .xmlIndentation("    ")
                        .build())
                .prepareRelease();

        git.push();

        assertThat(Files.readString(monorepoRoot.resolve("bar").resolve("pom.xml")))
                .as("bar/pom.xml development version")
                .isEqualToNormalizingNewlines(
                        """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.acme</groupId>
                    <artifactId>bar</artifactId>
                    <version>3.1.0-SNAPSHOT</version>
                    <packaging>pom</packaging>
                    <name>bar</name>
                    <description>bar</description>
                    <licenses>
                        <license>
                            <name>MIT</name>
                            <url>https://opensource.org/licenses/MIT</url>
                        </license>
                    </licenses>
                    <developers>
                        <developer>
                            <name>Nikolaos Georgiou</name>
                            <email>nikolaos.georgiou@gmail.com</email>
                        </developer>
                    </developers>
                    <scm>
                        <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                        <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                        <tag>HEAD</tag>
                        <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
                    </scm>
                    <dependencies>
                        <dependency>
                            <groupId>com.acme</groupId>
                            <artifactId>foo</artifactId>
                            <version>2.1.0-SNAPSHOT</version>
                        </dependency>
                    </dependencies>
                </project>
                """);
        assertThat(Files.readString(monorepoRoot.resolve("foo").resolve("pom.xml")))
                .as("foo/pom.xml development version")
                .isEqualToNormalizingNewlines(
                        """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <version>2.1.0-SNAPSHOT</version>
                    <packaging>pom</packaging>
                    <name>foo</name>
                    <description>foo</description>
                    <licenses>
                        <license>
                            <name>MIT</name>
                            <url>https://opensource.org/licenses/MIT</url>
                        </license>
                    </licenses>
                    <developers>
                        <developer>
                            <name>Nikolaos Georgiou</name>
                            <email>nikolaos.georgiou@gmail.com</email>
                        </developer>
                    </developers>
                    <scm>
                        <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                        <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                        <tag>HEAD</tag>
                        <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
                    </scm>
                </project>
                """);

        git.checkout("bar/v3.0.0");

        assertThat(Files.readString(monorepoRoot.resolve("bar").resolve("pom.xml")))
                .as("bar/pom.xml release version")
                .isEqualToNormalizingNewlines(
                        """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.acme</groupId>
                    <artifactId>bar</artifactId>
                    <version>3.0.0</version>
                    <packaging>pom</packaging>
                    <name>bar</name>
                    <description>bar</description>
                    <licenses>
                        <license>
                            <name>MIT</name>
                            <url>https://opensource.org/licenses/MIT</url>
                        </license>
                    </licenses>
                    <developers>
                        <developer>
                            <name>Nikolaos Georgiou</name>
                            <email>nikolaos.georgiou@gmail.com</email>
                        </developer>
                    </developers>
                    <scm>
                        <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                        <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                        <tag>bar/v3.0.0</tag>
                        <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
                    </scm>
                    <dependencies>
                        <dependency>
                            <groupId>com.acme</groupId>
                            <artifactId>foo</artifactId>
                            <version>2.0.0</version>
                        </dependency>
                    </dependencies>
                </project>
                """);
        assertThat(Files.readString(monorepoRoot.resolve("foo").resolve("pom.xml")))
                .as("foo/pom.xml development version")
                .isEqualToNormalizingNewlines(
                        """
                <project>
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>com.acme</groupId>
                    <artifactId>foo</artifactId>
                    <version>2.1.0-SNAPSHOT</version>
                    <packaging>pom</packaging>
                    <name>foo</name>
                    <description>foo</description>
                    <licenses>
                        <license>
                            <name>MIT</name>
                            <url>https://opensource.org/licenses/MIT</url>
                        </license>
                    </licenses>
                    <developers>
                        <developer>
                            <name>Nikolaos Georgiou</name>
                            <email>nikolaos.georgiou@gmail.com</email>
                        </developer>
                    </developers>
                    <scm>
                        <connection>scm:git:https://github.com/ngeor/kamino.git</connection>
                        <developerConnection>scm:git:git@github.com:ngeor/kamino.git</developerConnection>
                        <tag>HEAD</tag>
                        <url>https://github.com/ngeor/kamino/tree/master/libs/java</url>
                    </scm>
                </project>
                """);
    }
}
