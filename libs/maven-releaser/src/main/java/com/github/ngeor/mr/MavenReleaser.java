package com.github.ngeor.mr;

import static com.github.ngeor.mr.Defaults.XML_INDENTATION;

import com.github.ngeor.changelog.ChangeLogUpdater;
import com.github.ngeor.changelog.TagPrefix;
import com.github.ngeor.git.FetchOption;
import com.github.ngeor.git.Git;
import com.github.ngeor.git.LsFilesOption;
import com.github.ngeor.git.PushOption;
import com.github.ngeor.maven.document.effective.EffectivePomFactory;
import com.github.ngeor.maven.document.loader.FileDocumentLoader;
import com.github.ngeor.maven.document.parent.CanLoadParentFactory;
import com.github.ngeor.maven.dom.DomHelper;
import com.github.ngeor.maven.dom.ElementNames;
import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.maven.process.Maven;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.versions.SemVerBump;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.ElementWrapper;
import com.github.ngeor.yak4jdom.Preconditions;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.Validate;

public final class MavenReleaser {
    private final Options options;

    public MavenReleaser(Options options) {
        this.options = Objects.requireNonNull(options);
    }

    public void prepareRelease() throws IOException, ProcessFailedException {
        File modulePomFile = options.monorepoRoot()
                .toPath()
                .resolve(options.path())
                .resolve("pom.xml")
                .toFile();

        // calculate the groupId / artifactId of the module, do maven sanity checks
        MavenCoordinates moduleCoordinates = calcModuleCoordinatesAndDoSanityChecks(modulePomFile);

        // do git sanity checks, pull latest
        Git git = initializeGitAndDoSanityChecks();

        // switch to release version (fixes all usages in the monorepo)
        setVersion(moduleCoordinates, options.nextVersion().toString());

        // make a backup of the pom file
        File backupPom = createBackupOfModulePomFile(modulePomFile);

        // overwrite pom.xml with effective pom
        String tag = TagPrefix.forPath(options.path()).addTagPrefix(options.nextVersion());
        replacePomWithEffectivePom(modulePomFile, tag);

        // update changelog
        new ChangeLogUpdater(options.monorepoRoot(), options.path(), options.formatOptions())
                .updateChangeLog(false, options.nextVersion());

        // commit and tag
        git.addAll();
        git.commit(String.format("release(%s): releasing %s", options.path(), options.nextVersion()));
        git.tag(tag, String.format("Releasing %s", options.nextVersion()));

        // restore original pom
        restoreOriginalPom(backupPom, modulePomFile);

        // switch to development version
        String developmentVersion = options.nextVersion()
                .bump(SemVerBump.MINOR)
                .preRelease("SNAPSHOT")
                .toString();
        setVersion(moduleCoordinates.withVersion(options.nextVersion().toString()), developmentVersion);
        git.addAll();
        git.commit(
                String.format("release(%s): switching to development version %s", options.path(), developmentVersion));

        if (options.push()) {
            git.push(PushOption.TAGS);
        }
    }

    private MavenCoordinates calcModuleCoordinatesAndDoSanityChecks(File modulePomFile) {
        DocumentWrapper effectivePom = loadEffectivePom(modulePomFile);
        return calcModuleCoordinatesAndDoSanityChecks(effectivePom);
    }

    static MavenCoordinates calcModuleCoordinatesAndDoSanityChecks(DocumentWrapper effectivePom) {
        // ensure modelVersion, name, description exist
        // ensure licenses/license/name and url
        // ensure developers/developer/name and email
        // ensure scm and related properties
        Preconditions.check(effectivePom)
                .hasChildWithTextContent("modelVersion")
                .hasChildWithTextContent("name")
                .hasChildWithTextContent("description")
                .hasChild("licenses")
                .forEachChild("licenses", licenses -> licenses.hasChild("license")
                        .forEachChild("license", license -> license.hasChildWithTextContent("name")
                                .hasChildWithTextContent("url")))
                .hasChild("developers")
                .forEachChild(
                        "developers",
                        developers -> developers.hasChild("developer").forEachChild("developer", developer -> developer
                                .hasChildWithTextContent("name")
                                .hasChildWithTextContent("email")))
                .hasChildThat("scm", scm -> scm.hasChildWithTextContent("connection")
                        .hasChildWithTextContent("developerConnection")
                        .hasChildWithTextContent("tag")
                        .hasChildWithTextContent("url"));
        return DomHelper.coordinates(effectivePom);
    }

    private Git initializeGitAndDoSanityChecks() throws ProcessFailedException {
        Git git = new Git(options.monorepoRoot());
        git.ensureOnDefaultBranch();
        Validate.isTrue(!git.hasStagedChanges(), "repo has staged files");
        Validate.isTrue(!git.hasNonStagedChanges(), "repo has modified files");
        Validate.isTrue(
                git.lsFiles(LsFilesOption.OTHER, LsFilesOption.EXCLUDE_STANDARD)
                        .findFirst()
                        .isEmpty(),
                "repo has untracked files");
        git.fetch(FetchOption.PRUNE, FetchOption.PRUNE_TAGS, FetchOption.TAGS);
        git.pull();
        return git;
    }

    private void setVersion(MavenCoordinates moduleCoordinates, String newVersion) throws ProcessFailedException {
        // maven at monorepo root
        Maven maven = new Maven(monorepoPomFile());
        maven.setVersion(moduleCoordinates, newVersion);
    }

    private File createBackupOfModulePomFile(File modulePomFile) throws IOException {
        // make a backup of the pom file
        File backupPom = File.createTempFile("pom", ".xml");
        backupPom.deleteOnExit();
        Files.copy(modulePomFile.toPath(), backupPom.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return backupPom;
    }

    private void replacePomWithEffectivePom(File modulePomFile, String tag) {
        DocumentWrapper effectivePom = loadEffectivePom(modulePomFile);
        Set<String> elementsToRemove = Set.of(ElementNames.MODULES, ElementNames.PARENT);
        effectivePom.getDocumentElement().removeChildNodesByName(elementsToRemove::contains);
        effectivePom
                .getDocumentElement()
                .findChildElements("scm")
                .flatMap(e -> e.findChildElements("tag"))
                .forEach(e -> e.setTextContent(tag));
        effectivePom.indent(XML_INDENTATION);
        effectivePom.write(modulePomFile);
        ensureNoSnapshotVersions(effectivePom);
    }

    private DocumentWrapper loadEffectivePom(File modulePomFile) {
        return FileDocumentLoader.asFactory()
                .decorate(CanLoadParentFactory::new)
                .decorate(EffectivePomFactory::new)
                .createDocumentLoader(modulePomFile)
                .effectivePom();
    }

    private void ensureNoSnapshotVersions(DocumentWrapper document) {
        Objects.requireNonNull(document);
        ensureNoSnapshotVersions(document.getDocumentElement());
    }

    private void ensureNoSnapshotVersions(ElementWrapper element) {
        Objects.requireNonNull(element);
        // recursion
        element.getChildElements().forEach(this::ensureNoSnapshotVersions);

        if (ElementNames.VERSION.equals(element.getNodeName())) {
            String text = element.getTextContentTrimmed().orElse("");
            if (text.endsWith("-SNAPSHOT")) {
                throw new IllegalArgumentException(
                        String.format("Snapshot version %s is not allowed (%s)", text, element.path()));
            }
        }
    }

    private void restoreOriginalPom(File backupPom, File modulePomFile) throws IOException {
        Files.copy(backupPom.toPath(), modulePomFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private File monorepoPomFile() {
        return new File(options.monorepoRoot(), "pom.xml");
    }
}
