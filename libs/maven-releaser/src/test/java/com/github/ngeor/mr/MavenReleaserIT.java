package com.github.ngeor.mr;

import static com.github.ngeor.mr.Util.VALID_CHILD_POM_CONTENTS;
import static com.github.ngeor.mr.Util.VALID_PARENT_POM_CONTENTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.git.Commit;
import com.github.ngeor.git.Git;
import com.github.ngeor.git.InitOption;
import com.github.ngeor.git.User;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.versions.SemVer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
        // create remote git
        Git remoteGit = new Git(remoteRoot.toFile());
        remoteGit.init("trunk", InitOption.BARE);

        // clone local git
        git = new Git(monorepoRoot.toFile());
        git.clone("file://" + remoteRoot.toAbsolutePath());
        git.configureIdentity(new User("John Doe", "no-reply@acme.com"));

        // so that the default branch can be determined
        git.symbolicRef("refs/remotes/origin/HEAD", "refs/remotes/origin/trunk");

        Files.createDirectory(monorepoRoot.resolve("lib"));
    }

    private void act() throws IOException, ProcessFailedException {
        Options options = ImmutableOptions.builder()
                .monorepoRoot(monorepoRoot.toFile())
                .path("lib")
                .formatOptions(Defaults.defaultFormatOptions())
                .nextVersion(new SemVer(1, 0, 0))
                .push(false)
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

    @Nested
    class GitValidationIT {
        @Test
        void testDefaultBranch() throws IOException, ProcessFailedException {
            // arrange
            Files.writeString(monorepoRoot.resolve("pom.xml"), VALID_PARENT_POM_CONTENTS);
            Files.writeString(monorepoRoot.resolve("lib").resolve("pom.xml"), VALID_CHILD_POM_CONTENTS);
            git.addAll();
            git.commit("chore: Added pom.xml");

            // act
            git.checkoutNewBranch("develop");

            // assert
            assertThatThrownBy(MavenReleaserIT.this::act)
                    .hasMessage("repo was not on default branch (expected trunk, found develop)");
        }

        @Test
        void testNoUntrackedFiles() throws IOException, ProcessFailedException {
            // arrange
            addCommitWithInvalidXml();
            Files.writeString(monorepoRoot.resolve("lib").resolve("pom.xml"), VALID_CHILD_POM_CONTENTS);
            git.addAll();
            git.commit("Fixed pom issues");

            // act
            Files.writeString(monorepoRoot.resolve("pom2.xml"), VALID_CHILD_POM_CONTENTS);

            // assert
            assertThatThrownBy(MavenReleaserIT.this::act).hasMessage("repo has untracked files");
        }

        @Test
        void testNoStagedFiles() throws IOException, ProcessFailedException {
            // arrange
            addCommitWithInvalidXml();
            Files.writeString(monorepoRoot.resolve("lib").resolve("pom.xml"), VALID_CHILD_POM_CONTENTS);

            // act
            git.addAll();

            // act and assert
            assertThatThrownBy(MavenReleaserIT.this::act).hasMessage("repo has staged files");
        }

        @Test
        void testNoModifiedFiles() throws IOException, ProcessFailedException {
            // arrange
            addCommitWithInvalidXml();

            // act
            Files.writeString(monorepoRoot.resolve("lib").resolve("pom.xml"), VALID_CHILD_POM_CONTENTS);

            // act and assert
            assertThatThrownBy(MavenReleaserIT.this::act).hasMessage("repo has modified files");
        }

        private void addCommitWithInvalidXml() throws IOException, ProcessFailedException {
            Files.writeString(monorepoRoot.resolve("pom.xml"), VALID_PARENT_POM_CONTENTS);
            Files.writeString(monorepoRoot.resolve("lib").resolve("pom.xml"), "dummy");
            git.addAll();
            git.commit("Adding incorrect commit");
        }
    }
}
