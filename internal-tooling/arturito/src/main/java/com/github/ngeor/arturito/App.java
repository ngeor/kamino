package com.github.ngeor.arturito;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;

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
        Options options = parseOptions(args);
        try {
            // create GPG key file
            File keysFile = createKeysFile();

            Gpg gpg = new Gpg();

            // gpg key workaround
            gpg.workaround(options.gpg().passphrase(), keysFile);

            // now the real gpg key import
            gpg.importKey(options.gpg().passphrase(), keysFile);

            // prepare settings.xml
            File settingsFile = createMavenSettingsFile(options);

            // maven deploy
            runMavenDeploy(settingsFile, options);

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

    private static File createMavenSettingsFile(Options options) throws IOException {
        File settingsFile = File.createTempFile("settings", ".xml");
        settingsFile.deleteOnExit();
        try (InputStream is = getResource("/settings.xml.template")) {
            Files.writeString(
                    settingsFile.toPath(),
                    String.format(
                            new String(is.readAllBytes(), StandardCharsets.UTF_8),
                            options.nexus().username(),
                            options.nexus().password(),
                            options.gpg().key(),
                            options.gpg().passphrase()));
        }
        return settingsFile;
    }

    private static InputStream getResource(String resourceName) {
        return Objects.requireNonNull(
                App.class.getResourceAsStream(resourceName), "Resource " + resourceName + " not found");
    }

    private static void runMavenDeploy(File settingsFile, Options options) throws IOException, InterruptedException {
        int exitCode = new ProcessBuilder(mavenCommandLine(settingsFile.toString(), options))
                .directory(new File(".").toPath().resolve(options.path()).toFile())
                .inheritIO()
                .start()
                .waitFor();
        if (exitCode != 0) {
            throw new IOException("Deploy failed");
        }
    }

    static Options parseOptions(String[] args) {
        Map<String, String> parsedArgs = ArgParser.parse(args);
        String gpgKey = Validate.notBlank(parsedArgs.get("gpg-key"));
        String gpgPassphrase = Validate.notBlank(parsedArgs.get("gpg-passphrase"));
        String nexusUsername = Validate.notBlank(parsedArgs.get("nexus-username"));
        String nexusPassword = Validate.notBlank(parsedArgs.get("nexus-password"));
        String path = Validate.notBlank(parsedArgs.get("path"));
        return new Options(
                new GpgOptions(gpgKey, gpgPassphrase),
                new NexusOptions(nexusUsername, nexusPassword),
                path,
                parsedArgs.containsKey("debug"));
    }

    static List<String> mavenCommandLine(String settingsFile, Options options) {
        List<String> result = new ArrayList<>(List.of("mvn"));
        if (options.debug()) {
            result.add("--debug");
        }
        Collections.addAll(result, "-B", "-ntp", "-s", settingsFile, "-Pgpg", "deploy");
        return result;
    }
}
