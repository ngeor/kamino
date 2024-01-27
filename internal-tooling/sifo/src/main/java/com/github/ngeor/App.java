package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 */
public final class App {
    private App() {}

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws IOException {
        File root = new File("../../");
        if (!root.toPath().resolve(".github").toFile().isDirectory()) {
            throw new IllegalStateException("Could not find .github folder");
        }

        for (File typeLevel : root.listFiles(pathname -> pathname.isDirectory()
                && !pathname.isHidden()
                && !pathname.getName().startsWith("."))) {
            for (File projectLevel : typeLevel.listFiles(pathname -> pathname.isDirectory())) {
                File pomFile = new File(projectLevel, "pom.xml");
                if (pomFile.isFile()) {
                    System.out.println(projectLevel);
                    String javaVersion = Objects.requireNonNullElse(calculateJavaVersion(pomFile), "11");
                    String template = new String(
                                    App.class
                                            .getResourceAsStream("/build-template.yml")
                                            .readAllBytes(),
                                    StandardCharsets.UTF_8)
                            .replaceAll("\\$name", projectLevel.getName())
                            .replaceAll("\\$group", typeLevel.getName())
                            .replaceAll("\\$path", typeLevel.getName() + "/" + projectLevel.getName())
                            .replaceAll("\\$javaVersion", javaVersion);
                    Files.writeString(
                            root.toPath()
                                    .resolve(".github")
                                    .resolve("workflows")
                                    .resolve("build-" + typeLevel.getName() + "-" + projectLevel.getName() + ".yml"),
                            template);
                }
            }
        }
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
