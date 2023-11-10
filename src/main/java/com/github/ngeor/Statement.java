package com.github.ngeor;

public sealed interface Statement {
    record Assignment(String name, Expression expression) implements Statement {}
}
