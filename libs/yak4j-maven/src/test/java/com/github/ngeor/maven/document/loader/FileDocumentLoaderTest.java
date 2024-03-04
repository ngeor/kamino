package com.github.ngeor.maven.document.loader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileDocumentLoaderTest {
    private final DocumentLoaderFactory<DocumentLoader> factory = FileDocumentLoader.asFactory();

    @Nested
    class LoadDocument {
        @TempDir
        private Path rootDir;

        private File rootPom;

        @BeforeEach
        void beforeEach() throws IOException {
            rootPom = new File(rootDir.toFile(), "pom.xml");
            Files.writeString(rootPom.toPath(), """
                <project>hello world</project>""");
        }

        @Test
        void loadDocument() {
            DocumentLoader input = factory.createDocumentLoader(rootPom);
            DocumentWrapper document = input.loadDocument();
            assertThat(document).isNotNull();
            assertThat(document.getDocumentElement().getTextContent()).isEqualTo("hello world");
        }

        @Test
        void loadDocumentTwiceReturnsDifferentInstance() {
            DocumentLoader input = factory.createDocumentLoader(rootPom);
            DocumentWrapper doc1 = input.loadDocument();
            DocumentWrapper doc2 = input.loadDocument();
            assertThat(doc1).isNotNull().isNotSameAs(doc2);
        }

        @Test
        void fileNotFound() {
            DocumentLoader input = factory.createDocumentLoader(rootDir, "oops.xml");
            assertThatThrownBy(input::loadDocument)
                    .hasCauseInstanceOf(FileNotFoundException.class)
                    .hasMessageContaining("oops.xml");
        }
    }
}
