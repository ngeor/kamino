package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 */
public final class App {
    private final StringTemplate buildTemplate = StringTemplate.ofResource("/build-template.yml");
    private final StringTemplate releaseTemplate = StringTemplate.ofResource("/release-template.yml");
    private final File root;

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws IOException {
        new App(new File("../../")).run();
    }

    private App(File root) throws IOException {
        if (!root.toPath().resolve(".github").toFile().isDirectory()) {
            throw new IllegalStateException("Could not find .github folder");
        }

        this.root = root;
    }

    private void run() throws IOException {
        for (File typeLevel : getDirectories(root)) {
            for (File projectLevel : getDirectories(typeLevel)) {
                File pomFile = new File(projectLevel, "pom.xml");
                if (pomFile.isFile()) {
                    processProject(typeLevel, projectLevel, pomFile);
                }
            }
        }
    }

    private void processProject(File typeLevel, File projectLevel, File pomFile) throws IOException {
        System.out.println(projectLevel);
        String javaVersion = Objects.requireNonNullElse(calculateJavaVersion(pomFile), "11");
        Map<String, String> variables = Map.of(
                "name",
                projectLevel.getName(),
                "group",
                typeLevel.getName(),
                "path",
                typeLevel.getName() + "/" + projectLevel.getName(),
                "javaVersion",
                javaVersion);

        Files.writeString(
                root.toPath()
                        .resolve(".github")
                        .resolve("workflows")
                        .resolve("build-" + typeLevel.getName() + "-" + projectLevel.getName() + ".yml"),
                buildTemplate.render(variables));

        if (Set.of("archetypes", "libs").contains(typeLevel.getName())) {
            Files.writeString(
                    root.toPath()
                            .resolve(".github")
                            .resolve("workflows")
                            .resolve("release-" + typeLevel.getName() + "-" + projectLevel.getName() + ".yml"),
                    releaseTemplate.render(variables));
        }
    }

    private static File[] getDirectories(File file) {
        return file.listFiles(new DirectoryFileFilter());
    }

    private static String calculateJavaVersion(File pomFile) throws IOException {
        // TODO use effective pom command instead
        Pattern pattern = Pattern.compile("<java.version>([0-9]+)</java.version>");
        for (String line : Files.readAllLines(pomFile.toPath())) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
}
