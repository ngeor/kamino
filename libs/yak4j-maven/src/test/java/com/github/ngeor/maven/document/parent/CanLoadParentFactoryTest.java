package com.github.ngeor.maven.document.parent;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.document.loader.CachedDocumentDecorator;
import com.github.ngeor.maven.document.loader.DocumentLoaderFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CanLoadParentFactoryTest {
    private final LocalRepositoryLocator localRepositoryLocator = () -> {
        throw new IllegalStateException("oops");
    };
    private final ParentPomFinder parentPomFinder = new DefaultParentPomFinder(localRepositoryLocator);

    @TempDir
    private Path tempDir;

    @Nested
    class WithoutCache {
        private final DocumentLoaderFactory<CanLoadParent> factory =
                FileDocumentLoader.asFactory().decorate(f -> new CanLoadParentFactory(f, parentPomFinder));

        @Test
        void noParent() throws IOException {
            Files.writeString(tempDir.resolve("pom.xml"), """
            <project>
            </project>""");
            assertThat(factory.createDocumentLoader(tempDir, "pom.xml").loadParent())
                    .isEmpty();
        }

        @Test
        void hasParent() throws IOException {
            Files.writeString(
                    tempDir.resolve("parent.xml"),
                    """
            <project>
            hello, world!
            </project>""");
            Files.writeString(
                    tempDir.resolve("child.xml"),
                    """
            <project>
                <parent>
                    <groupId>a</groupId>
                    <artifactId>b</artifactId>
                    <version>1.0</version>
                    <relativePath>./parent.xml</relativePath>
                </parent>
            </project>""");
            CanLoadParent child = factory.createDocumentLoader(tempDir, "child.xml");
            Optional<CanLoadParent> parent = child.loadParent();
            assertThat(parent).isNotEmpty();
            DocumentWrapper parentDoc = parent.get().loadDocument();
            assertThat(parentDoc.getDocumentElement().getTextContentTrimmed()).contains("hello, world!");
        }
    }

    @Nested
    class LoadParent {
        private final DocumentLoaderFactory<CanLoadParent> cachedFactory = FileDocumentLoader.asFactory()
                .decorate(CachedDocumentDecorator::decorateFactory)
                .decorate(f -> new CanLoadParentFactory(f, parentPomFinder));

        @Test
        void loadParentTwice() throws IOException {
            // arrange
            Files.writeString(tempDir.resolve("parent.xml"), """
                <project>hello</project>""");
            Path pomXmlPath = tempDir.resolve("child.xml");
            Files.writeString(
                    pomXmlPath,
                    """
            <project>
                <parent>
                    <groupId>a</groupId>
                    <artifactId>b</artifactId>
                    <version>1.0</version>
                    <relativePath>./parent.xml</relativePath>
                </parent>
            </project>""");
            DocumentWrapper parent1 = cachedFactory
                    .createDocumentLoader(pomXmlPath)
                    .loadParent()
                    .orElseThrow()
                    .loadDocument();
            DocumentWrapper parent2 = cachedFactory
                    .createDocumentLoader(pomXmlPath)
                    .loadParent()
                    .orElseThrow()
                    .loadDocument();
            assertThat(parent1).isNotNull().isSameAs(parent2);
        }
    }
}
