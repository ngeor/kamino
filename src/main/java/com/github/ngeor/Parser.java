package com.github.ngeor;

@FunctionalInterface
public interface Parser<E> {
    ParseResult<E> parse(Tokenizer tokenizer);
}
