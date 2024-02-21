package com.github.ngeor.mr;

import com.github.ngeor.Git;
import com.github.ngeor.ProcessFailedException;
import com.github.ngeor.PushOption;
import com.github.ngeor.maven.Maven;
import com.github.ngeor.maven.MavenCoordinates;
import com.github.ngeor.maven.MavenDocument;
import com.github.ngeor.versions.SemVer;
import com.github.ngeor.versions.SemVerBump;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public record MavenReleaser(File monorepoRoot, String path) {

    public void prepareRelease(SemVer nextVersion, boolean push)
            throws IOException, InterruptedException, ProcessFailedException {

        // calculate the groupId / artifactId of the module
        MavenCoordinates moduleCoordinates = calcModuleCoordinatesAndDoSanityChecks();

        // switch to release version (fixes all usages in the monorepo)
        setVersion(moduleCoordinates, nextVersion.toString());

        // make a backup of the pom file
        File backupPom = createBackupOfModulePomFile();

        // overwrite pom.xml with effective pom
        replacePomWithEffectivePom();

        // TODO changelog

        // commit and tag
        Git git = new Git(monorepoRoot);
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
        // maven at module
        MavenDocument mavenDocument = MavenDocument.effectivePomWithoutResolvingProperties(modulePomFile());
        MavenCoordinates result = mavenDocument.coordinates().requireAllFields();
        if (mavenDocument.modelVersion().isEmpty()) {
            throw new IllegalStateException("Cannot release %s:%s without modelVersion element"
                    .formatted(result.groupId(), result.artifactId()));
        }
        if (mavenDocument.name().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot release %s:%s without name element".formatted(result.groupId(), result.artifactId()));
        }
        if (mavenDocument.description().isEmpty()) {
            throw new IllegalStateException("Cannot release %s:%s without description element"
                    .formatted(result.groupId(), result.artifactId()));
        }
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
