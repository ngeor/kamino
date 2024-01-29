package com.github.ngeor.parser;

import java.util.Map;

public sealed interface Expression {
    default Expression toBinary(String operator, Expression right) {
        return new Expression.BinaryExpression(this, operator, right);
    }

    record IntegerLiteral(int value) implements Expression {}

    record StringLiteral(String value) implements Expression {}

    record Name(String value) implements Expression {}

    record UnaryExpression(String operator, Expression expression) implements Expression {}

    record BinaryExpression(Expression left, String operator, Expression right) implements Expression {
        private static final Map<String, Integer> precedence = Map.of(
                "+", 1,
                "-", 1,
                "*", 2,
                "/", 2);

        @Override
        public Expression toBinary(String operator, Expression right) {
            if (precedence.get(this.operator) < precedence.get(operator)) {
                return new BinaryExpression(
                        left,
                        this.operator,
                        // recursion
                        this.right.toBinary(operator, right));
            } else {
                return Expression.super.toBinary(operator, right);
            }
        }
    }
}
