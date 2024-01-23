package com.github.ngeor.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SuppressWarnings("MagicNumber")
class ExpressionParserTest {
    private String input;
    private ParseResult<Expression> parseResult;

    @Test
    void integerLiteral() {
        input = "42";
        act();
        assertThat(parseResult.value()).isEqualTo(new Expression.IntegerLiteral(42));
    }

    @Test
    void stringLiteral() {
        input = "\"hello, world\"";
        act();
        assertThat(parseResult.value()).isEqualTo(new Expression.StringLiteral("hello, world"));
    }

    @Test
    void emptyStringLiteral() {
        input = "\"\"";
        act();
        assertThat(parseResult.value()).isEqualTo(new Expression.StringLiteral(""));
    }

    @Test
    void unfinishedStringLiteral() {
        input = "\"oops";
        act();
        assertThat(parseResult).isEqualTo(ParseResult.err());
    }

    @Test
    void name() {
        input = "Answer";
        act();
        assertThat(parseResult.value()).isEqualTo(new Expression.Name("Answer"));
    }

    @Test
    void minusIntegerLiteral() {
        input = "-42";
        act();
        assertThat(parseResult.value())
                .isEqualTo(new Expression.UnaryExpression("-", new Expression.IntegerLiteral(42)));
    }

    private void act() {
        parseResult = new ExpressionParser().parse(new Tokenizer(input));
    }
}
