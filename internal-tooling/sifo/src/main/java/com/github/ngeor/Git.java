package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

        return remotes.getFirst();
    }

    public List<String> getRemotes() throws IOException, InterruptedException {
        return processHelper.run("remote").lines().toList();
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
        return output.lines().findFirst().orElseThrow();
    }
}
