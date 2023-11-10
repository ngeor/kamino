package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SuppressWarnings("MagicNumber")
class TokenParserTest {
    @Test
    void test() {
        String input = "Hello, world";
        Tokenizer tokenizer = new Tokenizer(new StringCharReader(input));
        Parser<Token> parser = new TokenParser();
        assertThat(parser.parse(tokenizer)).isEqualTo(new ParseResult<>(new Token(TokenKind.LETTER, "Hello")));
        assertThat(parser.parse(tokenizer)).isEqualTo(new ParseResult<>(new Token(TokenKind.SYMBOL, ",")));
        assertThat(parser.parse(tokenizer)).isEqualTo(new ParseResult<>(new Token(TokenKind.SPACE, " ")));
        assertThat(parser.parse(tokenizer)).isEqualTo(new ParseResult<>(new Token(TokenKind.LETTER, "world")));
        assertThat(parser.parse(tokenizer)).isEqualTo(new ParseResult<>(new Token(TokenKind.EOF, "")));
    }
}
