package com.github.ngeor.maven.resolve.cache;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.resolve.input.DefaultParentLoader;
import com.github.ngeor.maven.resolve.input.FileInput;
import com.github.ngeor.maven.resolve.input.InputFactory;
import com.github.ngeor.maven.resolve.input.LocalRepositoryLocator;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CachedDocumentDecoratorTest {
    private final InputFactory factory =
            FileInput.asFactory().decorate(f -> CachedDocumentDecorator.decorateFactory(f, new HashMap<>()));
    private final LocalRepositoryLocator localRepositoryLocator = () -> {
        throw new IllegalStateException("oops");
    };
    private final DefaultParentLoader parentLoader = new DefaultParentLoader(factory, localRepositoryLocator);

    @TempDir
    private Path tempDir;

    @Nested
    class Load {
        private DocumentWrapper doc1;
        private DocumentWrapper doc2;

        @Test
        void loadDifferentFiles() throws IOException {
            Files.writeString(tempDir.resolve("a.xml"), "<project></project>");
            Files.writeString(tempDir.resolve("b.xml"), "<project></project>");
            act("a.xml", "b.xml");
            assertThat(doc1).isNotNull();
            assertThat(doc2).isNotNull().isNotSameAs(doc1);
        }

        @Test
        void loadSameFile() throws IOException {
            Files.writeString(tempDir.resolve("a.xml"), "<project></project>");
            act("a.xml", "a.xml");
            assertThat(doc2).isNotNull().isSameAs(doc1);
        }

        @Test
        void loadSameFilesCanonical() throws IOException {
            Files.writeString(tempDir.resolve("a.xml"), "<project></project>");
            act("./a.xml", "a.xml");
            assertThat(doc2).isNotNull().isSameAs(doc1);
        }

        private void act(String file1, String file2) {
            doc1 = factory.load(new File(tempDir.toFile(), file1)).document();
            doc2 = factory.load(new File(tempDir.toFile(), file2)).document();
        }
    }

    @Nested
    class LoadParent {
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
                        <relativePath>parent.xml</relativePath>
                    </parent>
                </project>""");
            DocumentWrapper parent1 = parentLoader
                    .loadParent(factory.load(pomXmlPath.toFile()))
                    .orElseThrow()
                    .document();
            DocumentWrapper parent2 = parentLoader
                    .loadParent(factory.load(pomXmlPath.toFile()))
                    .orElseThrow()
                    .document();
            assertThat(parent1).isNotNull().isSameAs(parent2);
        }
    }
}
