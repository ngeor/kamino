package com.github.ngeor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
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
        if (args.length != 5) {
            throw new IllegalArgumentException("Expected exactly 5 arguments");
        }

        String gpgKey = args[0];
        String gpgPassphrase = args[1];
        String nexusUsername = args[2];
        String nexusPassword = args[3];
        String path = args[4];

        try {
            // create GPG key file
            File keysFile = createKeysFile();

            Gpg gpg = new Gpg();

            // gpg key workaround
            gpg.workaround(gpgPassphrase, keysFile);

            // now the real gpg key import
            gpg.importKey(gpgPassphrase, keysFile);

            // prepare settings.xml
            File settingsFile = createMavenSettingsFile(nexusUsername, nexusPassword, gpgKey, gpgPassphrase);

            // maven deploy
            runMavenDeploy(settingsFile, path);

        } finally {

            // rm -rf $HOME/.gnupg
            FileUtils.deleteDirectory(new File(FileUtils.getUserDirectory(), ".gnupg"));
        }
    }

    private static File createKeysFile() throws IOException {
        File keysFile = File.createTempFile("keys", ".asc");
        keysFile.deleteOnExit();
        try (InputStream is = getResource("/keys.asc");
                FileOutputStream fos = new FileOutputStream(keysFile)) {
            is.transferTo(fos);
        }
        return keysFile;
    }

    private static File createMavenSettingsFile(
            String nexusUsername, String nexusPassword, String gpgKey, String gpgPassphrase) throws IOException {
        File settingsFile = File.createTempFile("settings", ".xml");
        settingsFile.deleteOnExit();
        try (InputStream is = getResource("/settings.xml.template")) {
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

    private static InputStream getResource(String resourceName) {
        return Objects.requireNonNull(
                App.class.getResourceAsStream(resourceName), "Resource " + resourceName + " not found");
    }

    private static void runMavenDeploy(File settingsFile, String path) throws IOException, InterruptedException {
        int exitCode = new ProcessBuilder("mvn", "-B", "-ntp", "-Pgpg", "-s", settingsFile.getAbsolutePath(), "deploy")
                .directory(new File(path))
                .inheritIO()
                .start()
                .waitFor();
        if (exitCode != 0) {
            throw new IOException("Deploy failed");
        }
    }
}
