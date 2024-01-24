package com.github.ngeor.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("MagicNumber")
class ExpressionParserTest {
    private String input;
    private ParseResult<Expression> parseResult;
    private Expression value;

    @Test
    void integerLiteral() {
        input = "42";
        act();
        assertThat(value).isEqualTo(new Expression.IntegerLiteral(42));
    }

    @Test
    void stringLiteral() {
        input = "\"hello, world\"";
        act();
        assertThat(value).isEqualTo(new Expression.StringLiteral("hello, world"));
    }

    @Test
    void emptyStringLiteral() {
        input = "\"\"";
        act();
        assertThat(value).isEqualTo(new Expression.StringLiteral(""));
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
        assertThat(value).isEqualTo(new Expression.Name("Answer"));
    }

    @Test
    void minusIntegerLiteral() {
        input = "-42";
        act();
        assertThat(value)
                .isEqualTo(new Expression.UnaryExpression("-", new Expression.IntegerLiteral(42)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1+2",
        "1 +2",
        "1+ 2",
        "1 + 2"
    })
    void integerLiteralPlusIntegerLiteral(String input) {
        this.input = input;
        act();
        assertThat(value).isEqualTo(
            new Expression.BinaryExpression(
                new Expression.IntegerLiteral(1),
                "+",
                new Expression.IntegerLiteral(2)
            )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1-2",
        "1 -2",
        "1- 2",
        "1 - 2"
    })
    void integerLiteralMinusIntegerLiteral(String input) {
        this.input = input;
        act();
        assertThat(value).isEqualTo(
            new Expression.BinaryExpression(
                new Expression.IntegerLiteral(1),
                "-",
                new Expression.IntegerLiteral(2)
            )
        );
    }

    private void act() {
        parseResult = new ExpressionParser().parse(new Tokenizer(input));
        if (parseResult instanceof ParseResult.Ok<Expression> ok) {
            value = ok.value();
        } else {
            value = null;
        }
    }
}
