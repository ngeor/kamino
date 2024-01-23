package com.github.ngeor.parser;

public sealed interface Expression {
    record IntegerLiteral(int value) implements Expression {}

    record StringLiteral(String value) implements Expression {}

    record Name(String value) implements Expression {}

    record UnaryExpression(String operation, Expression expression) implements Expression {}
}
