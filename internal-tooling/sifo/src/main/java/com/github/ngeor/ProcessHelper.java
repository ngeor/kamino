package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessHelper {
    private final File workingDirectory;
    private final String command;

    public ProcessHelper(File workingDirectory, String command) {
        this.workingDirectory = workingDirectory;
        this.command = command;
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
        Process process =
            new ProcessBuilder(command).directory(workingDirectory).inheritIO().start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String commandAsString = String.join(" ", command);
            throw new IllegalStateException(
                String.format("Error running %s in %s", commandAsString, workingDirectory));
        }
    }

    private List<String> createArgs(String... args) {
        List<String> result = new ArrayList<>(1 + args.length);
        result.add(command);
        result.addAll(Arrays.asList(args));
        return result;
    }
}
