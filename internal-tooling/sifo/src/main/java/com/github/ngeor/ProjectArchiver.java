package com.github.ngeor;

import com.github.ngeor.git.Git;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

/**
 * Archives a project that is already imported into the monorepo.
 */
public class ProjectArchiver {
    private final File projectDirectory;
    private final String newLocation;
    private final String githubToken;

    public ProjectArchiver(File projectDirectory, String newLocation, String githubToken) {
        this.projectDirectory = projectDirectory;
        this.newLocation = newLocation;
        this.githubToken = githubToken;
    }

    public void run() throws IOException, InterruptedException, ProcessFailedException {
        addArchivalNoticeToReadmeFile();
        deleteLocalFolder();
        archiveProjectInGitHub();
    }

    private void addArchivalNoticeToReadmeFile() throws IOException, InterruptedException, ProcessFailedException {
        // add archival notice if it does not exist,
        // either after the badges or before the first second level header
        File readmeFile = new File(projectDirectory, "README.md");
        List<String> lines = new ArrayList<>(Files.readAllLines(readmeFile.toPath()));

        if (lines.stream().anyMatch(line -> line.startsWith("**Archived project"))) {
            return;
        }

        String template = String.format(
                """

        **Archived project!**
        This project is **archived** and will be **removed**.
        It has moved [here](%s).

        """,
                newLocation);

        int firstBadgeIndex = -1;
        int lastBadgeIndex = -1;
        int firstH2Index = -1;
        boolean stopLookingForBadges = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith("[![") && !stopLookingForBadges) {
                if (firstBadgeIndex == -1) {
                    firstBadgeIndex = i;
                }
                lastBadgeIndex = i;
            } else {
                if (firstBadgeIndex != -1) {
                    stopLookingForBadges = true;
                }
            }

            if (firstH2Index == -1 && line.startsWith("## ")) {
                firstH2Index = i;
            }
        }

        LinkedList<String> newLines = template.lines().collect(Collectors.toCollection(LinkedList::new));
        if (lastBadgeIndex != -1) {
            // insert after this index
            while (!newLines.isEmpty()) {
                lines.add(lastBadgeIndex + 1, newLines.removeLast());
            }
        } else if (firstH2Index != -1) {
            // insert before this index
            while (!newLines.isEmpty()) {
                lines.add(firstH2Index, newLines.removeLast());
            }
        } else {
            // insert at end of file
            while (!newLines.isEmpty()) {
                lines.add(newLines.removeFirst());
            }
        }

        // remove duplicate empty lines
        int i = 1;
        while (i < lines.size()) {
            if (lines.get(i - 1).isEmpty() && lines.get(i).isEmpty()) {
                lines.remove(i);
            } else {
                i++;
            }
        }

        Files.writeString(readmeFile.toPath(), String.join(System.lineSeparator(), lines) + System.lineSeparator());

        Git git = new Git(projectDirectory);
        git.add("README.md");
        git.commit("Added archival notice");
        git.push();
    }

    private void deleteLocalFolder() throws IOException {
        FileUtils.deleteDirectory(projectDirectory);
    }

    private void archiveProjectInGitHub() throws IOException, InterruptedException {
        // TODO when upgrading to Java 21, use try-with-resources
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder(
                        URI.create("https://api.github.com/repos/ngeor/" + projectDirectory.getName()))
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer " + githubToken)
                .header("X-GitHub-Api-Version", "2022-11-28")
                .method("PATCH", HttpRequest.BodyPublishers.ofString("{ \"archived\": true }"))
                .build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (httpResponse.statusCode() != 200) {
            throw new IllegalStateException("Could not archive repository: " + httpResponse.statusCode());
        }
    }
}
