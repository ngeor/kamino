package com.github.ngeor.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SuppressWarnings("MagicNumber")
class StatementParserTest {

    @Test
    void assignment() {
        Tokenizer tokenizer = new Tokenizer("A=42");
        Parser<Statement> parser = new StatementParser();
        assertThat(parser.parse(tokenizer).value())
                .isEqualTo(new Statement.Assignment("A", new Expression.LiteralDigit("42")));
    }
}
