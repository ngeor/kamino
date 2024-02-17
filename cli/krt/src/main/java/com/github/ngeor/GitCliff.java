package com.github.ngeor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GitCliff {
    public void run(DirContext dirContext, String version, Path cliffTomlPath)
            throws IOException, InterruptedException {
        String[] args = buildArgs(dirContext, version, cliffTomlPath);
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = processBuilder.start();
        ProcessUtils.waitForSuccess(process);
    }

    String[] buildArgs(DirContext dirContext, String version, Path cliffTomlPath) {
        List<String> result = new ArrayList<>();
        result.add("git-cliff");
        StringBuilder includePath = new StringBuilder();
        StringBuilder repository = new StringBuilder();
        for (Path p : new PathIterator(dirContext.getRepoDir(), dirContext.getCurrentDir())) {
            includePath.insert(0, '/');
            includePath.insert(0, p.getFileName().toString());
            if (repository.length() > 0) {
                repository.append('/');
            }
            repository.append("..");
        }
        if (includePath.length() > 0) {
            includePath.append('*');
            result.add("--include-path");
            result.add(includePath.toString());
            result.add("-r");
            result.add(repository.toString());
        }
        result.add("-o");
        result.add("CHANGELOG.md");
        result.add("-t");
        result.add(version);
        result.add("-c");
        result.add(cliffTomlPath.toString());
        return result.toArray(new String[0]);
    }

    static final class PathIterator implements Iterable<Path>, Iterator<Path> {
        private final Path parentDirectory;
        private Path current;

        PathIterator(Path parentDirectory, Path childDirectory) {
            this.parentDirectory = parentDirectory;
            this.current = childDirectory;
        }

        @Override
        public Iterator<Path> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return current != null && !current.equals(parentDirectory);
        }

        @Override
        public Path next() {
            Path result = current;
            current = current.getParent();
            return result;
        }
    }
}
