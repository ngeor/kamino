package com.github.ngeor;

import com.github.ngeor.argparse.ArgSpecBuilder;
import com.github.ngeor.argparse.ArgumentParser;
import com.github.ngeor.argparse.SpecKind;
import java.io.File;
import java.util.Map;

/**
 * Changelog and semantic version calculator.
 */
public final class App {
    public static void main(String[] args) throws Exception {
        ArgumentParser parser = createArgumentParser();
        Map<String, Object> parsedArgs = parser.parse(args);
        if (parsedArgs.containsKey("help")) {
            parser.printHelp();
            return;
        }

        File rootDirectory = new File(".").toPath().toAbsolutePath().toFile();

        // e.g. libs/java
        String path = (String) parsedArgs.get("path");
        if (path != null) {
            // ensure given path exists
            if (!new File(rootDirectory, path).isDirectory()) {
                throw new IllegalArgumentException("path " + path + " not found");
            }
        }

        BaseCommand baseCommand =
                switch (determineCommand(parsedArgs)) {
                    case GIT_VERSION -> new GitVersionCommand(rootDirectory, parsedArgs);
                    case CHANGELOG -> new ChangeLogUpdaterCommand(rootDirectory, parsedArgs);
                    case RELEASE -> new ReleaseCommand(rootDirectory, parsedArgs);
                    case OVERVIEW -> new ChangesOverviewCommand(rootDirectory, parsedArgs);
                };
        baseCommand.run();
    }

    private static ArgumentParser createArgumentParser() {
        ArgumentParser parser = new ArgumentParser();
        parser.addFlagArgument("help", "Prints help for the possible flags and exits.");
        parser.add(new ArgSpecBuilder("path", SpecKind.POSITIONAL)
                .description(
                        "The path of the module. If not provided, the command will run for all modules, if applicable.")
                .normalizer(App::sanitizePath)
                .build());
        parser.addFlagArgument("git-version", "Evaluate the next release version based on git history.");
        parser.addFlagArgument("release", "Release the given module.");
        parser.addFlagArgument("changelog", "Update the changelog files.");
        parser.addFlagArgument("push", "Should the release operation push git changes upstream or not.");
        parser.addNamedArgument(
                "initial-version",
                false,
                "If releasing a previously unreleased module, use this as the first release version.");
        parser.addFlagArgument(
                "overwrite",
                "If specified, the full changelog will be re-generated, overwriting the existing matching entries. By default, only missing entries (and the unreleased) will be written.");
        return parser;
    }

    static String sanitizePath(String path) {
        if (path == null) {
            return null;
        }

        // ensure path does not end in slash
        while (path.endsWith("\\") || path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path.isBlank() ? null : path;
    }

    private static Command determineCommand(Map<String, Object> args) {
        if (args.containsKey("git-version")) {
            return Command.GIT_VERSION;
        }

        if (args.containsKey("release")) {
            return Command.RELEASE;
        }

        if (args.containsKey("changelog")) {
            return Command.CHANGELOG;
        }

        return args.containsKey("path") ? Command.CHANGELOG : Command.OVERVIEW;
    }

    private enum Command {
        OVERVIEW,
        CHANGELOG,
        RELEASE,
        GIT_VERSION
    }
}
