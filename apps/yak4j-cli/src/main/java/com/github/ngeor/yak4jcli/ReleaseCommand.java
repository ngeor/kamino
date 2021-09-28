package com.github.ngeor.yak4jcli;

import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import picocli.CommandLine;

/**
 * Performs the selective release of a subset of libraries.
 */
@CommandLine.Command(name = "release", description = "Selectively releases one or more libraries")
public class ReleaseCommand implements Callable<Integer> {
    @CommandLine.Parameters(description = "The module(s) to release", arity = "1..*")
    private List<String> modules;

    @Override
    public Integer call() throws Exception {
        // validate input
        validateModules(modules, "Error in input modules: ");

        // load parent pom
        PomDocument pomDocument = PomDocument.parse(new File("pom.xml"));
        List<String> existingModules = pomDocument.getModules()
            .map(ElementWrapper::getTextContent)
            .collect(Collectors.toList());

        // validate parent pom modules
        validateModules(existingModules, "Error in existing modules: ");

        // validate input modules are subset of parent pom modules
        validateSubSet(modules, existingModules);

        Git git = new Git();

        // validate on main branch (do not assume branch name)
        String defaultBranch = git.defaultBranch();
        String currentBranch = git.currentBranch();
        if (!defaultBranch.equals(currentBranch)) {
            throw new IllegalStateException(
                "You need to be on the " + defaultBranch + " branch, but you are on " + currentBranch);
        }

        // validate there aren't any pending changes
        String statusOutput = git.runKeepOutput("status", "--porcelain").trim();
        if (!statusOutput.isEmpty()) {
            throw new IllegalStateException("There are pending changes");
        }

        // run mvn clean + mvn release:clean
        Maven mvn = new Maven();
        // TODO cannot run clean because we're running ourselves during development
        mvn.runShowOutput("release:clean");

        // create release branch
        git.createBranch("release");

        // remove modules that aren't in the release

        // make a copy of pom.xml to restore the modules later
        // we could also keep them in-memory
        Files.copy(new File("pom.xml").toPath(), new File("pom.xml.bak").toPath());

        // TODO automatically include dependencies (recursively), e.g.
        // if releasing yak4j-dom, also release yak4j-cli

        pomDocument.setModules(modules);
        pomDocument.write();

        // sort pom to auto-format it
        mvn.runShowOutput("com.github.ekryd.sortpom:sortpom-maven-plugin:sort", "-Dsort.createBackupFile=false");

        git.runDiscardOutput("add", "-u", ".");
        git.commit("[bot] Removing modules to prepare for release");

        // prepare the release (will push the tag to remote which will trigger the selective release)
        mvn.runShowOutput("release:prepare");

        // read again the pom.xml because release:prepare has modified it
        PomDocument pomDocument2 = PomDocument.parse(new File("pom.xml"));

        // revert to original modules
        pomDocument2.setModules(existingModules);
        pomDocument2.write();

        // Update the parent pom version in all the child modules that were previously excluded
        String parentVersion = pomDocument2.getVersion();

        List<String> excludedModules = existingModules.stream()
            .filter(s -> !modules.contains(s)).collect(Collectors.toList());

        for (String excludedModule : excludedModules) {
            PomDocument childDocument = PomDocument.parse(new File(excludedModule + "/pom.xml"));
            childDocument.getParent().get().setVersion(parentVersion);
            childDocument.write();
        }

        // sort pom to auto-format it
        mvn.runShowOutput("com.github.ekryd.sortpom:sortpom-maven-plugin:sort", "-Dsort.createBackupFile=false");

        // add all affected pom.xml files to git index
        git.runDiscardOutput("add", "-u", ".");

        // commit
        git.commit("[bot] Restoring all modules");

        // merge the branch
        git.runDiscardOutput("checkout", defaultBranch);
        git.runDiscardOutput("merge", "release");
        git.runDiscardOutput("branch", "-D", "release");
        return 0;
    }

    private static void validateModules(Collection<String> modules, String errorPrefix) {
        if (modules == null) {
            throw new NullPointerException(errorPrefix + "null modules");
        }

        if (modules.isEmpty()) {
            throw new IllegalArgumentException(errorPrefix + "empty modules");
        }

        Set<String> modulesAsSet = new HashSet<>(modules);
        if (modules.size() != modulesAsSet.size()) {
            throw new IllegalArgumentException(errorPrefix + "duplicate modules");
        }

        if (!modules.stream().allMatch(ReleaseCommand::isModuleNameValid)) {
            throw new IllegalArgumentException(errorPrefix + "invalid module name");
        }
    }

    private static boolean isModuleNameValid(String name) {
        // non-null, non-empty, must not contain spaces
        return name != null && !name.isEmpty() && name.indexOf(' ') < 0;
    }

    private static void validateSubSet(Collection<String> modules, Collection<String> existingModules) {
        if (modules == null || modules.isEmpty()) {
            throw new IllegalArgumentException("modules");
        }

        if (existingModules == null || existingModules.isEmpty()) {
            throw new IllegalArgumentException("existingModules");
        }

        for (String module : modules) {
            if (!existingModules.contains(module)) {
                throw new IllegalArgumentException(
                    "Module " + module + " is not an existing module (check is case sensitive!)");
            }
        }
    }

    private static class ProcessRunner {
        private final String binary;

        ProcessRunner(String binary) {
            this.binary = binary;
        }

        void runDiscardOutput(String... args) throws IOException, InterruptedException {
            run(args);
        }

        String runKeepOutput(String... args) throws IOException, InterruptedException {
            Process process = run(args);
            StringBuilder result = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                while (line != null) {
                    result.append(line).append(System.lineSeparator());
                    line = reader.readLine();
                }
            }
            return result.toString();
        }

        private Process run(String... args) throws IOException, InterruptedException {
            String[] command = Stream.concat(Stream.of(binary), Stream.of(args)).toArray(String[]::new);
            Process process = new ProcessBuilder(command).start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Command " + String.join(" ", command) + " failed");
            }
            return process;
        }

        void runShowOutput(String... args) throws IOException, InterruptedException {
            String[] command = Stream.concat(Stream.of(binary), Stream.of(args)).toArray(String[]::new);
            Process process = new ProcessBuilder(command).inheritIO().start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Command " + String.join(" ", command) + " failed");
            }
        }
    }

    private static class Git extends ProcessRunner {
        Git() {
            super("git");
        }

        void createBranch(String branch) throws IOException, InterruptedException {
            runDiscardOutput("checkout", "-b", branch);
        }

        void commit(String msg) throws IOException, InterruptedException {
            runDiscardOutput("commit", "-m", msg);
        }

        String defaultBranch() throws IOException, InterruptedException {
            // TODO do not assume the remote name is origin
            // git symbolic-ref refs/remotes/origin/HEAD --> refs/remotes/origin/trunk
            String prefix = "refs/remotes/origin/";
            String output = runKeepOutput("symbolic-ref", prefix + "HEAD").trim();
            if (!output.startsWith(prefix)) {
                throw new IllegalStateException("Could not determine default branch from output: " + output);
            }
            String defaultBranch = output.substring(prefix.length());
            if (defaultBranch.isEmpty()) {
                throw new IllegalStateException("Could not determine default branch from output: " + output);
            }
            return defaultBranch;
        }

        String currentBranch() throws IOException, InterruptedException {
            return runKeepOutput("rev-parse",  "--abbrev-ref", "HEAD").trim();
        }
    }

    private static class Maven extends ProcessRunner {
        Maven() {
            // TODO support mvn for *nix
            super("mvn.cmd");
        }
    }
}
