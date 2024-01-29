package com.github.ngeor.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SuppressWarnings("MagicNumber")
class MapParserTest {
    @Test
    void test() {
        Tokenizer tokenizer = new Tokenizer("Hi");
        Parser<String> parser = new TokenParser().map(Token::value);
        assertThat(parser.parse(tokenizer)).isEqualTo(ParseResult.of("Hi"));
    }
}
