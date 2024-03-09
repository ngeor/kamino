package com.github.ngeor.maven.process;

import com.github.ngeor.maven.dom.MavenCoordinates;
import com.github.ngeor.process.ProcessFailedException;
import com.github.ngeor.process.ProcessHelper;
import com.github.ngeor.yak4jdom.DocumentWrapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public final class Maven {
    private final ProcessHelper processHelper;

    public Maven(File pomFile) {
        this(pomFile, null, null);
    }

    public Maven(File pomFile, File settingsFile, String profile) {
        String cmd = System.getProperty("os.name").contains("Windows") ? "mvn.cmd" : "mvn";
        List<String> baseArgs = new ArrayList<>(List.of(cmd, "-B", "-ntp"));
        if (settingsFile != null) {
            baseArgs.addAll(List.of("-s", settingsFile.toString()));
        }
        if (!StringUtils.isBlank(profile)) {
            baseArgs.add(String.format("-P%s", profile));
        }
        baseArgs.addAll(List.of("--file", pomFile.getName()));
        this.processHelper = new ProcessHelper(pomFile.getParentFile(), baseArgs);
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
