package com.github.ngeor.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

@SuppressWarnings("MagicNumber")
class ExpressionParserTest {
    private String input;
    private ParseResult<Expression> parseResult;

    @Test
    void literalDigit() {
        input = "42";
        act();
        assertThat(parseResult.value()).isEqualTo(new Expression.LiteralDigit("42"));
    }

    @Test
    void name() {
        input = "Answer";
        act();
        assertThat(parseResult.value()).isEqualTo(new Expression.Name("Answer"));
    }

    @Test
    void minusLiteralDigit() {
        input = "-42";
        act();
        assertThat(parseResult.value())
                .isEqualTo(new Expression.UnaryExpression("-", new Expression.LiteralDigit("42")));
    }

    private void act() {
        parseResult = new ExpressionParser().parse(new Tokenizer(input));
    }
}
