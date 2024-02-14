package com.github.ngeor;

import java.io.File;
import java.io.IOException;

public final class ReleasePerformer {
    private final File monorepoRoot;
    private final String typeName;
    private final String projectName;

    public ReleasePerformer(File monorepoRoot, String typeName, String projectName) {
        this.monorepoRoot = monorepoRoot;
        this.typeName = typeName;
        this.projectName = projectName;
    }

    public void performRelease(SemVerBump bump, boolean dryRun) throws IOException, InterruptedException {
        Git git = new Git(monorepoRoot);

        // TODO ensure no pending changes

        // TODO ensure on default branch and on latest

        // TODO ensure no internal dependencies are on SNAPSHOT

        // TODO convert pom to effective pom and remove parent before publishing (and restore afterwards)

        SemVer maxReleaseVersion = SemVer.parse(git.getMostRecentTag(typeName + "/" + projectName + "/v"));
        final SemVer nextVersion = maxReleaseVersion.bump(bump);
        performRelease(nextVersion, dryRun);
    }

    public void performRelease(SemVer nextVersion) throws IOException, InterruptedException {
        performRelease(nextVersion, false);
    }

    public void performRelease(SemVer nextVersion, boolean dryRun) throws IOException, InterruptedException {
        String developmentVersion = nextVersion.bump(SemVerBump.MINOR).preRelease("SNAPSHOT").toString();
        String tag = typeName + "/" + projectName + "/v" + nextVersion;

        Maven maven = new Maven(monorepoRoot
                .toPath()
                .resolve(typeName)
                .resolve(projectName)
                .resolve("pom.xml")
                .toFile());
        maven.clean();
        maven.cleanRelease();
        if (dryRun) {
            System.out.printf(
                    "Would have prepared release tag=%s, nextVersion=%s, developmentVersion=%s%n",
                    tag, nextVersion, developmentVersion);
        } else {
            maven.prepareRelease(tag, nextVersion.toString(), developmentVersion);
        }
        maven.cleanRelease();
        maven.clean();
    }
}
