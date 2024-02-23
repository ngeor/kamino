package com.github.ngeor.maven;

import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.process.ProcessHelper;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class Maven {
    private final ProcessHelper processHelper;
    private final File pomFile;

    public Maven(File pomFile) {
        String cmd = System.getProperty("os.name").contains("Windows") ? "mvn.cmd" : "mvn";
        this.pomFile = Objects.requireNonNull(pomFile);
        this.processHelper = new ProcessHelper(pomFile.getParentFile(), cmd, "-B", "-ntp", "--file", pomFile.getName());
    }

    public void sortPom() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.run("-q", "com.github.ekryd.sortpom:sortpom-maven-plugin:sort");
    }

    public void clean() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.runInheritIO("clean");
    }

    public void verify() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.runInheritIO("verify");
    }

    public DocumentWrapper effectivePomViaMaven() throws IOException, InterruptedException, ProcessFailedException {
        File output = File.createTempFile("pom", ".xml");
        try {
            effectivePomViaMaven(output);
            return DocumentWrapper.parse(output);
        } finally {
            output.delete();
        }
    }

    public void effectivePomViaMaven(File output) throws IOException, ProcessFailedException, InterruptedException {
        processHelper.run("help:effective-pom", "-Doutput=" + output.getAbsolutePath());
    }

    public void install() throws IOException, InterruptedException, ProcessFailedException {
        processHelper.runInheritIO("install");
    }

    public void setVersion(MavenCoordinates moduleCoordinates, String newVersion)
            throws IOException, ProcessFailedException, InterruptedException {
        processHelper.run(
                "versions:set",
                String.format("-DgroupId=%s", moduleCoordinates.groupId()),
                String.format("-DartifactId=%s", moduleCoordinates.artifactId()),
                String.format("-DoldVersion=%s", moduleCoordinates.version()),
                String.format("-DnewVersion=%s", newVersion));
    }
}
