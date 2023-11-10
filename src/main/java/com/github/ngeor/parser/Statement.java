package com.github.ngeor.parser;

public sealed interface Statement {
    record Assignment(String name, Expression expression) implements Statement {}
}
