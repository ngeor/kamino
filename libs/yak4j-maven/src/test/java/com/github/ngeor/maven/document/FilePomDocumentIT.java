package com.github.ngeor.maven.document;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.io.TempDir;

class FilePomDocumentIT extends BasePomDocumentTest<FilePomDocument> {
    @TempDir
    private Path tempDir;

    @Override
    protected FilePomDocument createDocument(String resourceName, UnaryOperator<DocumentWrapper> documentDecorator) {
        File pomFile = tempDir.resolve("pom.xml").toFile();

        try (InputStream is = Objects.requireNonNull(getClass().getResourceAsStream(resourceName));
                FileOutputStream fos = new FileOutputStream(pomFile)) {
            is.transferTo(fos);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        return new FilePomDocument(pomFile) {
            @Override
            protected DocumentWrapper doLoadDocument() {
                return documentDecorator.apply(super.doLoadDocument());
            }
        };
    }
}
