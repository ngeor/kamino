package com.github.ngeor.yak4j;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link PomVersion}.
 */
class PomVersionTest {
    @ParameterizedTest
    @ValueSource(strings = {
        "1.0.0",
        "1.2.3",
        "0.0.0",
        "3.10.123",
        "4.1.2-SNAPSHOT"
    })
    void validVersion(String version) {
        PomVersion pomVersion = new PomVersion(version);
        assertThat(pomVersion.toString()).isEqualTo(version);
    }

    @ParameterizedTest
    @CsvSource({
        "1.0.0, false",
        "1.0.0-SNAPSHOT, true"
    })
    void isSnapshot(String version, boolean expectedSnapshot) {
        PomVersion pomVersion = new PomVersion(version);
        assertThat(pomVersion.isSnapshot()).isEqualTo(expectedSnapshot);
    }

    @ParameterizedTest
    @CsvSource({
        "0.0.0, 0.0.1, 0.1.0, 1.0.0",
        "1.0.0, 1.0.1, 1.1.0, 2.0.0",
        "1.2.3, 1.2.4, 1.3.0, 2.0.0",
        "2.1.3, 2.1.4, 2.2.0, 3.0.0"
    })
    void allowedVersions(String currentVersion, String a, String b, String c) {
        PomVersion pomVersion = new PomVersion(currentVersion);
        List<String> strings = pomVersion.allowedVersions();
        assertThat(strings).containsExactlyInAnyOrder(a, b, c);
    }

    @ParameterizedTest
    @CsvSource({
        "0.0.0, 0.0.1",
        "0.0.0, 0.1.0",
        "0.0.0, 1.0.0",
        "0.0.1, 0.0.2",
        "0.1.0, 0.2.0",
        "1.0.0, 2.0.0",
        "1.2.3, 1.2.4",
        "1.2.3, 1.3.0",
        "1.2.3, 2.0.0"
    })
    void isAllowedNextVersionOf(String currentVersion, String nextVersion) {
        PomVersion a = new PomVersion(currentVersion);
        PomVersion b = new PomVersion(nextVersion);
        assertThat(b.isAllowedNextVersionOf(a)).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
        "0.0.0, 0.0.2",
        "0.0.0, 0.2.0",
        "0.0.0, 2.0.0",
        "0.0.1, 0.0.3",
        "0.1.0, 0.3.0",
        "1.0.0, 3.0.0",
        "1.2.3, 1.2.6",
        "1.2.3, 1.3.3",
        "1.2.3, 2.1.0"
    })
    void isNotAllowedNextVersionOf(String currentVersion, String nextVersion) {
        PomVersion a = new PomVersion(currentVersion);
        PomVersion b = new PomVersion(nextVersion);
        assertThat(b.isAllowedNextVersionOf(a)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "abc",
        "1.0",
        "1",
        "1-SNAPSHOT",
        "1.2-SNAPSHOT",
        "1.0.0.0",
        "1.0.0-snapshot",
        "1.0.0-hello",
        "a 1.0.0",
        "1.0.0-SNAPSHOT-really"
    })
    void invalidVersion(String version) {
        assertThatThrownBy(() -> new PomVersion(version))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("invalid version " + version);
    }

    @Test
    void versionCannotBeNull() {
        assertThatThrownBy(() -> new PomVersion(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("version cannot be empty");
    }

    @Test
    void versionCannotBeEmpty() {
        assertThatThrownBy(() -> new PomVersion(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("version cannot be empty");
    }
}
