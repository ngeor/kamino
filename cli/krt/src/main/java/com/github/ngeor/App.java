package com.github.ngeor;

import com.github.ngeor.versions.SemVer;
import com.github.ngeor.versions.SemVerBump;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "krt", description = "kamino release tool", versionProvider = ManifestVersionProvider.class)
public final class App implements Callable<Integer> {
    @Option(
            names = {"-V", "--version"},
            versionHelp = true,
            description = "Print version info and exit")
    private boolean versionRequested;

    @Parameters(description = "The version to release. Either explicit version or one of major, minor, patch.")
    private String version;

    @Option(
            names = {"-t", "--type"},
            description = "The package type to use. Valid values: ${COMPLETION-CANDIDATES}",
            required = true)
    private ProjectType type;

    @SuppressWarnings("FieldMayBeFinal")
    @Option(names = "--no-fail-on-pending-changes", negatable = true, description = "Check for pending changes")
    private boolean failOnPendingChanges = true;

    @SuppressWarnings("FieldMayBeFinal")
    @Option(names = "--no-push", negatable = true, description = "Push to the git remote")
    private boolean push = true;

    public App() {}

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
        GitImpl git = new GitImpl(dirContext.getRepoDir().toFile());
        validateGitPreconditions(git);
        fetchAndPull(git);

        GitTagPrefix gitTagPrefix = new GitTagPrefix(dirContext);
        GitTagProvider gitTagProvider = new GitTagProvider(git, gitTagPrefix);
        VersionResolver versionResolver = new VersionResolver(gitTagProvider);

        SemVer resolved = versionResolver.resolve(version);
        version = resolved.toString();

        VersionSetter versionSetter = createVersionSetter(dirContext.getCurrentDir());
        versionSetter.bumpVersion(version);
        updateChangelog(dirContext, git, gitTagPrefix.getPrefix());

        GitCommitMessageProvider gitCommitMessageProvider = new GitCommitMessageProvider();
        git.commit(gitCommitMessageProvider.getMessage(dirContext, version));

        GitTagMessageProvider gitTagMessageProvider = new GitTagMessageProvider(dirContext, gitTagPrefix, version);
        git.tag(gitTagMessageProvider.getMessage(), gitTagMessageProvider.getTag());
        doGitPush(git);

        SemVer snapshotVersion = resolved.bump(SemVerBump.MINOR).preRelease("SNAPSHOT");
        versionSetter.bumpVersion(snapshotVersion.toString());
        git.commit("chore(release): prepare for next development iteration");
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

    private void updateChangelog(DirContext dirContext, Git git, String tagPrefix)
            throws IOException, InterruptedException {
        String tagPattern = tagPrefix + "[0-9]*";
        Path cliffTomlPath = Files.createTempFile("cliff", ".toml");
        File cliffTomlFile = cliffTomlPath.toFile();
        cliffTomlFile.deleteOnExit();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/cliff.toml"))))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(cliffTomlFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.append(line);
                    writer.newLine();
                }

                writer.append("tag_pattern = \"").append(tagPattern).append('"');
                writer.newLine();
            }
        }
        GitCliff gitCliff = new GitCliff();
        gitCliff.run(dirContext, version, cliffTomlPath);
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
