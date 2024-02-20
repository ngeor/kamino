package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
class AppTest {
    @Test
    void testSanitizePath() {
        assertThat(App.sanitizePath(null)).isNull();
        assertThat(App.sanitizePath("")).isNull();
        assertThat(App.sanitizePath("libs/hello")).isEqualTo("libs/hello");
        assertThat(App.sanitizePath("libs/foo/")).isEqualTo("libs/foo");
    }
}
