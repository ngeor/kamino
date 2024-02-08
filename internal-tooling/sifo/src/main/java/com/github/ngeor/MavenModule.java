package com.github.ngeor;

import java.io.File;

public record MavenModule(File typeDirectory, File projectDirectory, File pomFile) {
    public String path() {
        return typeDirectory.getName() + "/" + projectDirectory.getName();
    }
}
