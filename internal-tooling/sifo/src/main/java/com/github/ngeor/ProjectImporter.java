package com.github.ngeor;

import com.github.ngeor.git.Git;
import com.github.ngeor.maven.process.Maven;
import com.github.ngeor.mr.Defaults;
import com.github.ngeor.mr.MavenReleaser;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.versions.SemVer;
import com.github.ngeor.versions.SemVerBump;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.concurrent.ConcurrentException;

/**
 * Imports a project from a different repository into the monorepo.
 */
@SuppressWarnings("java:S106") // allow System.out.println
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

    public void run() throws IOException, InterruptedException, ProcessFailedException, ConcurrentException {
        ensureGitLatest();
        importGitSubtree();
        adjustImportedCode();
        performPatchRelease();
        archiveImportedRepo();
    }

    private void ensureGitLatest() throws ProcessFailedException {
        System.out.println("Ensure git is on default branch and has latest");
        for (File projectDirectory : new File[] {monorepoRoot, oldRepoRoot}) {
            Git git = new Git(projectDirectory);
            git.checkout(git.getDefaultBranch());
            git.pull();
        }
    }

    private void ensureOldProjectBuilds() throws ProcessFailedException {
        System.out.println("Ensure project to be imported builds");
        ensureProjectBuilds(oldRepoRoot);
    }

    private void ensureImportedProjectBuilds() throws ProcessFailedException {
        System.out.println("Ensure imported project builds from new location");
        ensureProjectBuilds(monorepoRoot
                .toPath()
                .resolve(typeName)
                .resolve(oldRepoRoot.getName())
                .resolve("pom.xml")
                .toFile());
    }

    private void ensureProjectBuilds(File file) throws ProcessFailedException {
        Maven maven = new Maven(file);
        maven.clean();
        maven.verify();
        maven.clean();
    }

    private void importGitSubtree() throws ProcessFailedException {
        // git subtree add -P packages/foo ../source master
        if (monorepoRoot
                .toPath()
                .resolve(typeName)
                .resolve(oldRepoRoot.getName())
                .toFile()
                .isDirectory()) {
            System.out.println("git subtree already imported");
            return;
        }
        ensureOldProjectBuilds();
        System.out.println("Importing git subtree");
        Git monorepo = new Git(monorepoRoot);
        Git oldRepo = new Git(oldRepoRoot);
        monorepo.subTreeAdd(typeName + "/" + oldRepoRoot.getName(), oldRepoRoot, oldRepo.getDefaultBranch());
    }

    private void adjustImportedCode() throws IOException, ProcessFailedException, ConcurrentException {
        new TemplateGenerator(monorepoRoot).regenerateAllTemplates();

        Git monorepo = new Git(monorepoRoot);
        monorepo.addAll();
        if (monorepo.hasStagedChanges()) {
            monorepo.commit("chore: Adjusted imported code");
            monorepo.push();
        }
    }

    private void performPatchRelease() throws IOException, ProcessFailedException {
        ensureImportedProjectBuilds();

        if (TemplateGenerator.requiresReleaseWorkflow(typeName)) {
            Git git = new Git(oldRepoRoot);
            SemVer maxReleaseVersion = git.getMostRecentTag("v")
                    .map(tag -> tag.name().substring(1))
                    .map(SemVer::parse)
                    .orElseThrow();
            SemVer nextVersion = maxReleaseVersion.bump(SemVerBump.PATCH);

            new MavenReleaser(monorepoRoot, typeName + "/" + oldRepoRoot.getName(), Defaults.defaultFormatOptions())
                    .prepareRelease(nextVersion, true);
        }
    }

    private void archiveImportedRepo() throws IOException, InterruptedException, ProcessFailedException {
        new ProjectArchiver(
                        oldRepoRoot,
                        String.format(
                                "https://github.com/ngeor/kamino/tree/master/%s/%s", typeName, oldRepoRoot.getName()),
                        githubToken)
                .run();
    }
}
