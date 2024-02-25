package com.github.ngeor.maven.resolve;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public record FileInput(File pomFile) implements Input {
    public FileInput {
        Objects.requireNonNull(pomFile);
        Validate.isTrue(pomFile.isFile(), "%s is not a file", pomFile);
    }

    @Override
    public DocumentWrapper loadDocument() {
        return DocumentWrapper.parse(pomFile);
    }
}
