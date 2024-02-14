package com.github.ngeor;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArgumentParserTest {
    @Test
    void oneRequiredPositionalArgumentPresent() {
        ArgumentParser parser = new ArgumentParser();
        parser.addPositionalArgument("path", true);
        Map<String, Object> result = parser.parse(new String[] { "libs" });
        assertThat(result).containsOnlyKeys("path").containsEntry("path", "libs");
    }

    @Test
    void oneRequiredPositionalArgumentMissing() {
        ArgumentParser parser = new ArgumentParser();
        parser.addPositionalArgument("path", true);
        assertThatThrownBy(() -> parser.parse(new String[0])).hasMessage("path is required");
    }

    @Test
    void oneRequiredPositionalArgumentSuperfluousArguments() {
        ArgumentParser parser = new ArgumentParser();
        parser.addPositionalArgument("path", true);
        assertThatThrownBy(() -> parser.parse(new String[] { "libs", "tests" })).hasMessage("Unexpected argument tests");
    }

    @Test
    void oneOptionalPositionalArgumentMissing() {
        ArgumentParser parser = new ArgumentParser();
        parser.addPositionalArgument("path", false);
        Map<String, Object> result = parser.parse(new String[0]);
        assertThat(result).isEmpty();
    }
}
