package com.github.ngeor;

import com.github.ngeor.changelog.TagPrefix;
import com.github.ngeor.git.Git;
import com.github.ngeor.process.ProcessFailedException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;

public class ReadmeGenerator {
    private final File rootDirectory;
    private final MavenModule mavenModule;
    private final String workflowId;
    private final LazyTagCalculator lazyTagCalculator;

    public ReadmeGenerator(File rootDirectory, MavenModule mavenModule, String workflowId) {
        this.rootDirectory = Objects.requireNonNull(rootDirectory);
        this.mavenModule = Objects.requireNonNull(mavenModule);
        this.workflowId = Validate.notBlank(workflowId);
        this.lazyTagCalculator = new LazyTagCalculator();
    }

    public void fixProjectBadges() throws IOException, ConcurrentException {
        List<String> lines = readLines();
        State state = State.INITIAL;
        EnumSet<KnownBadge> foundBadges = EnumSet.noneOf(KnownBadge.class);
        int i = 0;
        while (i < lines.size() && state != State.DONE) {
            String line = lines.get(i);
            switch (state) {
                case INITIAL -> {
                    if (looksLikeBadge(line)) {
                        state = State.IN_BADGES;
                    } else {
                        i++;
                    }
                }
                case IN_BADGES -> {
                    if (looksLikeBadge(line)) {
                        KnownBadge knownBadge = detectKnownBadge(line).orElse(null);
                        if (knownBadge != null) {
                            lines.set(i, renderKnownBadge(knownBadge));
                            foundBadges.add(knownBadge);
                        }

                        i++;
                    } else {
                        state = State.AFTER_BADGES;
                    }
                }
                case AFTER_BADGES -> {
                    // prepend missing badges
                    for (KnownBadge missingBadge : EnumSet.complementOf(foundBadges)) {
                        if (isBadgeApplicable(missingBadge)) {
                            lines.add(i, renderKnownBadge(missingBadge));
                        }
                    }
                    // exit the loop
                    state = State.DONE;
                }
            }
        }

        if (state == State.INITIAL || state == State.IN_BADGES) {
            // never found any badges in the document, or the document ends with badges
            for (KnownBadge missingBadge : EnumSet.complementOf(foundBadges)) {
                if (isBadgeApplicable(missingBadge)) {
                    lines.add(renderKnownBadge(missingBadge));
                }
            }
        }

        Files.writeString(readmePath(), String.join(System.lineSeparator(), lines) + System.lineSeparator());
    }

    private Path readmePath() {
        return mavenModule.pomFile().toPath().resolveSibling("README.md");
    }

    private String projectName() {
        return mavenModule.projectDirectory().getName();
    }

    private String groupId() {
        return mavenModule.coordinates().groupId();
    }

    private String artifactId() {
        return mavenModule.coordinates().artifactId();
    }

    private List<String> readLines() throws IOException {
        return new ArrayList<>(
                readmePath().toFile().exists() ? Files.readAllLines(readmePath()) : List.of("# " + projectName()));
    }

    private Optional<KnownBadge> detectKnownBadge(String line) {
        if (line.startsWith("[![Maven Central")) {
            return Optional.of(KnownBadge.MAVEN_CENTRAL);
        } else if (line.startsWith("[![Java CI") || line.startsWith("[![Build")) {
            return Optional.of(KnownBadge.BUILD);
        } else if (line.startsWith("[![javadoc")) {
            return Optional.of(KnownBadge.JAVADOC);
        }
        return Optional.empty();
    }

    private String renderKnownBadge(KnownBadge knownBadge) {
        return switch (knownBadge) {
            case BUILD -> renderBuildBadge();
            case JAVADOC -> renderJavaDocBadge();
            case MAVEN_CENTRAL -> renderMavenCentralBadge();
        };
    }

    private String renderBuildBadge() {
        String url = "https://github.com/ngeor/kamino/actions/workflows/build-" + workflowId + ".yml";
        return String.format("[![Build %s](%s/badge.svg)](%s)", projectName(), url, url);
    }

    private String renderJavaDocBadge() {
        String groupIdArtifactId = String.format("%s/%s", groupId(), artifactId());
        String badgeUrl = String.format("https://javadoc.io/badge2/%s/javadoc.svg", groupIdArtifactId);
        String url = String.format("https://javadoc.io/doc/%s", groupIdArtifactId);
        return String.format("[![javadoc](%s)](%s)", badgeUrl, url);
    }

    private String renderMavenCentralBadge() {
        String groupIdArtifactId = String.format("%s/%s", groupId(), artifactId());
        return "[![Maven Central](https://img.shields.io/maven-central/v/" + groupIdArtifactId
                + ".svg?label=Maven%20Central)](https://central.sonatype.com/artifact/" + groupIdArtifactId
                + "/overview)";
    }

    private boolean isBadgeApplicable(KnownBadge knownBadge) throws ConcurrentException {
        if (knownBadge == KnownBadge.BUILD) {
            // all projects build
            return true;
        }

        // if there are tags for this project, then it is probably a released library,
        // so the rest of the badges are applicable too
        return lazyTagCalculator.get();
    }

    private final class LazyTagCalculator extends LazyInitializer<Boolean> {
        @Override
        protected Boolean initialize() throws ConcurrentException {
            // if there are tags for this project, then it is probably a released library,
            // so the rest of the badges are applicable too
            Git git = new Git(rootDirectory);
            try {
                return git.getMostRecentTag(
                                TagPrefix.forPath(mavenModule.path()).tagPrefix())
                        .isPresent();
            } catch (IOException | ProcessFailedException e) {
                throw new ConcurrentException(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ConcurrentException(e);
            }
        }
    }

    private static boolean looksLikeBadge(String line) {
        return line.startsWith("[![");
    }

    private enum State {
        INITIAL,
        IN_BADGES,
        AFTER_BADGES,
        DONE
    }

    private enum KnownBadge {
        BUILD,
        JAVADOC,
        MAVEN_CENTRAL
    }
}
