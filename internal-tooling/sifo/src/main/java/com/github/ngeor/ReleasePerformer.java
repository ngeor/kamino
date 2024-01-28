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

    public void performPatchRelease(SemVer maxReleaseVersion) throws IOException, InterruptedException {
        String nextVersion = maxReleaseVersion.increasePatch().toString();
        String developmentVersion = maxReleaseVersion.increaseMinor().toString() + "-SNAPSHOT";
        String tag = typeName + "/" + projectName + "/v" + nextVersion;

        Maven maven = new Maven(
                monorepoRoot.toPath().resolve(typeName).resolve(projectName).toFile());
        maven.cleanRelease();
        maven.prepareRelease(tag, nextVersion, developmentVersion);
        maven.cleanRelease();
    }
}
