package com.github.ngeor;

import java.util.function.Predicate;

public record ParseResult<E>(E value) {
    private static final ParseResult<?> EMPTY = new ParseResult<>(null);

    @SuppressWarnings("unchecked")
    public static <E> ParseResult<E> empty() {
        return (ParseResult<E>) EMPTY;
    }

    public ParseResult<E> filter(Predicate<E> predicate) {
        if (predicate.test(value)) {
            return this;
        }

        return empty();
    }
}
