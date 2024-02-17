package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MavenReleaserTest {
    @Test
    void test() {
        String input = """
        <project>
            <version>1.0-SNAPSHOT</version>
        </project>
        """;

        String expectedOutput = """
        <project>
            <version>1.1</version>
        </project>
        """;

        String actualOutput = MavenReleaser.updateVersion(input, "1.1").replace("\r\n", "\n");
        assertThat(actualOutput).isEqualTo(expectedOutput);
    }
}
