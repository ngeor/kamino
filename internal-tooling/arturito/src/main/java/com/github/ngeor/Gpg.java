package com.github.ngeor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public class Gpg {
    private final List<String> commonArgs = List.of("gpg", "--batch", "--yes");

    public void workaround(String passphrase, File keysFile) throws IOException, InterruptedException {
        Validate.notBlank(passphrase);
        Objects.requireNonNull(keysFile);
        createProcessBuilder("--passphrase=" + passphrase, "--output", "-", keysFile.toString())
                .inheritIO()
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .start()
                .waitFor();
    }

    public void importKey(String passphrase, File keysFile) throws IOException, InterruptedException {
        Validate.notBlank(passphrase);
        Objects.requireNonNull(keysFile);
        List<Process> processes = ProcessBuilder.startPipeline(List.of(
                createProcessBuilder("--passphrase=" + passphrase, "--output", "-", keysFile.toString())
                        .inheritIO()
                        .redirectOutput(ProcessBuilder.Redirect.PIPE),
                createProcessBuilder("--import")
                        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)));
        processes.get(processes.size() - 1).waitFor();
    }

    private ProcessBuilder createProcessBuilder(String... args) {
        List<String> allArgs = new ArrayList<>(commonArgs);
        Collections.addAll(allArgs, args);
        return new ProcessBuilder(allArgs);
    }
}
