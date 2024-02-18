package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Changelog and semantic version calculator.
 */
public final class App {
    private final File rootDirectory;
    private final String path;
    private final String tagPrefix;
    private final Git git;

    App(File rootDirectory, String path, String tagPrefix, Git git) {
        this.rootDirectory = rootDirectory;
        this.path = path;
        this.tagPrefix = tagPrefix;
        this.git = git;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ProcessFailedException {
        ArgumentParser parser = new ArgumentParser();
        parser.addPositionalArgument("path", false);
        parser.addPositionalArgument("version", false);
        parser.addFlagArgument("git-version");
        parser.addFlagArgument("release");
        parser.addFlagArgument("dry-run");
        parser.addFlagArgument("push");
        Map<String, Object> parsedArgs = parser.parse(args);
        // e.g. libs/java
        // ensure path does not end in slashes and is not blank
        String path = sanitize((String) parsedArgs.get("path"));
        // e.g. 4.2.1
        String version = (String) parsedArgs.get("version");
        if (path != null) {
            File rootDirectory = new File(".").toPath().toAbsolutePath().toFile();

            // ensure given path exists
            if (!new File(rootDirectory, path).isDirectory()) {
                throw new IllegalArgumentException("path " + path + " not found");
            }

            Git git = new Git(rootDirectory);

            String tagPrefix = path + "/v";

            App app = new App(rootDirectory, path, tagPrefix, git);
            if (parsedArgs.containsKey("git-version")) {
                app.calculateGitVersion();
            } else if (parsedArgs.containsKey("release")) {
                app.release(parsedArgs.containsKey("dry-run"), parsedArgs.containsKey("push"));
            } else {
                new ChangeLogUpdater(rootDirectory, path, tagPrefix, git).updateChangeLog(version);
            }
        } else {
            new ChangesOverviewCommand().run();
        }
    }

    private SemVer calculateGitVersion() throws IOException, InterruptedException, ProcessFailedException {
        SemVer mostRecentVersion = SemVer.parse(git.getMostRecentTag(tagPrefix).orElseThrow());
        String sinceCommit = tagPrefix + mostRecentVersion;

        List<Commit> commits = git.revList(sinceCommit, path).toList();
        if (commits.isEmpty()) {
            System.out.printf("No commits to %s since %s%n", path, mostRecentVersion);
            return null;
        }

        SemVerBump bump = commits.stream()
                .filter(new CommitFilter())
                .map(Commit::summary)
                .map(this::calculateBump)
                .max(Enum::compareTo)
                .orElse(SemVerBump.MINOR);

        SemVer nextVersion = mostRecentVersion.bump(bump);
        System.out.printf("The next version of %s should be %s (%s)%n", path, nextVersion, bump);
        return nextVersion;
    }

    private void release(boolean dryRun, boolean push)
            throws IOException, InterruptedException, ProcessFailedException {
        SemVer nextVersion = Objects.requireNonNull(calculateGitVersion());
        MavenReleaser.prepareRelease(rootDirectory, path, nextVersion, dryRun, push);
    }

    static String sanitize(String path) {
        if (path == null) {
            return null;
        }

        // ensure path does not end in slash
        while (path.endsWith("\\") || path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path.isBlank() ? null : path;
    }

    SemVerBump calculateBump(String message) {
        if (message.startsWith("fix:")) {
            return SemVerBump.PATCH;
        }

        if (message.matches("^[a-z]+!:.+$")) {
            return SemVerBump.MAJOR;
        }

        return SemVerBump.MINOR;
    }

}
