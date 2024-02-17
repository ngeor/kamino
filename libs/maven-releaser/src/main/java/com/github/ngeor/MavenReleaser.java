package com.github.ngeor;

import com.github.ngeor.yak4jdom.DocumentWrapper;

import java.io.File;
import java.io.IOException;

public final class MavenReleaser {
    private MavenReleaser() {}

    public static void prepareRelease(File monorepoRoot, String path, SemVer nextVersion, boolean dryRun, boolean push)
            throws IOException, InterruptedException, ProcessFailedException {
        String developmentVersion =
                nextVersion.bump(SemVerBump.MINOR).preRelease("SNAPSHOT").toString();
        String tag = path + "/v" + nextVersion;

        Maven maven =
                new Maven(monorepoRoot.toPath().resolve(path).resolve("pom.xml").toFile());
        maven.prepareRelease(tag, nextVersion.toString(), developmentVersion, dryRun, push);
    }

    public static String updateVersion(String input, String version) {
        DocumentWrapper document = DocumentWrapper.parseString(input);
        document.getDocumentElement().firstElement("version").ifPresent(e -> e.setTextContent(version));
        return document.writeToString();
    }
}
