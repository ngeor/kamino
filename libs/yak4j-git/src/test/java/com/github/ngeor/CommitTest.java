package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CommitTest {
    @Test
    void parseWithoutTag() {
        Optional<Commit> result = Commit.parse("f42|2024-02-11||chore: Whatever");
        assertThat(result).contains(new Commit("f42", LocalDate.parse("2024-02-11"), null, "chore: Whatever"));
    }

    @Test
    void parseWithTag() {
        Optional<Commit> result = Commit.parse("b31|2024-02-10|tag: v5.1.0|fix: Whatever");
        assertThat(result).contains(new Commit("b31", LocalDate.parse("2024-02-10"), "v5.1.0", "fix: Whatever"));
    }

    @Test
    void parseWithTagAtHead() {
        // 30efa45c842d444b49ceb49a03e22e2bbdf600de|2024-02-20|HEAD -> main, tag: v1.0|deps: Upgraded mockito
        Optional<Commit> result = Commit.parse(
                "30efa45c842d444b49ceb49a03e22e2bbdf600de|2024-02-20|HEAD -> main, tag: v1.0|deps: Upgraded mockito");
        assertThat(result)
                .contains(new Commit(
                        "30efa45c842d444b49ceb49a03e22e2bbdf600de",
                        LocalDate.parse("2024-02-20"),
                        "v1.0",
                        "deps: Upgraded mockito"));
    }
}
