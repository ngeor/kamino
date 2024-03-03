package com.github.ngeor.maven.document.parent;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ParentDocumentLoaderIteratorTest {
    @TempDir
    private Path tempDir;

    private final LocalRepositoryLocator localRepositoryLocator = () -> {
        throw new IllegalStateException("oops");
    };
    private final ParentPomFinder parentPomFinder = new DefaultParentPomFinder(localRepositoryLocator);

    private final DocumentLoaderFactory<CanLoadParent> factory =
            FileDocumentLoader.asFactory().decorate(f -> new CanLoadParentFactory(f, parentPomFinder));

    @Test
    void noParent() throws IOException {
        // arrange
        Files.writeString(tempDir.resolve("pom.xml"), """
            <project>
            </project>""");
        CanLoadParent next = factory.createDocumentLoader(tempDir, "pom.xml");

        // act
        ParentDocumentLoaderIterator parentDocumentLoaderIterator = new ParentDocumentLoaderIterator(next);

        // assert
        assertThat(parentDocumentLoaderIterator).toIterable().isEmpty();
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
        CanLoadParent next = factory.createDocumentLoader(tempDir, "child.xml");

        // act
        ParentDocumentLoaderIterator parentDocumentLoaderIterator = new ParentDocumentLoaderIterator(next);

        // assert
        assertThat(parentDocumentLoaderIterator)
                .toIterable()
                .containsExactly(factory.createDocumentLoader(tempDir, "parent.xml"));
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
        CanLoadParent next = factory.createDocumentLoader(tempDir, "child.xml");

        // act
        ParentDocumentLoaderIterator parentDocumentLoaderIterator = new ParentDocumentLoaderIterator(next);

        // assert
        assertThat(parentDocumentLoaderIterator)
                .toIterable()
                .containsExactly(
                        factory.createDocumentLoader(tempDir, "parent.xml"),
                        factory.createDocumentLoader(tempDir, "grandparent.xml"));
    }
}
