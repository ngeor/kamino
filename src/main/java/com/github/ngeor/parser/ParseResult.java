package com.github.ngeor.parser;

import java.util.function.Function;
import java.util.function.Predicate;

public record ParseResult<E>(E value) {
    private static final ParseResult<?> EMPTY = new ParseResult<>(null);

    @SuppressWarnings("unchecked")
    public static <E> ParseResult<E> empty() {
        return (ParseResult<E>) EMPTY;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public boolean isPresent() {
        return value != null;
    }

    public ParseResult<E> filter(Predicate<E> predicate) {
        if (isPresent() && predicate.test(value)) {
            return this;
        }

        return empty();
    }

    public <O> ParseResult<O> map(Function<E, O> mapper) {
        return isEmpty() ? empty() : new ParseResult<>(mapper.apply(value));
    }
}
