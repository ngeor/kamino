package com.github.ngeor.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SuppressWarnings("MagicNumber")
class FilterParserTest {
    @Test
    void success() {
        Tokenizer tokenizer = new Tokenizer("42");
        Parser<Token> parser = new TokenParser().filter(token -> token.kind() == TokenKind.DIGIT);
        assertThat(parser.parse(tokenizer).value()).isEqualTo(new Token(TokenKind.DIGIT, "42"));
    }

    @Test
    void fail() {
        Tokenizer tokenizer = new Tokenizer(",42");
        Parser<Token> parser = new TokenParser().filter(token -> token.kind() == TokenKind.DIGIT);
        assertThat(parser.parse(tokenizer)).isEqualTo(ParseResult.empty());
    }
}
