package com.github.ngeor;

import static com.github.ngeor.ProcessUtils.waitForSuccess;

import java.io.IOException;

public class NpmVersionSetter implements VersionSetter {
    @Override
    public void bumpVersion(String version) throws IOException, InterruptedException {
        ProcessBuilder processBuilder =
                new ProcessBuilder("cmd", "/C", "npm.cmd", "version", "--no-git-tag-version", version);
        Process process = processBuilder.start();
        waitForSuccess(process);
    }
}
