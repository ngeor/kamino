package com.github.ngeor;

import com.github.ngeor.versions.SemVer;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Changelog and semantic version calculator.
 */
public final class App {
    private final File rootDirectory;
    private final String path;
    private final Git git;

    App(File rootDirectory, String path, Git git) {
        this.rootDirectory = rootDirectory;
        this.path = path;
        this.git = git;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ProcessFailedException {
        ArgumentParser parser = new ArgumentParser();
        parser.addPositionalArgument("path", false, "The path of the module. If not provided, the command will run for all modules, if applicable.");
        parser.addPositionalArgument("version", false, "The version after which changelog should be generated.");
        parser.addFlagArgument("git-version", "Evaluate the next release version based on git history.");
        parser.addFlagArgument("release", "Release the given module.");
        parser.addFlagArgument("changelog", "Update the changelog files.");
        parser.addFlagArgument("push", "Should the release operation push git changes upstream or not.");
        parser.addNamedArgument("initial-version", false, "If releasing a previously unreleased module, use this as the first release version.");
        parser.addFlagArgument("help", "Prints help for the possible flags and exits.");

        Map<String, Object> parsedArgs = parser.parse(args);
        if (parsedArgs.containsKey("help")) {
            parser.printHelp();
            return;
        }
        // e.g. libs/java
        // ensure path does not end in slashes and is not blank
        String path = sanitize((String) parsedArgs.get("path"));
        File rootDirectory = new File(".").toPath().toAbsolutePath().toFile();
        Git git = new Git(rootDirectory);
        if (path != null) {

            // ensure given path exists
            if (!new File(rootDirectory, path).isDirectory()) {
                throw new IllegalArgumentException("path " + path + " not found");
            }
        }

        // e.g. 4.2.1
        String version = (String) parsedArgs.get("version");

        if (parsedArgs.containsKey("git-version")) {
            new GitVersionCommand(rootDirectory, path).run();
            return;
        }

        if (parsedArgs.containsKey("changelog")) {
            new ChangeLogUpdaterCommand(rootDirectory, path, version).run();
            return;
        }

        if (parsedArgs.containsKey("release")) {
            if (path != null) {
                App app = new App(rootDirectory, path, git);
                app.release(parsedArgs.containsKey("push"), (String) parsedArgs.get("initial-version"));
                return;
            } else {
                throw new IllegalStateException("path is required for --release command");
            }
        }

        if (path != null) {
            // default with path == changelog
            new ChangeLogUpdaterCommand(rootDirectory, path, version).run();
        } else {
            // default without path == overview
            new ChangesOverviewCommand().run();
        }
    }

    private void release(boolean push, String initialVersion)
            throws IOException, InterruptedException, ProcessFailedException {
        SemVer nextVersion = new GitVersionCalculator(git, path)
                .calculateGitVersion()
                .map(GitVersionCalculator.Result::nextVersion)
                .or(() -> Optional.ofNullable(initialVersion).map(SemVer::parse))
                .orElseThrow();
        new MavenReleaser(rootDirectory, path).prepareRelease(nextVersion, push);
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
}
