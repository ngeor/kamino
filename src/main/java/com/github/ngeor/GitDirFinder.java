package com.github.ngeor;

import java.nio.file.Path;

public class GitDirFinder {
    public Path find(Path directory) {
        Path current = directory;
        boolean found = false;
        while (!found && current != null) {
            Path git = current.resolve(".git");
            if (git.toFile().isDirectory()) {
                found = true;
            } else {
                current = current.getParent();
            }
        }
        return current;
    }
}
