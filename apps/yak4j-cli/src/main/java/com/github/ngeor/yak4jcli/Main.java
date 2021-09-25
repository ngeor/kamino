package com.github.ngeor.yak4jcli;

import picocli.CommandLine;

/**
 * Main class.
 */
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@CommandLine.Command(subcommands = {
    NewProjectCommand.class,
    ListProjectsCommand.class,
    ReleaseCommand.class,
    CommandLine.HelpCommand.class
})
public class Main {
    /**
     * Main entrypoint for the program.
     */
    public static void main(String[] args) {
        //noinspection InstantiationOfUtilityClass
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
