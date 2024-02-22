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
import org.apache.commons.lang3.Validate;

public final class Git {
    private final ProcessHelper processHelper;

    public Git(File workingDirectory) {
        this.processHelper = new ProcessHelper(workingDirectory, "git");
    }

    /**
     * Gets the default branch of the git repo.
     * The method will return a non-blank value or throw an exception.
     * @return The default branch.
     * @throws IOException
     * @throws InterruptedException
     * @throws ProcessFailedException
     */
    public String getDefaultBranch() throws IOException, InterruptedException, ProcessFailedException {
        String remote = getOnlyRemote();
        String output = processHelper.run("symbolic-ref", "refs/remotes/" + remote + "/HEAD", "--short");
        return Validate.notBlank(output.replace(remote + "/", "").trim());
    }

    /**
     * Gets the current branch of the git repo.
     * The method will return a non-blank value or throw an exception.
     * @return The current branch
     * @throws IOException
     * @throws ProcessFailedException
     * @throws InterruptedException
     */
    public String getCurrentBranch() throws IOException, ProcessFailedException, InterruptedException {
        return Validate.notBlank(
                processHelper.run("rev-parse", "--abbrev-ref", "HEAD").trim());
    }

    public String getOnlyRemote() throws IOException, InterruptedException, ProcessFailedException {
        List<String> remotes = getRemotes();
        Validate.noNullElements(remotes);
        Validate.notEmpty(remotes, "No remotes found");
        Validate.isTrue(remotes.size() == 1, "Multiple remotes found");
        return remotes.get(0);
    }

    public List<String> getRemotes() throws IOException, InterruptedException, ProcessFailedException {
        return processHelper.run("remote").lines().collect(Collectors.toList());
    }

    public void checkout(String branch) throws IOException, InterruptedException, ProcessFailedException {
        Validate.notBlank(branch);
        processHelper.run("checkout", branch);
    }

    public void checkoutNewBranch(String branch) throws IOException, ProcessFailedException, InterruptedException {
        Validate.notBlank(branch);
        processHelper.run("checkout", "-b", branch);
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
        Validate.notBlank(message);
        processHelper.run("commit", "-m", message);
    }

    public void push(PushOption... pushOptions) throws IOException, InterruptedException, ProcessFailedException {
        List<String> args = new ArrayList<>(List.of("push"));
        for (PushOption pushOption : pushOptions) {
            switch (pushOption) {
                case FOLLOW_TAGS -> args.add("--follow-tags");
                case TAGS -> args.add("--tags");
            }
        }
        processHelper.run(args);
    }

    /**
     * Gets the most recent tag that starts with the given prefix.
     * The prefix is not stripped from the returned value.
     * @param prefix The prefix of the tag.
     * @return The most recent tag.
     */
    public Optional<Tag> getMostRecentTag(String prefix)
            throws IOException, InterruptedException, ProcessFailedException {
        return getMostRecentTag(prefix, null, s -> new Tag(s, null));
    }

    public Optional<Tag> getMostRecentTagWithDate(String prefix)
            throws IOException, InterruptedException, ProcessFailedException {
        return getMostRecentTag(prefix, "--format=%(refname:strip=2),%(creatordate)", s -> {
            String[] parts = s.split(",");
            return new Tag(parts[0], parts[1]);
        });
    }

    private Optional<Tag> getMostRecentTag(String prefix, String format, Function<String, Tag> mapper)
            throws IOException, InterruptedException, ProcessFailedException {
        List<String> args = new ArrayList<>(Arrays.asList("tag", "-l", prefix + "*", "--sort=-version:refname"));
        if (format != null) {
            args.add(format);
        }
        String output = processHelper.run(args);
        return output.lines().map(mapper).findFirst();
    }

    public void add(String file) throws IOException, InterruptedException, ProcessFailedException {
        Validate.notBlank(file);
        processHelper.run("add", file);
    }

    public boolean hasStagedChanges() throws IOException, InterruptedException {
        return !processHelper.tryRun("diff", "--cached", "--quiet");
    }

    public boolean hasNonStagedChanges() throws IOException, InterruptedException {
        return !processHelper.tryRun("diff", "--quiet");
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
                        .toList())
                .lines()
                .flatMap(line -> Commit.parse(line).stream());
    }

    public Stream<Commit> revList(Tag since, String path)
            throws IOException, ProcessFailedException, InterruptedException {
        return revList(since.name(), path);
    }

    public Stream<Commit> revList(String path) throws IOException, ProcessFailedException, InterruptedException {
        return revList((String) null, path);
    }

    public void init(InitOption... initOptions) throws IOException, InterruptedException, ProcessFailedException {
        List<String> args = new ArrayList<>(List.of("init"));
        Arrays.stream(initOptions)
                .map(initOption -> switch (initOption) {
                    case BARE -> "--bare";
                })
                .forEach(args::add);
        processHelper.run(args);
    }

    public void tag(String tag) throws IOException, ProcessFailedException, InterruptedException {
        processHelper.run("tag", tag);
    }

    public void config(String key, String value) throws IOException, ProcessFailedException, InterruptedException {
        processHelper.run("config", key, value);
    }

    public void ensureOnDefaultBranch() throws IOException, ProcessFailedException, InterruptedException {
        String defaultBranch = getDefaultBranch();
        String currentBranch = getCurrentBranch();
        Validate.isTrue(
                defaultBranch.equals(currentBranch),
                "repo was not on default branch (expected %s, found %s)",
                defaultBranch,
                currentBranch);
    }

    public void clone(String url) throws IOException, ProcessFailedException, InterruptedException {
        processHelper.run("clone", Validate.notBlank(url), ".");
    }

    public void symbolicRef(String name, String ref) throws IOException, ProcessFailedException, InterruptedException {
        processHelper.run("symbolic-ref", Validate.notBlank(name), Validate.notBlank(ref));
    }

    public Stream<String> lsFiles(LsFilesOption... options)
            throws IOException, ProcessFailedException, InterruptedException {
        List<String> args = new ArrayList<>(List.of("ls-files"));
        Arrays.stream(options)
                .map(option -> switch (option) {
                    case OTHER -> "--other";
                    case EXCLUDE_STANDARD -> "--exclude-standard";
                })
                .forEach(args::add);
        return processHelper.run(args).lines();
    }
}
