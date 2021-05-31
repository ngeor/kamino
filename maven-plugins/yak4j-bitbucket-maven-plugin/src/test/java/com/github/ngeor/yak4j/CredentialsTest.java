package com.github.ngeor.yak4j;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link Credentials}.
 */
class CredentialsTest {
    @Test
    void nullUsername() {
        assertThatThrownBy(() -> new Credentials(null, "secret"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emptyUsername() {
        assertThatThrownBy(() -> new Credentials("", "secret"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullPassword() {
        assertThatThrownBy(() -> new Credentials("username", null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void emptyPassword() {
        assertThatThrownBy(() -> new Credentials("username", ""))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equality() {
        Credentials a = new Credentials("username", "secret");
        Credentials b = new Credentials("username", "secret");
        assertThat(a).isEqualTo(b);
    }

    @Test
    void differentUsername() {
        Credentials a = new Credentials("username", "secret");
        Credentials b = new Credentials("username 2", "secret");
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void differentPassword() {
        Credentials a = new Credentials("username", "secret");
        Credentials b = new Credentials("username", "secret secret");
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void equalityNull() {
        Credentials a = new Credentials("username", "secret");
        assertThat(a).isNotEqualTo(null);
    }
}
