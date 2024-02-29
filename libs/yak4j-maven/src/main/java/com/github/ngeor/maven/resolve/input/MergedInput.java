package com.github.ngeor.maven.resolve.input;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;

record MergedInput(DocumentWrapper document, File pomFile) implements Input {
    public MergedInput {
        Objects.requireNonNull(document);
        Objects.requireNonNull(pomFile);
    }
}
