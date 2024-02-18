package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
class AppTest {
    @Test
    void testSanitizePath() {
        assertThat(App.sanitize(null)).isNull();
        assertThat(App.sanitize("")).isNull();
        assertThat(App.sanitize("libs/hello")).isEqualTo("libs/hello");
        assertThat(App.sanitize("libs/foo/")).isEqualTo("libs/foo");
    }
}
