package com.github.ngeor.maven.resolve;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public final class FileInput implements Input {
    private final File pomFile;

    public FileInput(File pomFile) throws IOException {
        Objects.requireNonNull(pomFile);
        Validate.isTrue(pomFile.isFile(), "%s is not a file", pomFile);
        this.pomFile = pomFile.getCanonicalFile();
    }

    @Override
    public DocumentWrapper loadDocument() {
        return DocumentWrapper.parse(pomFile);
    }

    @Override
    public String toString() {
        return String.format("%s %s", FileInput.class.getSimpleName(), pomFile);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FileInput that && pomFile.equals(that.pomFile);
    }

    @Override
    public int hashCode() {
        return pomFile.hashCode();
    }

    public File getPomFile() {
        return pomFile;
    }
}
