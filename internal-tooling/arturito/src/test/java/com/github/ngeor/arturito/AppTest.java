package com.github.ngeor.arturito;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class AppTest {
    @Test
    void testParseOptions() {
        String[] args = new String[] {
            "--gpg-key",
            "abc",
            "--gpg-passphrase",
            "pass",
            "--nexus-username",
            "who",
            "--nexus-password",
            "secret",
            "--path",
            "/tmp"
        };
        Options options = App.parseOptions(args);
        assertThat(options.gpg().key()).isEqualTo("abc");
        assertThat(options.gpg().passphrase()).isEqualTo("pass");
        assertThat(options.nexus().username()).isEqualTo("who");
        assertThat(options.nexus().password()).isEqualTo("secret");
        assertThat(options.path()).isEqualTo("/tmp");
        assertThat(options.debug()).isFalse();
    }

    @Test
    void testParseOptionsWithDebug() {
        String[] args = new String[] {
            "--gpg-key",
            "abc",
            "--gpg-passphrase",
            "pass",
            "--nexus-username",
            "who",
            "--nexus-password",
            "secret",
            "--path",
            "/tmp",
            "--debug"
        };
        Options options = App.parseOptions(args);
        assertThat(options.gpg().key()).isEqualTo("abc");
        assertThat(options.gpg().passphrase()).isEqualTo("pass");
        assertThat(options.nexus().username()).isEqualTo("who");
        assertThat(options.nexus().password()).isEqualTo("secret");
        assertThat(options.path()).isEqualTo("/tmp");
        assertThat(options.debug()).isTrue();
    }

    @Test
    void testMavenCommandLine() {
        String[] args = new String[] {
            "--gpg-key",
            "abc",
            "--gpg-passphrase",
            "pass",
            "--nexus-username",
            "who",
            "--nexus-password",
            "secret",
            "--path",
            "/tmp"
        };
        Options options = App.parseOptions(args);
        List<String> mavenCommandLine = App.mavenCommandLine("settings.xml", options);
        assertThat(mavenCommandLine).containsExactly("mvn", "-B", "-ntp", "-s", "settings.xml", "-Pgpg", "deploy");
    }

    @Test
    void testMavenCommandLineWithDebug() {
        String[] args = new String[] {
            "--gpg-key",
            "abc",
            "--gpg-passphrase",
            "pass",
            "--nexus-username",
            "who",
            "--nexus-password",
            "secret",
            "--path",
            "/tmp",
            "--debug"
        };
        Options options = App.parseOptions(args);
        List<String> mavenCommandLine = App.mavenCommandLine("settings.xml", options);
        assertThat(mavenCommandLine)
                .containsExactly("mvn", "--debug", "-B", "-ntp", "-s", "settings.xml", "-Pgpg", "deploy");
    }
}
