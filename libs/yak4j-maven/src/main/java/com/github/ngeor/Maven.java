package com.github.ngeor;

import java.io.File;
import java.io.IOException;

public final class Maven {
    private final ProcessHelper processHelper;

    public Maven(File pomFile) {
        String cmd = System.getProperty("os.name").contains("Windows") ? "mvn.cmd" : "mvn";
        this.processHelper = new ProcessHelper(pomFile.getParentFile(), cmd, "-B", "-ntp", "--file", pomFile.getName());
    }

    public void sortPom() throws IOException, InterruptedException {
        processHelper.run("-q", "com.github.ekryd.sortpom:sortpom-maven-plugin:sort");
    }

    public void cleanRelease() throws IOException, InterruptedException {
        processHelper.run("release:clean");
    }

    public void prepareRelease(String tag, String releaseVersion, String developmentVersion)
            throws IOException, InterruptedException {
        processHelper.runInheritIO(
                "-Dtag=" + tag,
                "release:prepare",
                "-DreleaseVersion=" + releaseVersion,
                "-DdevelopmentVersion=" + developmentVersion);
    }

    public void clean() throws IOException, InterruptedException {
        processHelper.runInheritIO("clean");
    }

    public void verify() throws IOException, InterruptedException {
        processHelper.runInheritIO("verify");
    }

    public void effectivePom(File output) throws IOException, InterruptedException {
        processHelper.run("help:effective-pom", "-Doutput=" + output.getAbsolutePath());
    }
}
