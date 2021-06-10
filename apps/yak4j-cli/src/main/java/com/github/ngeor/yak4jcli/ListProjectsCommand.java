package com.github.ngeor.yak4jcli;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import picocli.CommandLine;

/**
 * Lists all projects inside the repo.
 */
@CommandLine.Command(name = "list", description = "Lists all projects inside the repo")
public class ListProjectsCommand implements Runnable {
    @Override
    public void run() {
        listModules(Paths.get("."));
    }

    private void listModules(Path rootPath) {
        File rootPomFile = rootPath.resolve("pom.xml").toFile();
        PomDocument pomDocument = PomDocument.parse(rootPomFile);
        String groupId = pomDocument.getGroupId();
        String artifactId = pomDocument.getArtifactId();
        String version = pomDocument.getVersion();
        // TODO if a version component is missing, resolve effective pom
        String mavenCoordinates = String.format(
            "%s:%s:%s", groupId, artifactId, version);
        System.out.printf("%s\t%s%n", rootPath, mavenCoordinates);
        pomDocument.getModules().forEach(moduleElement -> {
            String moduleName = moduleElement.getTextContent();
            Path childPath = rootPath.resolve(moduleName);
            listModules(childPath);
        });
    }
}
