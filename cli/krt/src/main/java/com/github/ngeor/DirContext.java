package com.github.ngeor;

import java.nio.file.Path;
import java.util.Objects;

public class DirContext {
    private final Path currentDir;
    private final Path repoDir;

    public DirContext(Path currentDir, Path repoDir) {
        this.currentDir = currentDir;
        this.repoDir = repoDir;
    }

    public static DirContext build() {
        Path currentDir = Path.of(".").toAbsolutePath().normalize();
        return build(currentDir);
    }

    public static DirContext build(Path currentDir) {
        GitDirFinder gitDirFinder = new GitDirFinder();
        Path repoDir = Objects.requireNonNull(gitDirFinder.find(currentDir), "Could not detect git directory");
        return new DirContext(currentDir, repoDir);
    }

    public Path getCurrentDir() {
        return currentDir;
    }

    public Path getRepoDir() {
        return repoDir;
    }

    public String resolveRelative(String path) {
        return repoDir.relativize(currentDir.resolve(path)).toString();
    }

    public boolean isTopLevelProject() {
        return currentDir.equals(repoDir);
    }

    public String getProjectName() {
        return currentDir.getFileName().toString();
    }
}
