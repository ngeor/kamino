package com.github.ngeor;

import java.io.File;
import java.io.IOException;

public final class MavenReleaser {
    private MavenReleaser() {}

    public static void prepareRelease(File monorepoRoot, String path, SemVer nextVersion, boolean dryRun)
            throws IOException, InterruptedException {
        String developmentVersion =
                nextVersion.bump(SemVerBump.MINOR).preRelease("SNAPSHOT").toString();
        String tag = path + "/v" + nextVersion;

        Maven maven =
                new Maven(monorepoRoot.toPath().resolve(path).resolve("pom.xml").toFile());
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
