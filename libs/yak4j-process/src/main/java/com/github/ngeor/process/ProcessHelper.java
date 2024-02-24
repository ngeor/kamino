package com.github.ngeor.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ProcessHelper {
    private final File workingDirectory;
    private final List<String> baseCommand;

    public ProcessHelper(File workingDirectory, String... baseCommand) {
        this.workingDirectory = workingDirectory;
        this.baseCommand = Arrays.asList(baseCommand);
    }

    public String run(String... args) throws ProcessFailedException {
        return doRun(createArgs(args));
    }

    public String run(Collection<String> args) throws ProcessFailedException {
        return doRun(createArgs(args));
    }

    private String doRun(List<String> command) throws ProcessFailedException {
        try {
            Process process = new ProcessBuilder(command)
                    .directory(workingDirectory)
                    .redirectErrorStream(true)
                    .start();
            String output = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                String commandAsString = String.join(" ", command);
                throw new ProcessFailedException(
                        String.format("Error running %s in %s: %s", commandAsString, workingDirectory, output));
            }
            return output;
        } catch (IOException ex) {
            throw new ProcessFailedException(ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ProcessFailedException(ex);
        }
    }

    public void runInheritIO(String... args) throws ProcessFailedException {
        doRunInheritIO(createArgs(args));
    }

    public void runInheritIO(Collection<String> args) throws ProcessFailedException {
        doRunInheritIO(createArgs(args));
    }

    private void doRunInheritIO(List<String> command) throws ProcessFailedException {
        try {
            Process process = new ProcessBuilder(command)
                    .directory(workingDirectory)
                    .inheritIO()
                    .start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                String commandAsString = String.join(" ", command);
                throw new ProcessFailedException(
                        String.format("Error running %s in %s", commandAsString, workingDirectory));
            }
        } catch (IOException ex) {
            throw new ProcessFailedException(ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ProcessFailedException(ex);
        }
    }

    public boolean tryRun(String... args) throws ProcessFailedException {
        return doTryRun(createArgs(args));
    }

    public boolean tryRun(Collection<String> args) throws ProcessFailedException {
        return doTryRun(createArgs(args));
    }

    private boolean doTryRun(List<String> command) throws ProcessFailedException {
        try {
            Process process = new ProcessBuilder(command)
                    .directory(workingDirectory)
                    .redirectErrorStream(true)
                    .start();
            process.getInputStream().readAllBytes();
            return process.waitFor() == 0;
        } catch (IOException ex) {
            throw new ProcessFailedException(ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ProcessFailedException(ex);
        }
    }

    private List<String> createArgs(String... args) {
        return createArgs(Arrays.asList(args));
    }

    private List<String> createArgs(Collection<String> args) {
        List<String> result = new ArrayList<>(baseCommand.size() + args.size());
        result.addAll(baseCommand);
        result.addAll(args);
        return result;
    }
}
