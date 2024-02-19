package com.github.ngeor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.ngeor.versions.SemVer;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class VersionResolverTest {
    private final VersionsProvider versionsProvider = mock(VersionsProvider.class);
    private final VersionResolver versionResolver = new VersionResolver(versionsProvider);

    @ParameterizedTest
    @CsvSource({"major, 2.0.0", "minor, 1.4.0", "patch, 1.3.1"})
    void testBump(String bump, String expectedVersion) throws IOException, InterruptedException {
        // arrange
        when(versionsProvider.listVersions())
                .thenReturn(new TreeSet<>(Set.of(SemVer.parse("1.2.3"), SemVer.parse("1.3.0"))));

        // act
        SemVer result = versionResolver.resolve(bump);

        // assert
        assertEquals(SemVer.parse(expectedVersion), result);
    }

    @Test
    void testExplicitVersionHappyFlow() throws IOException, InterruptedException {
        // arrange
        when(versionsProvider.listVersions())
                .thenReturn(new TreeSet<>(Set.of(SemVer.parse("1.2.3"), SemVer.parse("1.3.0"))));

        // act
        SemVer result = versionResolver.resolve("1.3.1");

        // assert
        assertEquals(SemVer.parse("1.3.1"), result);
    }

    @Test
    void testAlreadyExists() throws IOException, InterruptedException {
        // arrange
        when(versionsProvider.listVersions())
                .thenReturn(new TreeSet<>(Set.of(SemVer.parse("1.2.3"), SemVer.parse("1.3.0"))));

        // act and assert
        assertThrows(IllegalArgumentException.class, () -> versionResolver.resolve("1.3.0"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.3.2", "1.4.1", "1.5.0", "2.0.1", "2.1.0", "3.0.0"})
    void testSemVerGap(String version) throws IOException, InterruptedException {
        // arrange
        when(versionsProvider.listVersions())
                .thenReturn(new TreeSet<>(Set.of(SemVer.parse("1.2.3"), SemVer.parse("1.3.0"))));

        // act and assert
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> versionResolver.resolve(version));
        assertTrue(exception.getMessage().contains("1.3.1"));
        assertTrue(exception.getMessage().contains("1.4.0"));
        assertTrue(exception.getMessage().contains("2.0.0"));
    }
}
