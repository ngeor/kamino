package com.github.ngeor;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@Command(name = "krt", description = "kamino release tool")
public final class App implements Callable<Integer> {
    @Parameters(description = "The version to release")
    private String version;

    @Option(
        names = {"-t", "--type"},
        description = "The package type to use. Valid values: ${COMPLETION-CANDIDATES}",
        required = true)
    private ProjectType type;

    @SuppressWarnings("FieldMayBeFinal")
    @Option(
        names = "--no-fail-on-pending-changes",
        negatable = true,
        description = "Check for pending changes")
    private boolean failOnPendingChanges = true;

    @SuppressWarnings("FieldMayBeFinal")
    @Option(
        names = "--no-push",
        negatable = true,
        description = "Push to the git remote"
    )
    private boolean push = true;

    public App() {
    }

    public String getVersion() {
        return version;
    }

    public ProjectType getType() {
        return type;
    }

    public boolean isFailOnPendingChanges() {
        return failOnPendingChanges;
    }

    public boolean isPush() {
        return push;
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new App())
            .setCaseInsensitiveEnumValuesAllowed(true)
            .execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        DirContext dirContext = DirContext.build();
        Git git = new Git(dirContext.getRepoDir().toFile());
        validateGitPreconditions(git);
        fetchAndPull(git);

        GitTagPrefix gitTagPrefix = new GitTagPrefix(dirContext);
        GitTagProvider gitTagProvider = new GitTagProvider(git, gitTagPrefix);
        VersionResolver versionResolver = new VersionResolver(gitTagProvider);

        SemVer resolved = versionResolver.resolve(version);
        version = resolved.toString();

        VersionSetter versionSetter = createVersionSetter(dirContext.getCurrentDir());
        versionSetter.bumpVersion(version);
        updateChangelog(dirContext, git);

        GitCommitMessageProvider gitCommitMessageProvider = new GitCommitMessageProvider();
        git.commit(gitCommitMessageProvider.getMessage(dirContext, version));

        GitTagMessageProvider gitTagMessageProvider = new GitTagMessageProvider(
            dirContext, gitTagPrefix, version
        );
        git.tag(gitTagMessageProvider.getMessage(), gitTagMessageProvider.getTag());
        doGitPush(git);

        SemVer snapshotVersion = resolved.bump(SemVerBump.MINOR).preRelease("SNAPSHOT");
        versionSetter.bumpVersion(snapshotVersion.toString());
        git.commit("chore(release): setting snapshot version for next development iteration");
        doGitPush(git);

        return 0;
    }

    private void validateGitPreconditions(Git git) throws IOException, InterruptedException {
        EnsureOnDefaultBranchRule ensureOnDefaultBranchRule = new EnsureOnDefaultBranchRule(git);
        ensureOnDefaultBranchRule.validate();
        if (failOnPendingChanges) {
            EnsureNoPendingChangesRule ensureNoPendingChangesRule = new EnsureNoPendingChangesRule(git);
            ensureNoPendingChangesRule.validate();
        }
    }

    private void fetchAndPull(Git git) throws IOException, InterruptedException {
        git.fetch();
        git.pull();
    }

    private VersionSetter createVersionSetter(Path currentDir) {
        VersionSetter versionSetter;
        switch (type) {
            case MAVEN:
                versionSetter = new MavenVersionSetter(currentDir);
                break;
            case NPM:
                versionSetter = new NpmVersionSetter();
                break;
            case PIP:
                versionSetter = new PipVersionSetter(currentDir);
                break;
            default:
                throw new IllegalArgumentException("Unsupported project type");
        }
        return versionSetter;
    }

    private void updateChangelog(DirContext dirContext, Git git) throws IOException, InterruptedException {
        GitCliff gitCliff = new GitCliff();
        gitCliff.run(dirContext, version);
        git.add(dirContext.resolveRelative("CHANGELOG.md"));
    }

    private void doGitPush(Git git) throws IOException, InterruptedException {
        if (push) {
            git.push();
        }
    }

    private static String ensureSemVerRelease(String input) {
        SemVer semVer = SemVer.parse(input);
        if (semVer.isPreRelease()) {
            throw new IllegalArgumentException("Version " + input + " is not allowed to be pre-release");
        }
        return semVer.toString();
    }

    private static String ensureSemVerPreRelease(String input) {
        SemVer semVer = SemVer.parse(input);
        if (!semVer.isPreRelease()) {
            throw new IllegalArgumentException("Version " + input + " is not allowed to be a release");
        }
        return semVer.toString();
    }
}
