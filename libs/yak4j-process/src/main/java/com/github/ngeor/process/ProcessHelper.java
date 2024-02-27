package com.github.ngeor.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ProcessHelper {
    private final File workingDirectory;
    private final Collection<String> baseCommand;

    public ProcessHelper(File workingDirectory, String... baseCommand) {
        this(workingDirectory, Arrays.asList(baseCommand));
    }

    public ProcessHelper(File workingDirectory, Collection<String> baseCommand) {
        this.workingDirectory = Objects.requireNonNull(workingDirectory);
        this.baseCommand = Objects.requireNonNull(baseCommand);
    }

    public String run(String... args) throws ProcessFailedException {
        return doRun(createProcessBuilder(args));
    }

    public String run(Collection<String> args) throws ProcessFailedException {
        return doRun(createProcessBuilder(args));
    }

    private String doRun(ProcessBuilderWithArgs processBuilderWithArgs) throws ProcessFailedException {
        try {
            Process process = processBuilderWithArgs
                    .processBuilder()
                    .redirectErrorStream(true)
                    .start();
            String output = new String(process.getInputStream().readAllBytes());
            waitForAndCheckStatus(process, processBuilderWithArgs.commandLine(), output);
            return output;
        } catch (IOException ex) {
            throw new ProcessFailedException(ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ProcessFailedException(ex);
        }
    }

    public void runInheritIO(String... args) throws ProcessFailedException {
        doRunInheritIO(createProcessBuilder(args));
    }

    public void runInheritIO(Collection<String> args) throws ProcessFailedException {
        doRunInheritIO(createProcessBuilder(args));
    }

    private void doRunInheritIO(ProcessBuilderWithArgs processBuilderWithArgs) throws ProcessFailedException {
        try {
            Process process =
                    processBuilderWithArgs.processBuilder().inheritIO().start();
            waitForAndCheckStatus(process, processBuilderWithArgs.commandLine());
        } catch (IOException ex) {
            throw new ProcessFailedException(ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ProcessFailedException(ex);
        }
    }

    private void waitForAndCheckStatus(Process process, String commandLine)
            throws InterruptedException, ProcessFailedException {
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new ProcessFailedException(String.format("Error running %s in %s", commandLine, workingDirectory));
        }
    }

    private void waitForAndCheckStatus(Process process, String commandLine, String output)
            throws InterruptedException, ProcessFailedException {
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new ProcessFailedException(
                    String.format("Error running %s in %s: %s", commandLine, workingDirectory, output));
        }
    }

    public boolean tryRun(String... args) throws ProcessFailedException {
        return doTryRun(createProcessBuilder(args));
    }

    public boolean tryRun(Collection<String> args) throws ProcessFailedException {
        return doTryRun(createProcessBuilder(args));
    }

    private boolean doTryRun(ProcessBuilderWithArgs processBuilderWithArgs) throws ProcessFailedException {
        try {
            Process process = processBuilderWithArgs
                    .processBuilder()
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

    protected ProcessBuilderWithArgs createProcessBuilder(String... args) {
        Objects.requireNonNull(args);
        return createProcessBuilder(Arrays.asList(args));
    }

    protected ProcessBuilderWithArgs createProcessBuilder(Collection<String> args) {
        Objects.requireNonNull(args);
        List<String> completeArgs = new ArrayList<>(baseCommand.size() + args.size());
        completeArgs.addAll(baseCommand);
        completeArgs.addAll(args);
        return new ProcessBuilderWithArgs(createProcessBuilderWithCompletedArgs(completeArgs), completeArgs);
    }

    private ProcessBuilder createProcessBuilderWithCompletedArgs(List<String> args) {
        return new ProcessBuilder(args).directory(workingDirectory);
    }
}
