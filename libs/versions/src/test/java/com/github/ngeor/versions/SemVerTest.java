package com.github.ngeor.versions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("checkstyle:MagicNumber")
class SemVerTest {
    @Test
    void testToString() {
        assertThat(new SemVer(1, 0, 0)).hasToString("1.0.0");
        assertThat(new SemVer(0, 9, 8)).hasToString("0.9.8");
        assertThat(new SemVer(2, 3, 1, "SNAPSHOT")).hasToString("2.3.1-SNAPSHOT");
    }

    @Test
    void testSort() {
        final SemVer[] values = new SemVer[] {
            new SemVer(1, 2, 3),
            new SemVer(1, 0, 1),
            new SemVer(2, 1, 0),
            new SemVer(0, 1, 0),
            new SemVer(0, 1, 0, "SNAPSHOT"),
            new SemVer(0, 2, 0, "SNAPSHOT")
        };
        final SemVer[] sorted = new SemVer[] {
            new SemVer(0, 1, 0, "SNAPSHOT"),
            new SemVer(0, 1, 0),
            new SemVer(0, 2, 0, "SNAPSHOT"),
            new SemVer(1, 0, 1),
            new SemVer(1, 2, 3),
            new SemVer(2, 1, 0)
        };
        Arrays.sort(values);
        assertThat(values).isEqualTo(sorted);
    }

    @Test
    void testParse() {
        assertThat(SemVer.parse("1.2.3")).isEqualTo(new SemVer(1, 2, 3));
        assertThat(SemVer.parse("3.2.1-alpha")).isEqualTo(new SemVer(3, 2, 1, "alpha"));
        assertThat(SemVer.parse("11.12.31-alpha-beta")).isEqualTo(new SemVer(11, 12, 31, "alpha-beta"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2.1", "45.36.90-", "v100.200.300"})
    void testParseFail(String input) {
        assertThatThrownBy(() -> SemVer.parse(input)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({
        "1.2.3, 1, 2, 3, ",
        "0.10.5-alpha, 0, 10, 5, alpha",
    })
    void testTryParseSuccess(String input, int major, int minor, int patch, String preRelease) {
        assertThat(SemVer.tryParse(input)).contains(new SemVer(major, minor, patch, preRelease));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "1.0", "1.0.0-", "v3.2.1"})
    void testTryParseFail(String input) {
        assertThat(SemVer.tryParse(input)).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({"1.2.3, major, 2.0.0", "0.4.5, minor, 0.5.0", "1.3.4, patch, 1.3.5"})
    void testBump(String version, String bump, String expected) {
        // arrange
        SemVer original = SemVer.parse(version);
        SemVerBump semVerBump = SemVerBump.parse(bump);

        // act
        SemVer result = original.bump(semVerBump);

        // assert
        assertThat(result).hasToString(expected);
    }
}
