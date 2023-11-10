package com.github.ngeor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SuppressWarnings("MagicNumber")
class StatementParserTest {

    @Test
    void assignment() {
        Tokenizer tokenizer = new Tokenizer("A=42");
        Parser<Statement> parser = new StatementParser();
        assertThat(parser.parse(tokenizer))
                .isEqualTo(new ParseResult<>(new Statement.Assignment("A", new Expression.LiteralDigit("42"))));
    }
}
