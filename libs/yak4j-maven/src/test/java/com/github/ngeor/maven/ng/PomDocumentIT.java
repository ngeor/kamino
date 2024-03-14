package com.github.ngeor.maven.ng;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PomDocumentIT {
    @TempDir
    private Path tempDir;

    @Test
    void test() throws IOException {
        // copy resources to temp dir
        copyResource("/2level/grandparent.xml", tempDir.resolve("pom.xml"));
        Files.createDirectory(tempDir.resolve("parent"));
        copyResource("/2level/parent.xml", tempDir.resolve("parent").resolve("pom.xml"));
        Files.createDirectory(tempDir.resolve("parent").resolve("child1"));
        copyResource("/2level/child1.xml", tempDir.resolve("parent").resolve("child1").resolve("pom.xml"));
        Files.createDirectory(tempDir.resolve("parent").resolve("child2"));
        copyResource("/2level/child2.xml", tempDir.resolve("parent").resolve("child2").resolve("pom.xml"));

        // create factory
        PomDocumentFactory factory = new PomDocumentFactory();

        // load child1
        PomDocument child1 = factory.create(tempDir.resolve("parent").resolve("child1").resolve("pom.xml"));
        assertThat(child1).isNotNull();
        // load again
        assertThat(factory.create(tempDir.resolve("parent").resolve("child1").resolve("pom.xml")))
            .as("Should cache instances of PomDocument")
            .isSameAs(child1);
        // load child2
        PomDocument child2 = factory.create(tempDir.resolve("parent").resolve("child2").resolve("pom.xml"));
        assertThat(child2).isNotNull().isNotSameAs(child1);
        // load document of child1 twice
        DocumentWrapper child1Doc = child1.loadDocument();
        assertThat(child1Doc).isNotNull();
        assertThat(child1.loadDocument()).as("Should cache instances of DocumentWrapper").isSameAs(child1Doc);
        assertThat(child2.loadDocument()).isNotNull().isNotSameAs(child1Doc);
        // parent of child1 should be same instance as parent of child2
        assertThat(child1.parent()).isNotEmpty();
        PomDocument parent = child1.parent().orElseThrow();
        assertThat(child2.parent()).isNotEmpty().containsSame(parent);
        assertThat(child1.parent().orElseThrow().loadDocument()).isNotNull().isSameAs(child2.parent().orElseThrow().loadDocument());
    }

    private void copyResource(String resourceName, Path file) {
        try (InputStream is = Objects.requireNonNull( getClass().getResourceAsStream(resourceName) ); FileOutputStream fos = new FileOutputStream(file.toFile())) {
            is.transferTo(fos);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
