package com.github.ngeor;

import java.util.function.Predicate;

@FunctionalInterface
public interface Parser<E> {
    ParseResult<E> parse(Tokenizer tokenizer);

    default Parser<E> filter(Predicate<E> predicate) {
        return new FilterParser<>(this, predicate);
    }
}
