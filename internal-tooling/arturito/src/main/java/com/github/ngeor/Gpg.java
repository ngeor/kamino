package com.github.ngeor;

import com.github.ngeor.process.ProcessBuilderWithArgs;
import com.github.ngeor.process.ProcessHelper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.Validate;

public class Gpg extends ProcessHelper {
    public Gpg(File workingDirectory) {
        super(workingDirectory, "gpg", "--batch", "--yes");
    }

    public void workaround(String passphrase, File keysFile) throws IOException, InterruptedException {
        Validate.notBlank(passphrase);
        Objects.requireNonNull(keysFile);
        ProcessBuilderWithArgs processBuilderWithArgs =
                createProcessBuilder("--passphrase=" + passphrase, "--output", "-", keysFile.toString());
        processBuilderWithArgs
                .processBuilder()
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
                        .processBuilder()
                        .inheritIO()
                        .redirectOutput(ProcessBuilder.Redirect.PIPE),
                createProcessBuilder("--import")
                        .processBuilder()
                        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)));
        processes.get(processes.size() - 1).waitFor();
    }
}
