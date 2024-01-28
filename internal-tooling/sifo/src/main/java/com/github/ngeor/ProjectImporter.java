package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 * Imports a project from a different repository into the monorepo.
 */
public class ProjectImporter {
    private final File monorepoRoot;
    private final File oldRepoRoot;
    private final String typeName;

    public ProjectImporter(File monorepoRoot, File oldRepoRoot, String typeName) {
        this.monorepoRoot = monorepoRoot;
        this.oldRepoRoot = oldRepoRoot;
        this.typeName = typeName;
    }

    public void run()
            throws IOException, InterruptedException, ParserConfigurationException, TransformerException, SAXException {
        ensureGitLatest();
        importGitSubtree();
        adjustImportedCode();
        performPatchRelease();
        archiveImportedRepo();
        deleteLocalFolderOfImportedRepo();
    }

    private void ensureGitLatest() throws IOException, InterruptedException {
        for (Git git : new Git[] {new Git(monorepoRoot), new Git(oldRepoRoot)}) {
            git.checkout(git.getDefaultBranch());
            git.pull();
        }
    }

    private void importGitSubtree() throws IOException, InterruptedException {
        // git subtree add -P packages/foo ../source master
        Git monorepo = new Git(monorepoRoot);
        Git oldRepo = new Git(oldRepoRoot);
        monorepo.subTreeAdd(typeName + "/" + oldRepoRoot.getName(), oldRepoRoot, oldRepo.getDefaultBranch());
    }

    private void adjustImportedCode()
            throws IOException, ParserConfigurationException, InterruptedException, TransformerException, SAXException {
        new TemplateGenerator(monorepoRoot)
                .regenerateAllTemplates(
                        monorepoRoot.toPath().resolve(typeName).toFile(),
                        monorepoRoot
                                .toPath()
                                .resolve(typeName)
                                .resolve(oldRepoRoot.getName())
                                .toFile(),
                        monorepoRoot
                                .toPath()
                                .resolve(typeName)
                                .resolve(oldRepoRoot.getName())
                                .resolve("pom.xml")
                                .toFile());

        Git monorepo = new Git(monorepoRoot);
        monorepo.commitAll("chore: Adjusted imported code");
        monorepo.push();
    }

    private void performPatchRelease() throws IOException, InterruptedException {
        Maven maven = new Maven(monorepoRoot
                .toPath()
                .resolve(typeName)
                .resolve(oldRepoRoot.getName())
                .toFile());
        maven.cleanRelease();
        String currentVersion = "";
        String maxReleaseVersion = "";
        String nextVersion = "";
        String developmentVersion = "";
        String tag = typeName + "/" + oldRepoRoot.getName() + "/v" + nextVersion;
        maven.prepareRelease(tag, nextVersion, developmentVersion);
        maven.cleanRelease();
    }

    private void archiveImportedRepo() {}

    private void deleteLocalFolderOfImportedRepo() {}
}
