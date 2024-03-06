package com.github.ngeor.changelog;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.changelog.format.FormatOptions;
import com.github.ngeor.git.Git;
import com.github.ngeor.git.User;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.versions.SemVer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ChangeLogUpdaterIT {
    @TempDir
    private File rootDirectory;

    private Git git;

    @BeforeEach
    void beforeEach() throws ProcessFailedException {
        git = new Git(rootDirectory);
        git.initAndConfigureIdentity("master", new User("John Doe", "no-reply@acme.com"));
    }

    private ChangeLogUpdater buildChangeLogUpdater(UnaryOperator<ImmutableOptions.Builder> customizer) {
        return new ChangeLogUpdater(customizer
                .apply(ImmutableOptions.builder()
                        .rootDirectory(rootDirectory)
                        .formatOptions(new FormatOptions(
                                "Unreleased",
                                Map.of(
                                        "fix",
                                        "Fixes",
                                        "chore",
                                        "Miscellaneous Tasks",
                                        "feat",
                                        "Features",
                                        "deps",
                                        "Dependencies"),
                                null)))
                .build());
    }

    private void updateChangeLog(UnaryOperator<ImmutableOptions.Builder> customizer)
            throws IOException, ProcessFailedException {
        buildChangeLogUpdater(customizer).updateChangeLog();
    }

    private void updateChangeLog() throws IOException, ProcessFailedException {
        updateChangeLog(UnaryOperator.identity());
    }

    @Test
    void testWithoutExistingChangelog() throws IOException, ProcessFailedException {
        // arrange
        addCommits("Initial commit", "fix: Adjusted readme", "feat: New version");
        tag();
        addCommits("chore: Adding homepage");

        // act
        updateChangeLog();

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
    void testBreakingChanges() throws IOException, ProcessFailedException {
        // arrange
        addCommits("Initial commit", "fix!: Adjusted readme");

        // act
        updateChangeLog();

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
    void testDepsScope() throws IOException, ProcessFailedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito", "chore(deps): Upgraded jUnit");

        // act
        updateChangeLog();

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
    void testDoNotGenerateUnreleasedSectionIfEmpty() throws IOException, ProcessFailedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        tag();

        // act
        updateChangeLog();

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
    void testDoNotGenerateUnreleasedSectionIfOnlyIgnoredCommits() throws IOException, ProcessFailedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        tag();
        addCommits("[maven-release-plugin]: prepare for next development iteration");

        // act
        updateChangeLog();

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
    void testDoNotGenerateUnreleasedSectionIfEmptyWithExistingChangeLog() throws IOException, ProcessFailedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        tag();

        writeChangeLog(
                """
            # My changelog

            ## Unreleased

            * Whatever
            """);

        // act
        updateChangeLog();

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
    void testGenerateChangelogTwiceWithoutTags() throws IOException, ProcessFailedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        updateChangeLog();
        String contents = readChangeLog();

        // act
        updateChangeLog();

        // assert
        String contents2 = readChangeLog();
        assertThat(contents2).isEqualToNormalizingNewlines(contents);
    }

    @Test
    void testGenerateChangelogTwiceWithTagAtLatest() throws IOException, ProcessFailedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        tag();
        updateChangeLog();
        String contents = readChangeLog();

        // act
        updateChangeLog();

        // assert
        String contents2 = readChangeLog();
        assertThat(contents2).isEqualToNormalizingNewlines(contents);
    }

    @Test
    void testGenerateChangelogTwiceWithTagAndUnreleasedChanges() throws IOException, ProcessFailedException {
        // arrange
        addCommits("chore: Added something", "deps: Upgraded mockito");
        tag();
        addCommits("fix: Important hotfix");
        updateChangeLog();
        String contents = readChangeLog();

        // act
        updateChangeLog();

        // assert
        String contents2 = readChangeLog();
        assertThat(contents2).isEqualToNormalizingNewlines(contents);
    }

    @Test
    void testTagOnIgnoredCommit() throws IOException, ProcessFailedException {
        // arrange
        addCommits("chore: Added something", "[maven-release-plugin]: release 1.0");
        tag();

        // act
        updateChangeLog();

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
    void testDoNotOverwrite() throws IOException, ProcessFailedException {
        // arrange
        addCommits("chore: Added something", "[maven-release-plugin]: release 1.0");
        tag();
        updateChangeLog();
        writeChangeLog(readChangeLog().replace("something", "oops"));

        // act
        updateChangeLog();

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
    void testDoNotOverwriteUnreleasedAlwaysGetsOverwritten() throws IOException, ProcessFailedException {
        // arrange
        addCommits("chore: Added something", "[maven-release-plugin]: release 1.0");
        tag();
        addCommits("fix: Various fixes");
        updateChangeLog();
        writeChangeLog(readChangeLog().replace("something", "oops").replace("Various", "Hilarious"));

        // act
        updateChangeLog();

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

    @Test
    void testOverwrite() throws IOException, ProcessFailedException {
        // arrange
        addCommits("chore: Added something", "[maven-release-plugin]: release 1.0");
        tag();
        updateChangeLog();
        writeChangeLog(readChangeLog().replace("something", "oops"));

        // act
        updateChangeLog(builder -> builder.overwrite(true));

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
    void testAssumeFutureRelease() throws IOException, ProcessFailedException {
        // arrange
        addCommits("chore: Added something", "[maven-release-plugin]: release 1.0");

        // act
        updateChangeLog(builder -> builder.futureVersion(new SemVer(1, 0, 0)));

        // assert
        assertThat(readChangeLog())
                .isEqualToNormalizingNewlines(
                        """
        # Changelog

        ## [1.0.0] - $now

        ### Miscellaneous Tasks

        * Added something
        """
                                .replace("$now", LocalDate.now().toString()));
    }

    private void addCommits(String... subjects) throws IOException, ProcessFailedException {
        for (String subject : subjects) {
            addCommit(subject);
        }
    }

    private void addCommit(String subject) throws IOException, ProcessFailedException {
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

    private void tag() throws ProcessFailedException {
        git.tag("v1.0", "Releasing 1.0");
    }
}
