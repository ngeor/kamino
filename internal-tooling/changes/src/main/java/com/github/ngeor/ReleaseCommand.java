package com.github.ngeor;

import com.github.ngeor.git.Git;
import com.github.ngeor.mr.MavenReleaser;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.versions.SemVer;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ReleaseCommand extends BaseCommand {
    private final File rootDirectory;
    private final String path;
    private final Git git;
    private final boolean push;
    private final String initialVersion;

    public ReleaseCommand(File rootDirectory, Map<String, Object> args) {
        super(rootDirectory, args);
        this.rootDirectory = rootDirectory;
        this.git = new Git(rootDirectory);
        this.path = Objects.requireNonNull((String) args.get("path"));
        this.push = args.containsKey("push");
        this.initialVersion = (String) args.get("initial-version");
    }

    @Override
    public void run() throws IOException, ProcessFailedException {
        SemVer nextVersion = new GitVersionCalculator(git, path)
                .calculateGitVersion()
                .map(GitVersionCalculator.Result::nextVersion)
                .or(() -> Optional.ofNullable(initialVersion).map(SemVer::parse))
                .orElseThrow();
        new MavenReleaser(rootDirectory, path).prepareRelease(nextVersion, push);
    }
}
