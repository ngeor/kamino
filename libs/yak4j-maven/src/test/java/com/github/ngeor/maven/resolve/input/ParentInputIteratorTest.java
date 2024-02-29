package com.github.ngeor.maven.resolve.input;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParentInputIteratorTest {
    @TempDir
    private Path tempDir;

    private final InputFactory factory = FileInput.asFactory();
    private final ParentLoader parentLoader = new DefaultParentLoader(factory, () -> null);

    @Test
    void noParent() throws IOException {
        // arrange
        Files.writeString(tempDir.resolve("pom.xml"), """
            <project>
            </project>""");

        // act
        ParentInputIterator parentInputIterator =
                new ParentInputIterator(factory.load(tempDir.resolve("pom.xml").toFile()), parentLoader);

        // assert
        assertThat(parentInputIterator).toIterable().isEmpty();
    }

    @Test
    void withParent() throws IOException {
        // arrange
        Files.writeString(
                tempDir.resolve("child.xml"),
                """
            <project>
                <parent>
                    <groupId>a</groupId>
                    <artifactId>b</artifactId>
                    <version>1</version>
                    <relativePath>parent.xml</relativePath>
                </parent>
            </project>""");
        Files.writeString(
                tempDir.resolve("parent.xml"),
                """
            <project>
                <groupId>a</groupId>
                <artifactId>b</artifactId>
                <version>1</version>
            </project>""");

        // act
        ParentInputIterator parentInputIterator = new ParentInputIterator(
                factory.load(tempDir.resolve("child.xml").toFile()), parentLoader);

        // assert
        assertThat(parentInputIterator)
                .toIterable()
                .containsExactly(factory.load(tempDir.resolve("parent.xml").toFile()));
    }

    @Test
    void withGrandParent() throws IOException {
        // arrange
        Files.writeString(
                tempDir.resolve("child.xml"),
                """
            <project>
                <parent>
                    <groupId>a</groupId>
                    <artifactId>b</artifactId>
                    <version>1</version>
                    <relativePath>parent.xml</relativePath>
                </parent>
            </project>""");
        Files.writeString(
                tempDir.resolve("parent.xml"),
                """
            <project>
                <parent>
                    <groupId>x</groupId>
                    <artifactId>y</artifactId>
                    <version>2</version>
                    <relativePath>grandparent.xml</relativePath>
                </parent>
            </project>""");
        Files.writeString(
                tempDir.resolve("grandparent.xml"),
                """
            <project>
                <groupId>x</groupId>
                <artifactId>y</artifactId>
                <version>2</version>
            </project>""");

        // act
        ParentInputIterator parentInputIterator = new ParentInputIterator(
                factory.load(tempDir.resolve("child.xml").toFile()), parentLoader);

        // assert
        assertThat(parentInputIterator)
                .toIterable()
                .containsExactly(
                        factory.load(tempDir.resolve("parent.xml").toFile()),
                        factory.load(tempDir.resolve("grandparent.xml").toFile()));
    }
}
