package com.github.ngeor.parser;

public sealed interface Expression {
    record LiteralDigit(String value) implements Expression {}

    record Name(String value) implements Expression {}

    record UnaryExpression(String operation, Expression expression) implements Expression {}
}
