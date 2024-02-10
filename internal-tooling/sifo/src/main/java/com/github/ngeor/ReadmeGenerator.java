package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ReadmeGenerator {
    public void fixProjectBadges(File projectDirectory, String groupId, String workflowId) throws IOException {
        File readmeFile = projectDirectory.toPath().resolve("README.md").toFile();
        List<String> lines;
        if (readmeFile.exists()) {
            lines = new ArrayList<>(Files.readAllLines(readmeFile.toPath()));
        } else {
            lines = new ArrayList<>(List.of("# " + projectDirectory.getName(), "", "[![Build"));
        }

        String artifactId = projectDirectory.getName();
        boolean foundBadges = false;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("[![")) {
                foundBadges = true;

                // TODO add missing badges
                if (line.startsWith("[![Maven Central")) {
                    line = "[![Maven Central](https://img.shields.io/maven-central/v/" + groupId + "/" + artifactId
                            + ".svg?label=Maven%20Central)](https://central.sonatype.com/artifact/" + groupId + "/"
                            + artifactId + "/overview)";
                    lines.set(i, line);
                } else if (line.startsWith("[![Java CI") || line.startsWith("[![Build")) {
                    String url = "https://github.com/ngeor/kamino/actions/workflows/build-" + workflowId + ".yml";
                    line = String.format("[![Build %s](%s/badge.svg)](%s)", projectDirectory.getName(), url, url);
                    lines.set(i, line);
                } else if (line.startsWith("[![javadoc")) {
                    String badgeUrl = String.format("https://javadoc.io/badge2/%s/%s/javadoc.svg", groupId, artifactId);
                    String url = String.format("https://javadoc.io/doc/%s/%s", groupId, artifactId);
                    line = String.format("[![javadoc](%s)](%s)", badgeUrl, url);
                    lines.set(i, line);
                }
            } else {
                if (foundBadges) {
                    break;
                }
            }
        }
        Files.writeString(readmeFile.toPath(), String.join(System.lineSeparator(), lines) + System.lineSeparator());
    }
}
