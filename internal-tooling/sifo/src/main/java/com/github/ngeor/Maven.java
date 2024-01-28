package com.github.ngeor;

import java.io.File;
import java.io.IOException;

public final class Maven {
    private final ProcessHelper processHelper;

    public Maven(File workingDirectory) {
        String cmd = System.getProperty("os.name").contains("Windows") ? "mvn.cmd" : "mvn";
        this.processHelper = new ProcessHelper(workingDirectory, cmd);
    }

    public void sortPom() throws IOException, InterruptedException {
        processHelper.run("-B", "-ntp", "-q", "com.github.ekryd.sortpom:sortpom-maven-plugin:sort");
    }

    public void cleanRelease() throws IOException, InterruptedException {
        processHelper.run("-B", "-ntp", "release:clean");
    }

    public void prepareRelease(String tag, String releaseVersion, String developmentVersion)
            throws IOException, InterruptedException {
        processHelper.run(
                "-B",
                "-ntp",
                "-Dtag=" + tag,
                "release:prepare",
                "-DreleaseVersion=" + releaseVersion,
                "-DdevelopmentVersion=" + developmentVersion);
    }
}
