package com.github.ngeor.maven.document.loader;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CachedDocumentLoaderFactoryTest {
    private final DocumentLoaderFactory<DocumentLoader> factory =
            FileDocumentLoader.asFactory().decorate(CachedDocumentLoaderFactory::new);

    @TempDir
    private Path tempDir;

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
        doc1 = factory.createDocumentLoader(tempDir, file1).loadDocument();
        doc2 = factory.createDocumentLoader(tempDir, file2).loadDocument();
    }
}
