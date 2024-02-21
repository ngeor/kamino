package com.github.ngeor.mr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.Commit;
import com.github.ngeor.Git;
import com.github.ngeor.ProcessFailedException;
import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.MavenDocument;
import com.github.ngeor.versions.SemVer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MavenReleaserIT {
    private Path monorepoRoot;
    private Git git;

    @BeforeEach
    void beforeEach() throws IOException, ProcessFailedException, InterruptedException {
        monorepoRoot = Files.createTempDirectory("test");
        Files.createDirectory(monorepoRoot.resolve("lib"));
        git = new Git(monorepoRoot.toFile());
        git.init();
    }

    @AfterEach
    void afterEach() throws IOException {
        FileUtils.deleteDirectory(monorepoRoot.toFile());
    }

    @Test
    void test() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        Files.writeString(
                monorepoRoot.resolve("pom.xml"),
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>monorepo</artifactId>
                <version>1.0-SNAPSHOT</version>
                <packaging>pom</packaging>
                <modules>
                    <module>lib</module>
                </modules>
            </project>
            """);
        Files.writeString(
                monorepoRoot.resolve("lib").resolve("pom.xml"),
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0-SNAPSHOT</version>
                <name>foo</name>
                <description>The library</description>
            </project>
            """);
        git.addAll();
        git.commit("chore: Added pom.xml");

        MavenReleaser releaser = new MavenReleaser(monorepoRoot.toFile(), "lib");

        // act
        releaser.prepareRelease(new SemVer(1, 0, 0), false);

        // assert
        MavenDocument mavenDocument = MavenDocument.effectivePomWithoutResolvingProperties(
                monorepoRoot.resolve("lib").resolve("pom.xml"));
        assertThat(mavenDocument.coordinates()).isEqualTo(new MavenCoordinates("com.acme", "foo", "1.1.0-SNAPSHOT"));
        List<Commit> commits = git.revList(null).toList();
        assertThat(commits.stream().map(Commit::summary))
                .containsExactly(
                        "release(lib): switching to development version 1.1.0-SNAPSHOT",
                        "release(lib): releasing 1.0.0",
                        "chore: Added pom.xml");
        assertThat(commits.stream().map(Commit::tag)).containsExactly(null, "lib/v1.0.0", null);
    }

    @Test
    void testModelVersionIsRequired() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        Files.writeString(
                monorepoRoot.resolve("pom.xml"),
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>monorepo</artifactId>
                <version>1.0-SNAPSHOT</version>
                <packaging>pom</packaging>
                <modules>
                    <module>lib</module>
                </modules>
            </project>
            """);
        Files.writeString(
                monorepoRoot.resolve("lib").resolve("pom.xml"),
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0-SNAPSHOT</version>
            </project>
            """);
        git.addAll();
        git.commit("chore: Added pom.xml");

        MavenReleaser releaser = new MavenReleaser(monorepoRoot.toFile(), "lib");

        // act and assert
        assertThatThrownBy(() -> releaser.prepareRelease(new SemVer(1, 0, 0), false))
                .hasMessageContaining("modelVersion");
    }

    @Test
    void testGroupIdIsRequired() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        Files.writeString(
                monorepoRoot.resolve("pom.xml"),
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>monorepo</artifactId>
                <version>1.0-SNAPSHOT</version>
                <packaging>pom</packaging>
                <modules>
                    <module>lib</module>
                </modules>
            </project>
            """);
        Files.writeString(
                monorepoRoot.resolve("lib").resolve("pom.xml"),
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <artifactId>foo</artifactId>
                <version>1.0-SNAPSHOT</version>
            </project>
            """);
        git.addAll();
        git.commit("chore: Added pom.xml");

        MavenReleaser releaser = new MavenReleaser(monorepoRoot.toFile(), "lib");

        // act and assert
        assertThatThrownBy(() -> releaser.prepareRelease(new SemVer(1, 0, 0), false))
                .hasMessageContaining("groupId");
    }

    @Test
    void testArtifactIdIsRequired() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        Files.writeString(
                monorepoRoot.resolve("pom.xml"),
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>monorepo</artifactId>
                <version>1.0-SNAPSHOT</version>
                <packaging>pom</packaging>
                <modules>
                    <module>lib</module>
                </modules>
            </project>
            """);
        Files.writeString(
                monorepoRoot.resolve("lib").resolve("pom.xml"),
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <version>1.0-SNAPSHOT</version>
            </project>
            """);
        git.addAll();
        git.commit("chore: Added pom.xml");

        MavenReleaser releaser = new MavenReleaser(monorepoRoot.toFile(), "lib");

        // act and assert
        assertThatThrownBy(() -> releaser.prepareRelease(new SemVer(1, 0, 0), false))
                .hasMessageContaining("artifactId");
    }

    @Test
    void testVersionIsRequired() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        Files.writeString(
                monorepoRoot.resolve("pom.xml"),
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>monorepo</artifactId>
                <version>1.0-SNAPSHOT</version>
                <packaging>pom</packaging>
                <modules>
                    <module>lib</module>
                </modules>
            </project>
            """);
        Files.writeString(
                monorepoRoot.resolve("lib").resolve("pom.xml"),
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
            </project>
            """);
        git.addAll();
        git.commit("chore: Added pom.xml");

        MavenReleaser releaser = new MavenReleaser(monorepoRoot.toFile(), "lib");

        // act and assert
        assertThatThrownBy(() -> releaser.prepareRelease(new SemVer(1, 0, 0), false))
                .hasMessageContaining("version is missing com.acme:foo");
    }

    @Test
    void testNameIsRequired() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        Files.writeString(
                monorepoRoot.resolve("pom.xml"),
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>monorepo</artifactId>
                <version>1.0-SNAPSHOT</version>
                <packaging>pom</packaging>
                <modules>
                    <module>lib</module>
                </modules>
            </project>
            """);
        Files.writeString(
                monorepoRoot.resolve("lib").resolve("pom.xml"),
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0-SNAPSHOT</version>
            </project>
            """);
        git.addAll();
        git.commit("chore: Added pom.xml");

        MavenReleaser releaser = new MavenReleaser(monorepoRoot.toFile(), "lib");

        // act and assert
        assertThatThrownBy(() -> releaser.prepareRelease(new SemVer(1, 0, 0), false))
                .hasMessageContaining("Cannot release com.acme:foo without name element");
    }

    @Test
    void testDescriptionIsRequired() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        Files.writeString(
                monorepoRoot.resolve("pom.xml"),
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>monorepo</artifactId>
                <version>1.0-SNAPSHOT</version>
                <packaging>pom</packaging>
                <modules>
                    <module>lib</module>
                </modules>
            </project>
            """);
        Files.writeString(
                monorepoRoot.resolve("lib").resolve("pom.xml"),
                """
            <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>com.acme</groupId>
                <artifactId>foo</artifactId>
                <version>1.0-SNAPSHOT</version>
                <name>foo</name>
            </project>
            """);
        git.addAll();
        git.commit("chore: Added pom.xml");

        MavenReleaser releaser = new MavenReleaser(monorepoRoot.toFile(), "lib");

        // act and assert
        assertThatThrownBy(() -> releaser.prepareRelease(new SemVer(1, 0, 0), false))
                .hasMessageContaining("Cannot release com.acme:foo without description element");
    }
}
