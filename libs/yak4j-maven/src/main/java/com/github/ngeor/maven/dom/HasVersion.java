package com.github.ngeor.maven.dom;

public interface HasVersion {
    String version();

    default boolean isSnapshot() {
        String value = version();
        return value != null && value.endsWith("-SNAPSHOT");
    }
}
