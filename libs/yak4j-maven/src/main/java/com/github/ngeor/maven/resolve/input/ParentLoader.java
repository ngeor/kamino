package com.github.ngeor.maven.resolve.input;

import java.util.Optional;

@FunctionalInterface
public interface ParentLoader {
    Optional<Input> loadParent(Input input);
}
