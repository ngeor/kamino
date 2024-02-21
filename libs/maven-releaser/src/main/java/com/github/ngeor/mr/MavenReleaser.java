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
import java.util.List;
import java.util.Objects;

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
        MavenDocument mavenDocument = MavenDocument.effectivePomWithoutResolvingProperties(modulePomFile());
        MavenCoordinates result = mavenDocument.coordinates().requireAllFields();
        RequiredProperties requiredProperties;
        try {
            requiredProperties = mavenDocument.getDocument().asTyped(RequiredProperties.class);
        } catch (Exception ex) {
            if (ex.getCause() instanceof NullPointerException nullPointerException) {
                throw nullPointerException;
            } else if (ex.getCause() instanceof IllegalArgumentException illegalArgumentException) {
                throw illegalArgumentException;
            } else {
                throw new RuntimeException(ex);
            }
        }
        return result;
    }

    public record RequiredProperties(
            String modelVersion,
            String name,
            String description,
            List<License> licenses,
            List<Developer> developers,
            Scm scm) {
        public RequiredProperties {
            Objects.requireNonNull(modelVersion, "modelVersion is required");
            Objects.requireNonNull(name, "name is required");
            Objects.requireNonNull(description, "description is required");
            Objects.requireNonNull(licenses, "licenses is required");
            if (licenses.isEmpty()) {
                throw new IllegalArgumentException("licenses cannot be empty");
            }
            Objects.requireNonNull(developers, "developers is required");
            if (developers.isEmpty()) {
                throw new IllegalArgumentException("developers cannot be empty");
            }
            Objects.requireNonNull(scm, "scm is required");
        }
    }

    public record License(String name, String url) {
        public License {
            Objects.requireNonNull(name, "name is required");
            Objects.requireNonNull(url, "url is required");
        }
    }

    public record Developer(String name, String email) {
        public Developer {
            Objects.requireNonNull(name, "name is required");
            Objects.requireNonNull(email, "email is required");
        }
    }

    public record Scm(String connection, String developerConnection, String url, String tag) {
        public Scm {
            Objects.requireNonNull(connection, "connection is required");
            Objects.requireNonNull(developerConnection, "developerConnection is required");
            Objects.requireNonNull(url, "url is required");
            Objects.requireNonNull(tag, "tag is required");
        }
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
