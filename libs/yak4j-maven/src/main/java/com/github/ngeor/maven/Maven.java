package com.github.ngeor.maven;

import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.process.ProcessHelper;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.io.IOException;

public final class Maven {
    private final ProcessHelper processHelper;

    public Maven(File pomFile) {
        String cmd = System.getProperty("os.name").contains("Windows") ? "mvn.cmd" : "mvn";
        this.processHelper = new ProcessHelper(pomFile.getParentFile(), cmd, "-B", "-ntp", "--file", pomFile.getName());
    }

    public void sortPom() throws ProcessFailedException {
        processHelper.run("-q", "com.github.ekryd.sortpom:sortpom-maven-plugin:sort");
    }

    public void clean() throws ProcessFailedException {
        processHelper.runInheritIO("clean");
    }

    public void verify() throws ProcessFailedException {
        processHelper.runInheritIO("verify");
    }

    public DocumentWrapper effectivePomViaMaven() throws IOException, ProcessFailedException {
        File output = File.createTempFile("pom", ".xml");
        try {
            effectivePomViaMaven(output);
            return DocumentWrapper.parse(output);
        } finally {
            output.delete();
        }
    }

    public void effectivePomViaMaven(File output) throws ProcessFailedException {
        processHelper.run("help:effective-pom", "-Doutput=" + output.getAbsolutePath());
    }

    public void install() throws ProcessFailedException {
        processHelper.runInheritIO("install");
    }

    public void setVersion(MavenCoordinates moduleCoordinates, String newVersion) throws ProcessFailedException {
        processHelper.run(
                "versions:set",
                String.format("-DgroupId=%s", moduleCoordinates.groupId()),
                String.format("-DartifactId=%s", moduleCoordinates.artifactId()),
                String.format("-DoldVersion=%s", moduleCoordinates.version()),
                String.format("-DnewVersion=%s", newVersion));
    }
}
