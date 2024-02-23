package com.github.ngeor.changelog;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.git.Git;
import com.github.ngeor.process.ProcessFailedException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ChangeLogUpdaterIT {
    @TempDir
    private File rootDirectory;

    private Git git;
    private ChangeLogUpdater changeLogUpdater;

    @BeforeEach
    void beforeEach() throws IOException, ProcessFailedException, InterruptedException {
        git = new Git(rootDirectory);
        git.initAndConfigureIdentity("John Doe", "no-reply@acme.com");
        changeLogUpdater = new ChangeLogUpdater(rootDirectory, null);
    }

    @Test
    void testWithoutExistingChangelog() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("Initial commit", "fix: Adjusted readme", "feat: New version");
        git.tag("v1.0");
        addCommits("chore: Adding homepage");

        // act
        changeLogUpdater.updateChangeLog();

        // assert
        assertThat(readChangeLog())
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
        changeLogUpdater.updateChangeLog();

        // assert
        assertThat(readChangeLog())
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
        changeLogUpdater.updateChangeLog();

        // assert
        assertThat(readChangeLog())
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
        changeLogUpdater.updateChangeLog();

        // assert
        assertThat(readChangeLog())
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
    void testDoNotGenerateUnreleasedSectionIfOnlyIgnoredCommits()
            throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        git.tag("v1.0");
        addCommits("[maven-release-plugin]: prepare for next development iteration");

        // act
        changeLogUpdater.updateChangeLog();

        // assert
        assertThat(readChangeLog())
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

        writeChangeLog("""
        # My changelog

        ## Unreleased

        * Whatever
        """);

        // act
        changeLogUpdater.updateChangeLog();

        // assert
        assertThat(readChangeLog())
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
    void testGenerateChangelogTwiceWithoutTags() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        changeLogUpdater.updateChangeLog();
        String contents = readChangeLog();

        // act
        changeLogUpdater.updateChangeLog();

        // assert
        String contents2 = readChangeLog();
        assertThat(contents2).isEqualToNormalizingNewlines(contents);
    }

    @Test
    void testGenerateChangelogTwiceWithTagAtLatest() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        git.tag("v1.0");
        changeLogUpdater.updateChangeLog();
        String contents = readChangeLog();

        // act
        changeLogUpdater.updateChangeLog();

        // assert
        String contents2 = readChangeLog();
        assertThat(contents2).isEqualToNormalizingNewlines(contents);
    }

    @Test
    void testGenerateChangelogTwiceWithTagAndUnreleasedChanges()
            throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        git.tag("v1.0");
        addCommits("fix: Important hotfix");
        changeLogUpdater.updateChangeLog();
        String contents = readChangeLog();

        // act
        changeLogUpdater.updateChangeLog();

        // assert
        String contents2 = readChangeLog();
        assertThat(contents2).isEqualToNormalizingNewlines(contents);
    }

    @Test
    void testTagOnIgnoredCommit() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "[maven-release-plugin]: release 1.0");
        git.tag("v1.0");

        // act
        changeLogUpdater.updateChangeLog();

        // assert
        assertThat(readChangeLog())
                .isEqualToNormalizingNewlines(
                        """
                # Changelog

                ## [1.0] - $now

                ### Miscellaneous Tasks

                * Added something
                """
                                .replace("$now", LocalDate.now().toString()));
    }

    @Test
    void testOverwrite() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "[maven-release-plugin]: release 1.0");
        git.tag("v1.0");
        changeLogUpdater.updateChangeLog();
        writeChangeLog(readChangeLog().replace("something", "oops"));

        // act
        changeLogUpdater.updateChangeLog(true);

        // assert
        assertThat(readChangeLog())
                .isEqualToNormalizingNewlines(
                        """
                # Changelog

                ## [1.0] - $now

                ### Miscellaneous Tasks

                * Added something
                """
                                .replace("$now", LocalDate.now().toString()));
    }

    @Test
    void testDoNotOverwrite() throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "[maven-release-plugin]: release 1.0");
        git.tag("v1.0");
        changeLogUpdater.updateChangeLog();
        writeChangeLog(readChangeLog().replace("something", "oops"));

        // act
        changeLogUpdater.updateChangeLog();

        // assert
        assertThat(readChangeLog())
                .isEqualToNormalizingNewlines(
                        """
                # Changelog

                ## [1.0] - $now

                ### Miscellaneous Tasks

                * Added oops
                """
                                .replace("$now", LocalDate.now().toString()));
    }

    @Test
    void testDoNotOverwriteUnreleasedAlwaysGetsOverwritten()
            throws IOException, ProcessFailedException, InterruptedException {
        // arrange
        addCommits("chore: Added something", "[maven-release-plugin]: release 1.0");
        git.tag("v1.0");
        addCommits("fix: Various fixes");
        changeLogUpdater.updateChangeLog();
        writeChangeLog(readChangeLog().replace("something", "oops").replace("Various", "Hilarious"));

        // act
        changeLogUpdater.updateChangeLog();

        // assert
        assertThat(readChangeLog())
                .isEqualToNormalizingNewlines(
                        """
                # Changelog

                ## Unreleased

                ### Fixes

                * Various fixes

                ## [1.0] - $now

                ### Miscellaneous Tasks

                * Added oops
                """
                                .replace("$now", LocalDate.now().toString()));
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

    private String readChangeLog() throws IOException {
        return Files.readString(changeLogPath());
    }

    private void writeChangeLog(String contents) throws IOException {
        Files.writeString(changeLogPath(), contents);
    }

    private Path changeLogPath() {
        return rootDirectory.toPath().resolve("CHANGELOG.md");
    }
}
