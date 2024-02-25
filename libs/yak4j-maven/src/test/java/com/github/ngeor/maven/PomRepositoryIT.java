package com.github.ngeor.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PomRepositoryIT {
    @TempDir
    private File rootDir;

    private final PomRepository pomRepository = new PomRepository();

    @Test
    void test() throws IOException {
        Files.writeString(
                rootDir.toPath().resolve("pom.xml"),
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>aggregator</artifactId>
                <version>1.0</version>
                <packaging>pom</packaging>
                <modules>
                    <module>grandparent</module>
                    <module>parent</module>
                    <module>child</module>
                </modules>
            </project>""");
        Files.createDirectory(rootDir.toPath().resolve("grandparent"));
        Files.writeString(
                rootDir.toPath().resolve("grandparent").resolve("pom.xml"),
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>grandparent</artifactId>
                <version>2.0</version>
                <packaging>pom</packaging>
                <properties>
                    <java.version>11</java.version>
                </properties>
            </project>
            """);
        Files.createDirectory(rootDir.toPath().resolve("parent"));
        Files.writeString(
                rootDir.toPath().resolve("parent").resolve("pom.xml"),
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>parent</artifactId>
                <version>3.0</version>
                <packaging>pom</packaging>
                <parent>
                    <groupId>com.acme</groupId>
                    <artifactId>grandparent</artifactId>
                    <version>2.0</version>
                    <relativePath>../grandparent</relativePath>
                </parent>
                <properties>
                    <java.version>11</java.version>
                </properties>
            </project>
            """);
        Files.createDirectory(rootDir.toPath().resolve("child"));
        Files.writeString(
                rootDir.toPath().resolve("child").resolve("pom.xml"),
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>child</artifactId>
                <version>4.0</version>
                <parent>
                    <groupId>com.acme</groupId>
                    <artifactId>parent</artifactId>
                    <version>3.0</version>
                    <relativePath>../parent</relativePath>
                </parent>
            </project>
            """);

        assertThat(pomRepository.loadAndResolveProperties(new File(rootDir, "pom.xml")))
                .isNotNull();
        for (String module : List.of("parent", "child", "grandparent")) {
            assertThat(pomRepository.loadAndResolveProperties(new File(new File(rootDir, module), "pom.xml")))
                    .isNotNull();
        }
    }
}
