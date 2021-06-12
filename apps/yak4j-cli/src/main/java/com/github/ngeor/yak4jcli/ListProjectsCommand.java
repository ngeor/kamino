package com.github.ngeor.yak4jcli;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
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
        if (groupId == null || artifactId == null || version == null) {
            Optional<PomParentElement> optionalParent = pomDocument.getParent();
            if (optionalParent.isPresent()) {
                PomParentElement pomParentElement = optionalParent.get();
                String parentGroupId = pomParentElement.getGroupId();
                String parentVersion = pomParentElement.getVersion();
                groupId = StringUtils.defaultString(groupId, parentGroupId);
                version = StringUtils.defaultString(version, parentVersion);
            }
        }
        if (groupId == null || artifactId == null || version == null) {
            throw new IllegalStateException("Could not resolve coordinates for " + rootPomFile);
        }
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
