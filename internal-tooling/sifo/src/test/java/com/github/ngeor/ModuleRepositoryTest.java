package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.dom.MavenCoordinates;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.apache.commons.lang3.function.Failable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ModuleRepositoryTest {
    @TempDir
    private Path tempDir;

    private final ModuleRepository moduleRepository = new ModuleRepository();

    @BeforeEach
    void beforeEach() throws IOException {
        Files.writeString(
                tempDir.resolve("pom.xml"),
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>aggregator</artifactId>
                <version>1.0</version>
                <modules>
                    <module>alpha</module>
                    <module>beta</module>
                    <module>gamma</module>
                    <module>delta</module>
                </modules>
            </project>""");
        Set.of("alpha", "beta", "gamma", "delta")
                .forEach(Failable.asConsumer(name -> Files.createDirectory(tempDir.resolve(name))));
        Files.writeString(
                tempDir.resolve("alpha").resolve("pom.xml"),
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>alpha</artifactId>
                <version>1.0-SNAPSHOT</version>
                <dependencies>
                    <dependency>
                        <groupId>com.acme</groupId>
                        <artifactId>beta</artifactId>
                        <version>1.0-SNAPSHOT</version>
                    </dependency>
                    <dependency>
                        <groupId>com.external</groupId>
                        <artifactId>should-not-match</artifactId>
                        <version>3.2.1</version>
                    </dependency>
                </dependencies>
            </project>
            """);
        Files.writeString(
                tempDir.resolve("beta").resolve("pom.xml"),
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>beta</artifactId>
                <version>1.0-SNAPSHOT</version>
                <dependencies>
                    <dependency>
                        <groupId>com.acme</groupId>
                        <artifactId>gamma</artifactId>
                        <version>1.0-SNAPSHOT</version>
                    </dependency>
                    <dependency>
                        <groupId>com.external</groupId>
                        <artifactId>should-not-match</artifactId>
                        <version>3.2.1</version>
                    </dependency>
                </dependencies>
                <parent>
                    <groupId>com.acme</groupId>
                    <artifactId>gamma</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    <relativePath>../gamma</relativePath>
                </parent>
            </project>
            """);
        Files.writeString(
                tempDir.resolve("gamma").resolve("pom.xml"),
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>gamma</artifactId>
                <version>1.0-SNAPSHOT</version>
                <parent>
                    <groupId>com.acme</groupId>
                    <artifactId>delta</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    <relativePath>../delta</relativePath>
                </parent>
            </project>
            """);
        Files.writeString(
                tempDir.resolve("delta").resolve("pom.xml"),
                """
            <project>
                <groupId>com.acme</groupId>
                <artifactId>delta</artifactId>
                <version>1.0-SNAPSHOT</version>
            </project>
            """);

        moduleRepository.loadModules(tempDir.resolve("pom.xml"));
    }

    @Test
    void moduleNames() {
        assertThat(moduleRepository.moduleNames()).containsExactly("alpha", "beta", "gamma", "delta");
    }

    @Test
    void moduleCoordinates() {
        assertThat(moduleRepository.moduleCoordinates("alpha"))
                .isEqualTo(new MavenCoordinates("com.acme", "alpha", "1.0-SNAPSHOT"));
    }

    @Test
    void moduleByCoordinates() {
        assertThat(moduleRepository.moduleByCoordinates(new MavenCoordinates("com.acme", "beta", "1.0-SNAPSHOT")))
                .contains("beta");
    }

    @Test
    void dependenciesOf() {
        assertThat(moduleRepository.dependenciesOf("alpha")).containsExactlyInAnyOrder("beta");
        assertThat(moduleRepository.dependenciesOf("beta")).containsExactlyInAnyOrder("gamma");
        assertThat(moduleRepository.dependenciesOf("gamma")).isEmpty();
    }

    @Test
    void dependenciesOfRecursively() {
        assertThat(moduleRepository.dependenciesOfRecursively("alpha")).containsExactlyInAnyOrder("beta", "gamma");
        assertThat(moduleRepository.dependenciesOfRecursively("beta")).containsExactlyInAnyOrder("gamma");
        assertThat(moduleRepository.dependenciesOfRecursively("gamma")).isEmpty();
    }

    @Test
    void parentSnapshots() {
        assertThat(moduleRepository.parentSnapshotsOfRecursively("alpha")).isEmpty();
        assertThat(moduleRepository.parentSnapshotsOfRecursively("beta")).containsExactlyInAnyOrder("gamma", "delta");
        assertThat(moduleRepository.parentSnapshotsOfRecursively("gamma")).containsExactlyInAnyOrder("delta");
    }
}
