package com.github.ngeor.yak4jcli;

import picocli.CommandLine;

/**
 * Main class.
 */
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@CommandLine.Command(subcommands = {
    NewProjectCommand.class,
    CommandLine.HelpCommand.class
})
public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
