package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CommitTest {
    @Test
    void parseWithoutTag() {
        Optional<Commit> result = Commit.parse("sha|2024-02-11||chore: Whatever");
        assertThat(result).contains(new Commit("sha", LocalDate.parse("2024-02-11"), null, "chore: Whatever"));
    }

    @Test
    void parseWithTag() {
        Optional<Commit> result = Commit.parse("sha2|2024-02-10|tag: v5.1.0|fix: Whatever");
        assertThat(result).contains(new Commit("sha2", LocalDate.parse("2024-02-10"), "v5.1.0", "fix: Whatever"));
    }
}
