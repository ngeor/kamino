package com.github.ngeor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import org.junit.jupiter.api.Test;

class GitTagProviderTest {
    @Test
    void listVersions() throws IOException, InterruptedException {
        // arrange
        Git git = mock(Git.class);
        GitTagPrefix gitTagPrefix = mock(GitTagPrefix.class);
        GitTagProvider gitTagProvider = new GitTagProvider(git, gitTagPrefix);
        when(git.listTags("v*")).thenReturn(List.of("v1.2.3", "v1.2.4", "v1.0.1"));
        when(gitTagPrefix.getPrefix()).thenReturn("v");

        // act
        SortedSet<SemVer> versions = gitTagProvider.listVersions();

        // assert
        assertArrayEquals(
                new SemVer[] {SemVer.parse("1.0.1"), SemVer.parse("1.2.3"), SemVer.parse("1.2.4")}, versions.toArray());
        assertEquals(SemVer.parse("1.2.4"), versions.last());
    }
}
