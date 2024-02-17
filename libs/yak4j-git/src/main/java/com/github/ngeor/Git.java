package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Git {
    private final ProcessHelper processHelper;

    public Git(File workingDirectory) {
        this.processHelper = new ProcessHelper(workingDirectory, "git");
    }

    public String getDefaultBranch() throws IOException, InterruptedException, ProcessFailedException {
        String remote = getOnlyRemote();
        String output = processHelper.run("symbolic-ref", "refs/remotes/" + remote + "/HEAD", "--short");
        return output.replace(remote + "/", "").trim();
    }

    public String getOnlyRemote() throws IOException, InterruptedException, ProcessFailedException {
        List<String> remotes = getRemotes();
        if (remotes.size() != 1) {
            throw new IllegalStateException("Multiple remotes found");
        }

        return remotes.get(0);
    }

    public List<String> getRemotes() throws IOException, InterruptedException, ProcessFailedException {
        return processHelper.run("remote").lines().collect(Collectors.toList());
    }

    public void checkout(String branch) throws IOException, InterruptedException, ProcessFailedException {
        processHelper.run("checkout", branch);
    }

    public void pull() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.run("pull");
    }

    public void subTreeAdd(String destination, File oldRepoRoot, String oldRepoBranch)
            throws IOException, InterruptedException, ProcessFailedException {
        processHelper.run("subtree", "add", "-P", destination, oldRepoRoot.getAbsolutePath(), oldRepoBranch);
    }

    public void addAll() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.run("add", "-A");
    }

    public void commit(String message) throws IOException, InterruptedException, ProcessFailedException {
        processHelper.run("commit", "-m", message);
    }

    public void push() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.run("push");
    }

    /**
     * Gets the most recent tag that starts with the given prefix.
     * The prefix is stripped from the returned value.
     * @param prefix The prefix of the tag.
     * @return The most recent tag, stripped of the given prefix.
     */
    public Optional<String> getMostRecentTag(String prefix)
            throws IOException, InterruptedException, ProcessFailedException {
        return getMostRecentTag(prefix, null, s -> s.substring(prefix.length()));
    }

    public Optional<String[]> getMostRecentTagWithDate(String prefix)
            throws IOException, InterruptedException, ProcessFailedException {
        return getMostRecentTag(
                prefix, "--format=\"%(refname:strip=2),%(creatordate)\"", s -> s.substring(prefix.length())
                        .split(","));
    }

    private <E> Optional<E> getMostRecentTag(String prefix, String format, Function<String, E> mapper)
            throws IOException, InterruptedException, ProcessFailedException {
        List<String> args = new ArrayList<>(Arrays.asList("tag", "-l", prefix + "*", "--sort=-version:refname"));
        if (format != null) {
            args.add(format);
        }
        String output = processHelper.run(args.toArray(String[]::new));
        return output.lines().map(mapper).findFirst();
    }

    public void add(String file) throws IOException, InterruptedException, ProcessFailedException {
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
    public Stream<Commit> revList(String since, String path)
            throws IOException, InterruptedException, ProcessFailedException {
        String commit = since == null ? null : since + "..HEAD";
        return processHelper
                .run(Stream.of("rev-list", "--all", "--format=%H|%as|%D|%s", commit, path)
                        .filter(Objects::nonNull)
                        .toArray(String[]::new))
                .lines()
                .flatMap(line -> Commit.parse(line).stream());
    }

    public void init() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.run("init");
    }

    public void tag(String tag) throws IOException, ProcessFailedException, InterruptedException {
        processHelper.run("tag", tag);
    }

    public void config(String key, String value) throws IOException, ProcessFailedException, InterruptedException {
        processHelper.run("config", key, value);
    }
}
