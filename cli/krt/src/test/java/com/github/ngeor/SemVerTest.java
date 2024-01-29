package com.github.ngeor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("checkstyle:MagicNumber")
class SemVerTest {
    @Test
    void testToString() {
        assertEquals("1.0.0", new SemVer(1, 0, 0).toString());
        assertEquals("0.9.8", new SemVer(0, 9, 8).toString());
        assertEquals("2.3.1-SNAPSHOT", new SemVer(2, 3, 1, "SNAPSHOT").toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void testPreReleaseValidation(String invalidPreRelease) {
        assertThrows(IllegalArgumentException.class, () -> new SemVer(1, 2, 3, invalidPreRelease));
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
        assertArrayEquals(sorted, values);
    }

    @Test
    void testParse() {
        assertEquals(
            new SemVer(1, 2, 3),
            SemVer.parse("1.2.3")
        );
        assertEquals(
            new SemVer(3, 2, 1, "alpha"),
            SemVer.parse("3.2.1-alpha")
        );
        assertEquals(
            new SemVer(11, 12, 31, "alpha-beta"),
            SemVer.parse("11.12.31-alpha-beta")
        );
    }

    @ParameterizedTest
    @CsvSource({
        "1.2.3, major, 2.0.0",
        "0.4.5, minor, 0.5.0",
        "1.3.4, patch, 1.3.5"
    })
    void testBump(String version, String bump, String expected) {
        // arrange
        SemVer original = SemVer.parse(version);
        SemVerBump semVerBump = SemVerBump.parse(bump);

        // act
        SemVer result = original.bump(semVerBump);

        // assert
        assertEquals(expected, result.toString());
    }
}
