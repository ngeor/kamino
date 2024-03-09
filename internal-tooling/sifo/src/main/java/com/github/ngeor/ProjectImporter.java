package com.github.ngeor;

import com.github.ngeor.git.Git;
import com.github.ngeor.git.Tag;
import com.github.ngeor.maven.process.Maven;
import com.github.ngeor.mr.Defaults;
import com.github.ngeor.mr.ImmutableOptions;
import com.github.ngeor.mr.MavenReleaser;
import com.github.ngeor.mr.Options;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.versions.SemVer;
import com.github.ngeor.versions.SemVerBump;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

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
        this.monorepoRoot = Objects.requireNonNull(monorepoRoot);
        this.oldRepoRoot = Objects.requireNonNull(oldRepoRoot);
        this.typeName = Validate.notBlank(typeName);
        this.githubToken = githubToken;
    }

    private String modulePath() {
        return String.join("/", typeName, oldRepoRoot.getName());
    }

    public void run() throws IOException, InterruptedException, ProcessFailedException {
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
        monorepo.subTreeAdd(modulePath(), oldRepoRoot, oldRepo.getDefaultBranch());
    }

    private void adjustImportedCode() throws IOException, ProcessFailedException {
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

        if (Defaults.isEligibleForRelease(modulePath())) {
            Git git = new Git(oldRepoRoot);
            SemVer maxReleaseVersion = git.getTags("v", true)
                    .map(Tag::name)
                    .map(SemVer::parse)
                    .findFirst()
                    .orElseThrow();
            SemVer nextVersion = maxReleaseVersion.bump(SemVerBump.PATCH);
            Options options = ImmutableOptions.builder()
                    .monorepoRoot(monorepoRoot)
                    .path(modulePath())
                    .formatOptions(Defaults.defaultFormatOptions())
                    .xmlIndentation(Defaults.XML_INDENTATION)
                    .nextVersion(nextVersion)
                    .push(true)
                    .build();
            new MavenReleaser(options).prepareRelease();
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
