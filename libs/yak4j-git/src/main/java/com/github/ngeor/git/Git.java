package com.github.ngeor.git;

import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.process.ProcessHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.Validate;

public final class Git {
    public static final String CONFIG_USER_NAME = "user.name";
    public static final String CONFIG_USER_EMAIL = "user.email";
    private final ProcessHelper processHelper;

    public Git(File workingDirectory) {
        this.processHelper = new ProcessHelper(workingDirectory, "git");
    }

    /**
     * Gets the default branch of the git repo.
     * The method will return a non-blank value or throw an exception.
     * @return The default branch.
     * @throws ProcessFailedException
     */
    public String getDefaultBranch() throws ProcessFailedException {
        String remote = getOnlyRemote();
        String output = processHelper.run("symbolic-ref", "refs/remotes/" + remote + "/HEAD", "--short");
        return Validate.notBlank(output.replace(remote + "/", "").trim());
    }

    /**
     * Gets the current branch of the git repo.
     * The method will return a non-blank value or throw an exception.
     * @return The current branch
     * @throws ProcessFailedException
     */
    public String getCurrentBranch() throws ProcessFailedException {
        return Validate.notBlank(
                processHelper.run("rev-parse", "--abbrev-ref", "HEAD").trim());
    }

    public String getOnlyRemote() throws ProcessFailedException {
        List<String> remotes = getRemotes();
        Validate.noNullElements(remotes);
        Validate.notEmpty(remotes, "No remotes found");
        Validate.isTrue(remotes.size() == 1, "Multiple remotes found");
        return remotes.get(0);
    }

    public List<String> getRemotes() throws ProcessFailedException {
        return processHelper.run("remote").lines().toList();
    }

    public void checkout(String branch) throws ProcessFailedException {
        Validate.notBlank(branch);
        processHelper.run("checkout", branch);
    }

    public void checkoutNewBranch(String branch) throws ProcessFailedException {
        Validate.notBlank(branch);
        processHelper.run("checkout", "-b", branch);
    }

    public void fetch(FetchOption... options) throws ProcessFailedException {
        List<String> args = new ArrayList<>(List.of("fetch"));
        for (FetchOption option : options) {
            args.add(
                    switch (option) {
                        case PRUNE -> "-p";
                        case PRUNE_TAGS -> "-P";
                        case TAGS -> "-t";
                    });
        }
        processHelper.run(args);
    }

    public void pull() throws ProcessFailedException {
        processHelper.run("pull");
    }

    public void subTreeAdd(String destination, File oldRepoRoot, String oldRepoBranch) throws ProcessFailedException {
        processHelper.run("subtree", "add", "-P", destination, oldRepoRoot.getAbsolutePath(), oldRepoBranch);
    }

    public void addAll() throws ProcessFailedException {
        processHelper.run("add", "-A");
    }

    public void commit(String message) throws ProcessFailedException {
        Validate.notBlank(message);
        processHelper.run("commit", "-m", message);
    }

    public void push(PushOption... pushOptions) throws ProcessFailedException {
        List<String> args = new ArrayList<>(List.of("push"));
        for (PushOption pushOption : pushOptions) {
            args.add(
                    switch (pushOption) {
                        case FOLLOW_TAGS -> "--follow-tags";
                        case TAGS -> "--tags";
                    });
        }
        processHelper.run(args);
    }

    /**
     * Gets the most recent tag that starts with the given prefix.
     * The prefix is not stripped from the returned value.
     * @param prefix The prefix of the tag.
     * @return The most recent tag.
     */
    public Optional<Tag> getMostRecentTag(String prefix) throws ProcessFailedException {
        return getTags(prefix, false).findFirst();
    }

    public Stream<Tag> getTags(String prefix, boolean stripPrefix) throws ProcessFailedException {
        List<String> args = new ArrayList<>(List.of("tag", "-l"));
        boolean usePrefix = prefix != null && !prefix.isBlank();
        if (usePrefix) {
            if (prefix.endsWith("*")) {
                throw new IllegalArgumentException("prefix cannot end in *");
            }
            args.add(prefix + "*");
        }
        args.addAll(List.of("--sort=-version:refname", "--format=%(refname:strip=2),%(creatordate)"));
        String output = processHelper.run(args);
        return output.lines().map(line -> {
            String[] parts = line.split(",");
            String name = parts[0];
            if (usePrefix && stripPrefix && name.startsWith(prefix)) {
                name = name.substring(prefix.length());
            }
            String date = parts[1];
            return new Tag(name, date);
        });
    }

    public void add(String file) throws ProcessFailedException {
        Validate.notBlank(file);
        processHelper.run("add", file);
    }

    public boolean hasStagedChanges() throws ProcessFailedException {
        return !processHelper.tryRun("diff", "--cached", "--quiet");
    }

    public boolean hasNonStagedChanges() throws ProcessFailedException {
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
    public Stream<Commit> revList(String since, String path) throws ProcessFailedException {
        String commit = since == null ? null : since + "..HEAD";
        return processHelper
                .run(Stream.of("rev-list", "--all", "--format=%H|%as|%D|%s", commit, path)
                        .filter(Objects::nonNull)
                        .toList())
                .lines()
                .flatMap(line -> Commit.parse(line).stream());
    }

    public Stream<Commit> revList(Tag since, String path) throws ProcessFailedException {
        return revList(since.name(), path);
    }

    public Stream<Commit> revList(String path) throws ProcessFailedException {
        return revList((String) null, path);
    }

    public void init(String initialBranch, InitOption... initOptions) throws ProcessFailedException {
        List<String> args = new ArrayList<>(List.of("init", "-b", Validate.notBlank(initialBranch)));
        Arrays.stream(initOptions)
                .map(initOption -> switch (initOption) {
                    case BARE -> "--bare";
                })
                .forEach(args::add);
        processHelper.run(args);
    }

    public void initAndConfigureIdentity(String initialBranch, User user, InitOption... initOptions)
            throws ProcessFailedException {
        init(initialBranch, initOptions);
        configureIdentity(user);
    }

    public void tag(String tag, String message) throws ProcessFailedException {
        processHelper.run("tag", "-m", message, tag);
    }

    public void config(String key, String value) throws ProcessFailedException {
        processHelper.run("config", Validate.notBlank(key), value);
    }

    public void configureIdentity(User user) throws ProcessFailedException {
        Objects.requireNonNull(user);
        config(CONFIG_USER_NAME, user.name());
        config(CONFIG_USER_EMAIL, user.email());
    }

    public void ensureOnDefaultBranch() throws ProcessFailedException {
        String defaultBranch = getDefaultBranch();
        String currentBranch = getCurrentBranch();
        Validate.isTrue(
                defaultBranch.equals(currentBranch),
                "repo was not on default branch (expected %s, found %s)",
                defaultBranch,
                currentBranch);
    }

    public void clone(String url) throws ProcessFailedException {
        processHelper.run("clone", Validate.notBlank(url), ".");
    }

    public void symbolicRef(String name, String ref) throws ProcessFailedException {
        processHelper.run("symbolic-ref", Validate.notBlank(name), Validate.notBlank(ref));
    }

    public Stream<String> lsFiles(LsFilesOption... options) throws ProcessFailedException {
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
