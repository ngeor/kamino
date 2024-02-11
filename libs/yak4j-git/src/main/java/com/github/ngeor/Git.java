package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Git {
    private final ProcessHelper processHelper;

    public Git(File workingDirectory) {
        this.processHelper = new ProcessHelper(workingDirectory, "git");
    }

    public String getDefaultBranch() throws IOException, InterruptedException {
        String remote = getOnlyRemote();
        String output = processHelper.run("symbolic-ref", "refs/remotes/" + remote + "/HEAD", "--short");
        return output.replace(remote + "/", "").trim();
    }

    public String getOnlyRemote() throws IOException, InterruptedException {
        List<String> remotes = getRemotes();
        if (remotes.size() != 1) {
            throw new IllegalStateException("Multiple remotes found");
        }

        return remotes.get(0);
    }

    public List<String> getRemotes() throws IOException, InterruptedException {
        return processHelper.run("remote").lines().collect(Collectors.toList());
    }

    public void checkout(String branch) throws IOException, InterruptedException {
        processHelper.run("checkout", branch);
    }

    public void pull() throws IOException, InterruptedException {
        processHelper.run("pull");
    }

    public void subTreeAdd(String destination, File oldRepoRoot, String oldRepoBranch)
            throws IOException, InterruptedException {
        processHelper.run("subtree", "add", "-P", destination, oldRepoRoot.getAbsolutePath(), oldRepoBranch);
    }

    public void addAll() throws IOException, InterruptedException {
        processHelper.run("add", "-A");
    }

    public void commit(String message) throws IOException, InterruptedException {
        processHelper.run("commit", "-m", message);
    }

    public void push() throws IOException, InterruptedException {
        processHelper.run("push");
    }

    public String getMostRecentTag(String prefix) throws IOException, InterruptedException {
        String output = processHelper.run("tag", "-l", prefix + "*", "--sort=-version:refname");
        return output.lines()
                .map(s -> s.substring(prefix.length()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Could not find any git tags starting with %s", prefix)));
    }

    public void add(String file) throws IOException, InterruptedException {
        processHelper.run("add", file);
    }

    public boolean hasStagedChanges() throws IOException, InterruptedException {
        return !processHelper.tryRun("diff", "--cached", "--quiet");
    }

    /**
     * Returns the output of {@code git rev-list --all --format=%H|%as|%D|%s}.
     * <p>
     * The output consists of lines that are in the format
     * {@code commit SHA}
     * alternating with lines that are in the format
     * {@code SHA|yyyy-MM-dd|object|summary}
     * where "object" can be in the form {@code tag: tagname} for tags.
     */
    public Stream<Commit> revList(String commit, String path) throws IOException, InterruptedException {
        return processHelper
                .run(Stream.of("rev-list", "--all", "--format=%H|%as|%D|%s", commit, path)
                        .filter(Objects::nonNull)
                        .toArray(String[]::new))
                .lines()
                .flatMap(line -> Commit.parse(line).stream());
    }
}
