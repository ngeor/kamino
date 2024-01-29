package com.github.ngeor.interpreter;

public sealed interface Variant {
    record VInt(int value) implements Variant {}
}
