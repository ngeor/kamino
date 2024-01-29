package com.github.ngeor.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
        assertThat(value).isEqualTo(new Expression.UnaryExpression("-", new Expression.IntegerLiteral(42)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1+2", "1 +2", "1+ 2", "1 + 2"})
    void integerLiteralPlusIntegerLiteral(String input) {
        this.input = input;
        act();
        assertThat(value)
                .isEqualTo(new Expression.BinaryExpression(
                        new Expression.IntegerLiteral(1), "+", new Expression.IntegerLiteral(2)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1-2", "1 -2", "1- 2", "1 - 2"})
    void integerLiteralMinusIntegerLiteral(String input) {
        this.input = input;
        act();
        assertThat(value)
                .isEqualTo(new Expression.BinaryExpression(
                        new Expression.IntegerLiteral(1), "-", new Expression.IntegerLiteral(2)));
    }

    @ParameterizedTest
    @CsvSource({"1 + 4, 1, +, 4", "42 - 10, 42, -, 10", "5 * 7, 5, *, 7", "8 / 2, 8, /, 2"})
    void mathBinary(String expression, int left, String operator, int right) {
        this.input = expression;
        act();
        assertThat(value)
                .isEqualTo(new Expression.BinaryExpression(
                        new Expression.IntegerLiteral(left), operator, new Expression.IntegerLiteral(right)));
    }

    @ParameterizedTest
    @CsvSource({
        "1 + 2 + 3, 1, +, 2, +, 3",
        "1 - 2 + 3, 1, -, 2, +, 3",
        "1 * 2 + 3, 1, *, 2, +, 3",
        "1 / 2 * 3, 1, /, 2, *, 3",
    })
    void mathBinaryUnchangedPriority(
            String expression, int innerLeft, String innerOp, int innerRight, String outerOp, int outerRight) {
        this.input = expression;
        act();
        assertThat(value)
                .isEqualTo(new Expression.BinaryExpression(
                        new Expression.BinaryExpression(
                                new Expression.IntegerLiteral(innerLeft),
                                innerOp,
                                new Expression.IntegerLiteral(innerRight)),
                        outerOp,
                        new Expression.IntegerLiteral(outerRight)));
    }

    @Test
    void fourMemberAddition() {
        this.input = "1 + 2 + 3 + 4";
        act();
        assertThat(value)
                .isEqualTo(new Expression.BinaryExpression(
                        new Expression.BinaryExpression(
                                new Expression.BinaryExpression(
                                        new Expression.IntegerLiteral(1), "+", new Expression.IntegerLiteral(2)),
                                "+",
                                new Expression.IntegerLiteral(3)),
                        "+",
                        new Expression.IntegerLiteral(4)));
    }

    @ParameterizedTest
    @CsvSource({
        "1 + 2 * 3, 1, +, 2, *, 3",
        "1 - 2 / 3, 1, -, 2, /, 3",
    })
    void mathBinaryChangedPriority(
            String expression, int outerLeft, String outerOp, int innerLeft, String innerOp, int innerRight) {
        this.input = expression;
        act();
        assertThat(value)
                .isEqualTo(new Expression.BinaryExpression(
                        new Expression.IntegerLiteral(outerLeft),
                        outerOp,
                        new Expression.BinaryExpression(
                                new Expression.IntegerLiteral(innerLeft),
                                innerOp,
                                new Expression.IntegerLiteral(innerRight))));
    }

    @Test
    void fourMemberChangedPriority() {
        input = "1 + 2 + 3 * 4";
        act();
        assertThat(value)
                .isEqualTo(new Expression.BinaryExpression(
                        new Expression.BinaryExpression(
                                new Expression.IntegerLiteral(1), "+", new Expression.IntegerLiteral(2)),
                        "+",
                        new Expression.BinaryExpression(
                                new Expression.IntegerLiteral(3), "*", new Expression.IntegerLiteral(4))));

        input = "1 + 2 * 3 + 4";
        act();
        assertThat(value)
                .isEqualTo(new Expression.BinaryExpression(
                        new Expression.BinaryExpression(
                                new Expression.IntegerLiteral(1),
                                "+",
                                new Expression.BinaryExpression(
                                        new Expression.IntegerLiteral(2), "*", new Expression.IntegerLiteral(3))),
                        "+",
                        new Expression.IntegerLiteral(4)));

        input = "1 * 2 + 3 + 4";
        act();
        assertThat(value)
                .isEqualTo(new Expression.BinaryExpression(
                        new Expression.BinaryExpression(
                                new Expression.BinaryExpression(
                                        new Expression.IntegerLiteral(1), "*", new Expression.IntegerLiteral(2)),
                                "+",
                                new Expression.IntegerLiteral(3)),
                        "+",
                        new Expression.IntegerLiteral(4)));
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
