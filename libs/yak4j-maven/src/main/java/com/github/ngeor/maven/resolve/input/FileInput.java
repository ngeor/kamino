package com.github.ngeor.maven.resolve.input;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;

public record FileInput(File pomFile) implements Input {
    public FileInput {
        Objects.requireNonNull(pomFile);
    }

    @Override
    public DocumentWrapper document() {
        return DocumentWrapper.parse(pomFile);
    }

    public static InputFactory asFactory() {
        return FileInput::new;
    }
}
