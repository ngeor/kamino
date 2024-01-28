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
    private final String githubToken;

    public ProjectImporter(File monorepoRoot, File oldRepoRoot, String typeName, String githubToken) {
        this.monorepoRoot = monorepoRoot;
        this.oldRepoRoot = oldRepoRoot;
        this.typeName = typeName;
        this.githubToken = githubToken;
    }

    public void run()
            throws IOException, InterruptedException, ParserConfigurationException, TransformerException, SAXException {
        ensureGitLatest();
        importGitSubtree();
        adjustImportedCode();
        performPatchRelease();
        archiveImportedRepo();
    }

    private void ensureGitLatest() throws IOException, InterruptedException {
        for (Git git : new Git[] {new Git(monorepoRoot), new Git(oldRepoRoot)}) {
            git.checkout(git.getDefaultBranch());
            git.pull();
        }
    }

    private void importGitSubtree() throws IOException, InterruptedException {
        // git subtree add -P packages/foo ../source master
        if (monorepoRoot.toPath().resolve(typeName).resolve(oldRepoRoot.getName()).toFile().isDirectory()) {
            System.out.println("git subtree already imported");
            return;
        }
        System.out.println("Importing git subtree");
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
        monorepo.addAll();
        monorepo.commit("chore: Adjusted imported code");
        monorepo.push();
    }

    private void performPatchRelease() throws IOException, InterruptedException {
        Git git = new Git(oldRepoRoot);
        SemVer maxReleaseVersion = SemVer.parse(git.getMostRecentTag("v").replace("v", ""));

        new ReleasePerformer(monorepoRoot, typeName, oldRepoRoot.getName()).performPatchRelease(maxReleaseVersion);
    }

    private void archiveImportedRepo() throws IOException, InterruptedException {
        new ProjectArchiver(
                        oldRepoRoot,
                        String.format(
                                "https://github.com/ngeor/kamino/tree/master/%s/%s", typeName, oldRepoRoot.getName()),
                        githubToken)
                .run();
    }
}
