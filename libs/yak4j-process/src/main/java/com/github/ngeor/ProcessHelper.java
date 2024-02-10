package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessHelper {
    private final File workingDirectory;
    private final List<String> baseCommand;

    public ProcessHelper(File workingDirectory, String... baseCommand) {
        this.workingDirectory = workingDirectory;
        this.baseCommand = Arrays.asList(baseCommand);
    }

    // TODO micro-optimisation: do not return the entire String but a stream (with a completed future approach)
    public String run(String... args) throws IOException, InterruptedException {
        List<String> command = createArgs(args);
        Process process =
                new ProcessBuilder(command).directory(workingDirectory).start();
        int exitCode = process.waitFor();
        String output = new String(process.getInputStream().readAllBytes());
        if (exitCode != 0) {
            String commandAsString = String.join(" ", command);
            String error = new String(process.getErrorStream().readAllBytes());
            throw new IllegalStateException(
                    String.format("Error running %s in %s: %s %s", commandAsString, workingDirectory, output, error));
        }
        return output;
    }

    public void runInheritIO(String... args) throws IOException, InterruptedException {
        List<String> command = createArgs(args);
        Process process = new ProcessBuilder(command)
                .directory(workingDirectory)
                .inheritIO()
                .start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String commandAsString = String.join(" ", command);
            throw new IllegalStateException(String.format("Error running %s in %s", commandAsString, workingDirectory));
        }
    }

    public boolean tryRun(String... args) throws IOException, InterruptedException {
        List<String> command = createArgs(args);
        Process process =
                new ProcessBuilder(command).directory(workingDirectory).start();
        int exitCode = process.waitFor();
        return exitCode == 0;
    }

    private List<String> createArgs(String... args) {
        List<String> result = new ArrayList<>(baseCommand.size() + args.length);
        result.addAll(baseCommand);
        result.addAll(Arrays.asList(args));
        return result;
    }
}
