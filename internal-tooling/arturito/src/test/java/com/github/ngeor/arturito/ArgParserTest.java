package com.github.ngeor.arturito;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ArgParserTest {
    @Test
    void testParse() {
        String[] args = new String[] {"--foo", "bar", "--debug"};
        Map<String, String> parsedArgs = ArgParser.parse(args);
        assertThat(parsedArgs).containsEntry("foo", "bar").containsKey("debug");
    }

    @Test
    void testParseOnlyFlags() {
        String[] args = new String[] {"--foo", "--debug"};
        Map<String, String> parsedArgs = ArgParser.parse(args);
        assertThat(parsedArgs).containsKeys("foo", "debug");
    }

    @Test
    void testParseOnlyValues() {
        String[] args = new String[] {"--foo", "bar", "--level", "1"};
        Map<String, String> parsedArgs = ArgParser.parse(args);
        assertThat(parsedArgs).containsEntry("foo", "bar").containsEntry("level", "1");
    }
}
