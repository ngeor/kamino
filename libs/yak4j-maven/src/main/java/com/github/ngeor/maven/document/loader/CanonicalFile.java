package com.github.ngeor.maven.document.loader;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

public record CanonicalFile(File file) {
    public CanonicalFile(File file) {
        try {
            this.file = Objects.requireNonNull(file.getCanonicalFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
