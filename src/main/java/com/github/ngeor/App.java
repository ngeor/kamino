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

    @Option(
        names = {"-s", "--snapshot"},
        description = "A snapshot version to use after the release")
    private String snapshotVersion;

    @Option(
        names = "--no-fail-on-pending-changes",
        negatable = true,
        description = "Check for pending changes")
    private boolean failOnPendingChanges = true;

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

    public String getSnapshotVersion() {
        return snapshotVersion;
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
        GitDirFinder gitDirFinder = new GitDirFinder();
        Path currentDir = Path.of(".").toAbsolutePath().normalize();
        Path projectDir = gitDirFinder.find(currentDir);
        if (projectDir == null) {
            throw new IllegalStateException("Could not detect git directory");
        }
        Git git = new Git(projectDir.toFile());
        EnsureOnDefaultBranchRule ensureOnDefaultBranchRule = new EnsureOnDefaultBranchRule(git);
        ensureOnDefaultBranchRule.validate();
        if (failOnPendingChanges) {
            EnsureNoPendingChangesRule ensureNoPendingChangesRule = new EnsureNoPendingChangesRule(git);
            ensureNoPendingChangesRule.validate();
        }
        git.fetch();
        git.pull();

        VersionSetter versionSetter = createVersionSetter(currentDir);
        versionSetter.bumpVersion(version);
        GitCliff gitCliff = new GitCliff();
        gitCliff.run(currentDir, projectDir, version);
        git.add("CHANGELOG.md");

        GitCommitMessageProvider gitCommitMessageProvider = new GitCommitMessageProvider();
        git.commit(gitCommitMessageProvider.getMessage(currentDir, projectDir, version));

        GitTagMessageProvider gitTagMessageProvider = new GitTagMessageProvider(
            currentDir, projectDir, version
        );
        git.tag(gitTagMessageProvider.getMessage(), gitTagMessageProvider.getTag());
        doGitPush(git);

        if (snapshotVersion != null && !snapshotVersion.isBlank()) {
            versionSetter.bumpVersion(snapshotVersion);
            git.commit("chore(release): setting snapshot version for next development iteration");
            doGitPush(git);
        }
        return 0;
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

    private void doGitPush(Git git) throws IOException, InterruptedException {
        if (push) {
            git.push();
        }
    }
}
