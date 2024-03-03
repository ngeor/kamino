package com.github.ngeor.git;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.github.ngeor.process.ProcessFailedException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GitIT {
    @TempDir
    private File directory;

    private Git git;

    @BeforeEach
    void beforeEach() throws ProcessFailedException {
        git = new Git(directory);
        git.initAndConfigureIdentity("main", new User("John Doe", "no-reply@acme.com"));
    }

    @Test
    void testHasStagedChanges() throws IOException, ProcessFailedException {
        assertThat(git.hasStagedChanges()).isFalse();
        Files.writeString(directory.toPath().resolve("README.md"), "My project");
        git.addAll();
        assertThat(git.hasStagedChanges()).isTrue();
        git.commit("Adding readme file");
        assertThat(git.hasStagedChanges()).isFalse();
    }

    @Test
    void testGetMostRecentTag() throws IOException, ProcessFailedException {
        Files.writeString(directory.toPath().resolve("README.md"), "My project");
        git.addAll();
        git.commit("Adding readme file");
        tag("v9.0.0");

        Files.writeString(directory.toPath().resolve("CHANGELOG.md"), "My changelog");
        git.addAll();
        git.commit("Adding changelog file");
        tag("v10.0.0");

        Tag tag = git.getMostRecentTag("v").orElseThrow();

        assertThat(tag.name()).isEqualTo("v10.0.0");
        assertThat(OffsetDateTime.parse(tag.date(), DateTimeFormatter.ofPattern("E MMM d HH:mm:ss yyyy Z", Locale.US)))
                .isCloseToUtcNow(within(2, ChronoUnit.SECONDS));
    }

    @Test
    void testGetRevList() throws IOException, ProcessFailedException {
        Files.writeString(directory.toPath().resolve("README.md"), "My project");
        git.addAll();
        git.commit("Adding readme file");
        tag("v9.0.0");

        Files.writeString(directory.toPath().resolve("CHANGELOG.md"), "My changelog");
        git.addAll();
        git.commit("Adding changelog file");
        tag("v10.0.0");

        assertThat(git.revList(".").map(Commit::summary))
                .containsExactly("Adding changelog file", "Adding readme file");
        assertThat(git.revList("v9.0.0", ".").map(Commit::summary)).containsExactly("Adding changelog file");
    }

    private void tag(String tag) throws ProcessFailedException {
        git.tag(tag, String.format("Releasing %s", tag));
    }
}
