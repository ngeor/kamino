package com.github.ngeor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 * Hello world!
 */
public final class App {
    private App() {}

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 4) {
            throw new IllegalArgumentException("Expected exactly 4 arguments");
        }

        String gpgKey = args[0];
        String gpgPassphrase = args[1];
        String nexusUsername = args[2];
        String nexusPassword = args[3];

        try {
            // create GPG key file
            File keysFile = createKeysFile();

            // gpg key workaround
            new ProcessBuilder(
                            "gpg",
                            "--batch",
                            "--yes",
                            "--passphrase=" + gpgPassphrase,
                            "--output",
                            "-",
                            keysFile.toString())
                    .inheritIO()
                    .start()
                    .waitFor();

            // now the real gpg key import
            List<Process> processes = ProcessBuilder.startPipeline(List.of(
                    new ProcessBuilder(
                                    "gpg",
                                    "--batch",
                                    "--yes",
                                    "--passphrase=" + gpgPassphrase,
                                    "--output",
                                    "-",
                                    keysFile.toString())
                            .inheritIO()
                            .redirectOutput(ProcessBuilder.Redirect.PIPE),
                    new ProcessBuilder("gpg", "--batch", "--yes", "--import")
                            .redirectError(ProcessBuilder.Redirect.INHERIT)));
            processes.get(processes.size() - 1).waitFor();

            // prepare settings.xml
            File settingsFile = createMavenSettingsFile(nexusUsername, nexusPassword, gpgKey, gpgPassphrase);

            // maven deploy
            runMavenDeploy(settingsFile);

        } finally {

            // rm -rf $HOME/.gnupg
            FileUtils.deleteDirectory(new File(FileUtils.getUserDirectory(), ".gnupg"));
        }
    }

    private static File createKeysFile() throws IOException {
        File keysFile = File.createTempFile("keys", ".asc");
        keysFile.deleteOnExit();
        try (InputStream is = App.class.getResourceAsStream("/keys.asc");
                FileOutputStream fos = new FileOutputStream(keysFile)) {
            is.transferTo(fos);
        }
        return keysFile;
    }

    private static File createMavenSettingsFile(
            String nexusUsername, String nexusPassword, String gpgKey, String gpgPassphrase) throws IOException {
        File settingsFile = File.createTempFile("settings", ".xml");
        settingsFile.deleteOnExit();
        try (InputStream is = App.class.getResourceAsStream("/settings.xml.template")) {
            Files.writeString(
                    settingsFile.toPath(),
                    String.format(
                            new String(is.readAllBytes(), StandardCharsets.UTF_8),
                            nexusUsername,
                            nexusPassword,
                            gpgKey,
                            gpgPassphrase));
        }
        return settingsFile;
    }

    private static void runMavenDeploy(File settingsFile) throws InterruptedException, IOException {
        new ProcessBuilder("mvn", "-B", "-s", settingsFile.toString(), "-Pgpg", "deploy")
                .inheritIO()
                .start()
                .waitFor();
    }
}
