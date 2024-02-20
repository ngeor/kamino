package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChangeLogUpdaterIT {
    private File rootDirectory;
    private Git git;
    private ChangeLogUpdater changeLogUpdater;

    @BeforeEach
    void beforeEach() throws IOException, ProcessFailedException, InterruptedException {
        rootDirectory = Files.createTempDirectory("test").toFile();
        git = new Git(rootDirectory);
        git.init();
        git.config("user.name", "John Doe");
        git.config("user.email", "no-reply@acme.com");
        changeLogUpdater = new ChangeLogUpdater(rootDirectory, null, "v", git);
    }

    @AfterEach
    void afterEach() throws IOException {
        FileUtils.deleteDirectory(rootDirectory);
    }

    @Test
    void testWithoutExistingChangelog() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("Initial commit", "fix: Adjusted readme", "feat: New version");
        git.tag("v1.0");
        addCommits("chore: Adding homepage");

        // act
        changeLogUpdater.updateChangeLog(null);

        // assert
        String contents = Files.readString(rootDirectory.toPath().resolve("CHANGELOG.md"));
        assertThat(contents)
                .isEqualToNormalizingNewlines(
                        """
            # Changelog

            ## Unreleased

            ### Miscellaneous Tasks

            * Adding homepage

            ## [1.0] - $now

            ### Features

            * New version

            ### Fixes

            * Adjusted readme

            ### Miscellaneous Tasks

            * Initial commit
            """
                                .replace("$now", LocalDate.now().toString()));
    }

    @Test
    void testBreakingChanges() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("Initial commit", "fix!: Adjusted readme");

        // act
        changeLogUpdater.updateChangeLog(null);

        // assert
        String contents = Files.readString(rootDirectory.toPath().resolve("CHANGELOG.md"));
        assertThat(contents)
                .isEqualToNormalizingNewlines(
                        """
                # Changelog

                ## Unreleased

                ### Fixes

                * **Breaking**: Adjusted readme

                ### Miscellaneous Tasks

                * Initial commit
                """);
    }

    @Test
    void testDepsScope() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito", "chore(deps): Upgraded jUnit");

        // act
        changeLogUpdater.updateChangeLog(null);

        // assert
        String contents = Files.readString(rootDirectory.toPath().resolve("CHANGELOG.md"));
        assertThat(contents)
                .isEqualToNormalizingNewlines(
                        """
        # Changelog

        ## Unreleased

        ### Miscellaneous Tasks

        * Added something

        ### Dependencies

        * Upgraded mockito
        * Upgraded jUnit
        """);
    }

    @Test
    void testDoNotGenerateUnreleasedSectionIfEmpty() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        git.tag("v1.0");

        // act
        changeLogUpdater.updateChangeLog(null);

        // assert
        String contents = Files.readString(rootDirectory.toPath().resolve("CHANGELOG.md"));
        assertThat(contents)
                .isEqualToNormalizingNewlines(
                        """
            # Changelog

            ## [1.0] - $now

            ### Miscellaneous Tasks

            * Added something

            ### Dependencies

            * Upgraded mockito
            """
                                .replace("$now", LocalDate.now().toString()));
    }

    @Test
    void testDoNotGenerateUnreleasedSectionIfEmptyWithExistingChangeLog()
            throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        git.tag("v1.0");

        Files.writeString(
                rootDirectory.toPath().resolve("CHANGELOG.md"),
                """
        # My changelog

        ## Unreleased

        * Whatever
        """);

        // act
        changeLogUpdater.updateChangeLog(null);

        // assert
        String contents = Files.readString(rootDirectory.toPath().resolve("CHANGELOG.md"));
        assertThat(contents)
                .isEqualToNormalizingNewlines(
                        """
            # My changelog

            ## [1.0] - $now

            ### Miscellaneous Tasks

            * Added something

            ### Dependencies

            * Upgraded mockito
            """
                                .replace("$now", LocalDate.now().toString()));
    }

    @Test
    void testGenerateChangelogTwiceWithoutTags()
        throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        changeLogUpdater.updateChangeLog(null);
        String contents = Files.readString(rootDirectory.toPath().resolve("CHANGELOG.md"));

        // act
        changeLogUpdater.updateChangeLog(null);

        // assert
        String contents2 = Files.readString(rootDirectory.toPath().resolve("CHANGELOG.md"));
        assertThat(contents2).isEqualToNormalizingNewlines(contents);
    }

    @Test
    void testGenerateChangelogTwiceWithTagAtLatest()
        throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        git.tag("v1.0");
        changeLogUpdater.updateChangeLog(null);
        String contents = Files.readString(rootDirectory.toPath().resolve("CHANGELOG.md"));

        // act
        changeLogUpdater.updateChangeLog(null);

        // assert
        String contents2 = Files.readString(rootDirectory.toPath().resolve("CHANGELOG.md"));
        assertThat(contents2).isEqualToNormalizingNewlines(contents);
    }

    @Test
    void testGenerateChangelogTwiceWithTagAndUnreleasedChanges()
        throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        git.tag("v1.0");
        addCommits("fix: Important hotfix");
        changeLogUpdater.updateChangeLog(null);
        String contents = Files.readString(rootDirectory.toPath().resolve("CHANGELOG.md"));

        // act
        changeLogUpdater.updateChangeLog(null);

        // assert
        String contents2 = Files.readString(rootDirectory.toPath().resolve("CHANGELOG.md"));
        assertThat(contents2).isEqualToNormalizingNewlines(contents);
    }

    private void addCommits(String... subjects) throws IOException, ProcessFailedException, InterruptedException {
        for (String subject : subjects) {
            addCommit(subject);
        }
    }

    private void addCommit(String subject) throws IOException, ProcessFailedException, InterruptedException {
        Files.writeString(rootDirectory.toPath().resolve("README.md"), subject);
        git.addAll();
        git.commit(subject);
    }
}
