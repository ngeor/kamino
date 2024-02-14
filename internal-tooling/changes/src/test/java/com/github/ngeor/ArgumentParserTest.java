package com.github.ngeor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Test
    void oneRequiredNamedArgumentPresent() {
        ArgumentParser parser = new ArgumentParser();
        parser.addNamedArgument("color", true);
        Map<String, Object> result = parser.parse(new String[] { "--color", "blue" });
        assertThat(result).containsOnlyKeys("color").containsEntry("color", "blue");
    }

    @Test
    void oneRequiredNamedArgumentMissing() {
        ArgumentParser parser = new ArgumentParser();
        parser.addNamedArgument("color", true);
        assertThatThrownBy(() -> parser.parse(new String[0])).hasMessage("color is required");
    }

    @ParameterizedTest
    @ValueSource(booleans = { false, true })
    void oneNamedArgumentValueMissing(boolean required) {
        ArgumentParser parser = new ArgumentParser();
        parser.addNamedArgument("color", required);
        assertThatThrownBy(() -> parser.parse(new String[] { "--color" })).hasMessage("No value for named argument color");
    }

    @Test
    void oneRequiredNamedUnexpectedNamedArgument() {
        ArgumentParser parser = new ArgumentParser();
        parser.addNamedArgument("color", true);
        assertThatThrownBy(() -> parser.parse(new String[] { "--color", "red", "--flag", "blue" })).hasMessage("Unexpected argument --flag");
    }

    @Test
    void oneOptionalNamedArgumentMissing() {
        ArgumentParser parser = new ArgumentParser();
        parser.addNamedArgument("color", false);
        Map<String, Object> result = parser.parse(new String[0]);
        assertThat(result).isEmpty();
    }

    @Test
    void oneFlagArgumentPresent() {
        ArgumentParser parser = new ArgumentParser();
        parser.addFlagArgument("dry-run");
        Map<String, Object> result = parser.parse(new String[] { "--dry-run" });
        assertThat(result).containsOnlyKeys("dry-run").containsEntry("dry-run", true);
    }

    @Test
    void oneFlagArgumentMissing() {
        ArgumentParser parser = new ArgumentParser();
        parser.addFlagArgument("dry-run");
        Map<String, Object> result = parser.parse(new String[0]);
        assertThat(result).isEmpty();
    }
}
