package com.github.ngeor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class ProcessUtils {
    private ProcessUtils() {}

    public static void waitForSuccess(Process process) throws InterruptedException, IOException {
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            try (BufferedInputStream stderr = new BufferedInputStream(process.getErrorStream())) {
                String error = new String(stderr.readAllBytes(), StandardCharsets.UTF_8);
                throw new IOException(String.format("Command failed with exit code %d: %s", exitCode, error));
            }
        }
    }
}
