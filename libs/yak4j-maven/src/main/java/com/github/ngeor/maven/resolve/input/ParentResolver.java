package com.github.ngeor.maven.resolve.input;

public interface ParentResolver {
    Input resolveWithParentRecursively(Input input);
}
