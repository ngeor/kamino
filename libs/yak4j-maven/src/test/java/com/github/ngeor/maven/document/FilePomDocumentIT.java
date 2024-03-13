package com.github.ngeor.maven.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.maven.dom.MavenCoordinates;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FilePomDocumentIT {
    @TempDir
    private Path tempDir;

    @Test
    void test() throws IOException {
        File pomFile = tempDir.resolve("pom.xml").toFile();
        try (InputStream is = Objects.requireNonNull(getClass().getResourceAsStream("/pom1.xml"));
                FileOutputStream fos = new FileOutputStream(pomFile)) {
            is.transferTo(fos);
        }
        FilePomDocument filePomDocument = new FilePomDocument(pomFile);
        assertThat(filePomDocument.coordinates()).isEqualTo(new MavenCoordinates("com.acme", "foo", "1.0"));
    }
}
