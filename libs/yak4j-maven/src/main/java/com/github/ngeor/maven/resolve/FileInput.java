package com.github.ngeor.maven.resolve;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public record FileInput(File pomFile) implements Input {
    public FileInput(File pomFile) {
        Objects.requireNonNull(pomFile);
        Validate.isTrue(pomFile.isFile(), "%s is not a file", pomFile);
        try {
            this.pomFile = pomFile.getCanonicalFile();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public DocumentWrapper loadDocument() {
        return DocumentWrapper.parse(pomFile);
    }
}
