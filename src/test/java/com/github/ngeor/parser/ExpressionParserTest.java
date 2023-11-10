package com.github.ngeor.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SuppressWarnings("MagicNumber")
class ExpressionParserTest {
    @Test
    void literalDigit() {
        Tokenizer tokenizer = new Tokenizer("42");
        Parser<Expression> parser = new ExpressionParser();
        assertThat(parser.parse(tokenizer)).isEqualTo(new ParseResult<>(new Expression.LiteralDigit("42")));
    }

    @Test
    void name() {
        Tokenizer tokenizer = new Tokenizer("Answer");
        Parser<Expression> parser = new ExpressionParser();
        assertThat(parser.parse(tokenizer)).isEqualTo(new ParseResult<>(new Expression.Name("Answer")));
    }

    @Test
    void minusLiteralDigit() {
        Tokenizer tokenizer = new Tokenizer("-42");
        Parser<Expression> parser = new ExpressionParser();
        assertThat(parser.parse(tokenizer))
                .isEqualTo(new ParseResult<>(new Expression.UnaryExpression("-", new Expression.LiteralDigit("42"))));
    }
}
