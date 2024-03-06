package com.github.ngeor.mr;

import static com.github.ngeor.mr.Defaults.XML_INDENTATION;

import com.github.ngeor.changelog.ChangeLogUpdater;
import com.github.ngeor.changelog.ImmutableOptions;
import com.github.ngeor.changelog.TagPrefix;
import com.github.ngeor.git.Git;
import com.github.ngeor.git.PushOption;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.process.Maven;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public final class MavenReleaser {
    private final Options options;

    public MavenReleaser(Options options) {
        this.options = Objects.requireNonNull(options);
    }

    public void prepareRelease() throws IOException, ProcessFailedException {
        // do git sanity checks, pull latest
        Git git = new GitInitializer().apply(options.monorepoRoot());

        File modulePomFile = options.monorepoRoot()
                .toPath()
                .resolve(options.path())
                .resolve("pom.xml")
                .toFile();

        // calculate the groupId / artifactId of the module, do maven sanity checks
        MavenCoordinates moduleCoordinates = calcModuleCoordinatesAndDoSanityChecks(modulePomFile);
        VersionDiff releaseDiff = new VersionDiff(moduleCoordinates, options.nextVersion());
        Maven maven = new Maven(new File(options.monorepoRoot(), "pom.xml"));

        // Switch to release version.
        // Converts the pom to the effective pom, discarding parent information, so that it
        // can be published to Maven Central independently.
        switchToReleaseVersion(maven, git, releaseDiff, options, modulePomFile);

        // original, i.e. not effective, pom is restored at this point

        // switch to development version
        switchToNextDevelopmentVersion(maven, git, options.path(), releaseDiff);

        if (options.push()) {
            git.push(PushOption.TAGS);
        }
    }

    private static void switchToReleaseVersion(
            Maven maven, Git git, VersionDiff releaseDiff, Options options, File modulePomFile)
            throws ProcessFailedException, IOException {
        // switch to release version (fixes all usages in the monorepo)
        setVersion(maven, releaseDiff);

        // make a backup of the pom file
        try (BackupFile ignored = new BackupFile(modulePomFile)) {
            // overwrite pom.xml with effective pom
            String tag = TagPrefix.forPath(options.path()).addTagPrefix(options.nextVersion());

            replacePomWithEffectivePom(
                    modulePomFile,
                    RemoveParentElements.INSTANCE,
                    doc -> updateScmTag(doc, tag),
                    MavenReleaser::ensureNoSnapshotVersions);

            // update changelog
            new ChangeLogUpdater(ImmutableOptions.builder()
                            .rootDirectory(options.monorepoRoot())
                            .modulePath(options.path())
                            .formatOptions(options.formatOptions())
                            .futureVersion(options.nextVersion())
                            .build())
                    .updateChangeLog();

            // commit and tag
            git.addAll();
            git.commit(String.format("release(%s): releasing %s", options.path(), options.nextVersion()));
            git.tag(tag, String.format("Releasing %s", tag));
        }
    }

    private static void switchToNextDevelopmentVersion(Maven maven, Git git, String path, VersionDiff releaseDiff)
            throws ProcessFailedException {
        VersionDiff snapshotDiff = releaseDiff.toSnapshot();
        setVersion(maven, snapshotDiff);
        git.addAll();
        git.commit(String.format("release(%s): switching to development version %s", path, snapshotDiff.newVersion()));
    }

    private static MavenCoordinates calcModuleCoordinatesAndDoSanityChecks(File modulePomFile) {
        return new EffectivePomLoader()
                .andThen(FnUtil.toUnaryOperator(new PomBasicValidator()))
                .andThen(DomHelper::coordinates)
                .apply(modulePomFile);
    }

    private static void setVersion(Maven maven, VersionDiff versionDiff) throws ProcessFailedException {
        // maven at monorepo root
        maven.setVersion(versionDiff.oldVersion(), versionDiff.newVersion().toString());
    }

    @SafeVarargs
    private static void replacePomWithEffectivePom(File modulePomFile, Consumer<DocumentWrapper>... consumers) {
        // need to load the effective pom again because it has switched to the release version
        DocumentWrapper effectivePom = new EffectivePomLoader().apply(modulePomFile);
        for (Consumer<DocumentWrapper> consumer : consumers) {
            consumer.accept(effectivePom);
        }
        indentAndWrite(modulePomFile, effectivePom);
    }

    private static void updateScmTag(DocumentWrapper effectivePom, String tag) {
        effectivePom
                .getDocumentElement()
                .findChildElements("scm")
                .flatMap(e -> e.findChildElements("tag"))
                .forEach(e -> e.setTextContent(tag));
    }

    private static void indentAndWrite(File modulePomFile, DocumentWrapper effectivePom) {
        effectivePom.indent(XML_INDENTATION);
        effectivePom.write(modulePomFile);
    }

    private static void ensureNoSnapshotVersions(DocumentWrapper document) {
        Objects.requireNonNull(document);
        ensureNoSnapshotVersions(document.getDocumentElement());
    }

    private static void ensureNoSnapshotVersions(ElementWrapper element) {
        Objects.requireNonNull(element);
        // recursion
        element.getChildElements().forEach(MavenReleaser::ensureNoSnapshotVersions);

        if (ElementNames.VERSION.equals(element.getNodeName())) {
            String text = element.getTextContentTrimmed().orElse("");
            if (text.endsWith("-SNAPSHOT")) {
                throw new IllegalArgumentException(
                        String.format("Snapshot version %s is not allowed (%s)", text, element.path()));
            }
        }
    }
}
