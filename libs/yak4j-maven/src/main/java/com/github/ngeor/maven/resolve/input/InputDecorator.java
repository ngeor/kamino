package com.github.ngeor.maven.resolve.input;

import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.util.Objects;

public abstract class InputDecorator implements Input {
    private final Input decorated;

    protected InputDecorator(Input decorated) {
        this.decorated = Objects.requireNonNull(decorated);
    }

    @Override
    public DocumentWrapper document() {
        return decorated.document();
    }

    @Override
    public File pomFile() {
        return decorated.pomFile();
    }

    @Override
    public String toString() {
        return String.format("%s %s", getClass().getSimpleName(), decorated);
    }

    protected Input getDecorated() {
        return decorated;
    }
}
