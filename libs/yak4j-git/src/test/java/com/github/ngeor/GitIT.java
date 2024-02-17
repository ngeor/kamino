package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GitIT {
    private File directory;
    private Git git;

    @BeforeEach
    void beforeEach() throws IOException, ProcessFailedException, InterruptedException {
        directory = Files.createTempDirectory("git").toFile();
        git = new Git(directory);
        git.init();
        git.config("user.name", "John Doe");
        git.config("user.email", "no-reply@acme.com");
    }

    @AfterEach
    void afterEach() throws IOException {
        FileUtils.deleteDirectory(directory);
    }

    @Test
    void testHasStagedChanges() throws IOException, InterruptedException, ProcessFailedException {
        assertThat(git.hasStagedChanges()).isFalse();
        Files.writeString(directory.toPath().resolve("README.md"), "My project");
        git.addAll();
        assertThat(git.hasStagedChanges()).isTrue();
        git.commit("Adding readme file");
        assertThat(git.hasStagedChanges()).isFalse();
    }

    @Test
    void testGetMostRecentTag() throws IOException, ProcessFailedException, InterruptedException {
        Files.writeString(directory.toPath().resolve("README.md"), "My project");
        git.addAll();
        git.commit("Adding readme file");
        git.tag("v1.0.0");

        Files.writeString(directory.toPath().resolve("CHANGELOG.md"), "My changelog");
        git.addAll();
        git.commit("Adding changelog file");
        git.tag("v1.1.0");

        assertThat(git.getMostRecentTag("v")).contains("1.1.0");
    }

    @Test
    void testGetMostRecentTagWithDate() throws IOException, ProcessFailedException, InterruptedException {
        Files.writeString(directory.toPath().resolve("README.md"), "My project");
        git.addAll();
        git.commit("Adding readme file");
        git.tag("v9.0.0");

        Files.writeString(directory.toPath().resolve("CHANGELOG.md"), "My changelog");
        git.addAll();
        git.commit("Adding changelog file");
        git.tag("v10.0.0");

        String[] parts = git.getMostRecentTagWithDate("v").orElseThrow();

        assertThat(parts[0]).isEqualTo("10.0.0");
        assertThat(OffsetDateTime.parse(parts[1], DateTimeFormatter.ofPattern("E MMM d HH:mm:ss yyyy Z", Locale.US)))
                .isCloseToUtcNow(within(2, ChronoUnit.SECONDS));
    }

    @Test
    void testGetRevList() throws IOException, ProcessFailedException, InterruptedException {
        Files.writeString(directory.toPath().resolve("README.md"), "My project");
        git.addAll();
        git.commit("Adding readme file");
        git.tag("v9.0.0");

        Files.writeString(directory.toPath().resolve("CHANGELOG.md"), "My changelog");
        git.addAll();
        git.commit("Adding changelog file");
        git.tag("v10.0.0");

        assertThat(git.revList(null, ".").map(Commit::summary))
                .containsExactly("Adding changelog file", "Adding readme file");
        assertThat(git.revList("v9.0.0", ".").map(Commit::summary)).containsExactly("Adding changelog file");
    }
}