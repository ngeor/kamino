package com.github.ngeor.mr;

import com.github.ngeor.changelog.TagPrefix;
import com.github.ngeor.git.FetchOption;
import com.github.ngeor.git.Git;
import com.github.ngeor.git.LsFilesOption;
import com.github.ngeor.git.PushOption;
import com.github.ngeor.maven.Maven;
import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.MavenDocument;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.versions.SemVer;
import com.github.ngeor.versions.SemVerBump;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import com.github.ngeor.yak4jdom.Preconditions;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.apache.commons.lang3.Validate;

public record MavenReleaser(File monorepoRoot, String path) {

    public void prepareRelease(SemVer nextVersion, boolean push)
            throws IOException, InterruptedException, ProcessFailedException {
        // calculate the groupId / artifactId of the module, do maven sanity checks
        MavenCoordinates moduleCoordinates = calcModuleCoordinatesAndDoSanityChecks();

        // do git sanity checks, pull latest
        Git git = new Git(monorepoRoot);
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

        // switch to release version (fixes all usages in the monorepo)
        setVersion(moduleCoordinates, nextVersion.toString());

        // make a backup of the pom file
        File backupPom = createBackupOfModulePomFile();

        // overwrite pom.xml with effective pom
        replacePomWithEffectivePom();

        // TODO changelog

        // commit and tag
        git.addAll();
        git.commit(String.format("release(%s): releasing %s", path, nextVersion));
        String tag = TagPrefix.forPath(path).addTagPrefix(nextVersion);
        git.tag(tag);

        // restore original pom
        restoreOriginalPom(backupPom);

        // switch to development version
        String developmentVersion =
                nextVersion.bump(SemVerBump.MINOR).preRelease("SNAPSHOT").toString();
        setVersion(moduleCoordinates.withVersion(nextVersion.toString()), developmentVersion);
        git.addAll();
        git.commit(String.format("release(%s): switching to development version %s", path, developmentVersion));

        if (push) {
            git.push(PushOption.TAGS);
        }
    }

    private MavenCoordinates calcModuleCoordinatesAndDoSanityChecks() {
        MavenDocument mavenDocument = MavenDocument.effectivePomWithoutResolvingProperties(modulePomFile());
        MavenCoordinates result = mavenDocument.coordinates().requireAllFields();
        // ensure modelVersion, name, description exist
        // ensure licenses/license/name and url
        // ensure developers/developer/name and email
        // ensure scm and related properties
        Preconditions.check(mavenDocument.getDocument())
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
        return result;
    }

    private void setVersion(MavenCoordinates moduleCoordinates, String newVersion)
            throws IOException, ProcessFailedException, InterruptedException {
        // maven at monorepo root
        Maven maven = new Maven(monorepoPomFile());
        maven.setVersion(moduleCoordinates, newVersion);
    }

    private File createBackupOfModulePomFile() throws IOException {
        // make a backup of the pom file
        File backupPom = File.createTempFile("pom", ".xml");
        backupPom.deleteOnExit();
        Files.copy(modulePomFile().toPath(), backupPom.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return backupPom;
    }

    private void replacePomWithEffectivePom() {
        File pomFile = modulePomFile();
        MavenDocument mavenDocument = MavenDocument.effectivePomWithoutResolvingProperties(pomFile);
        DocumentWrapper document = mavenDocument.getDocument();
        document.indent();
        document.write(pomFile);
    }

    private void restoreOriginalPom(File backupPom) throws IOException {
        Files.copy(backupPom.toPath(), modulePomFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private File monorepoPomFile() {
        return new File(monorepoRoot, "pom.xml");
    }

    private File modulePomFile() {
        return monorepoRoot.toPath().resolve(path).resolve("pom.xml").toFile();
    }
}
