package com.github.ngeor.parser;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed interface ParseResult<E> {
    @SuppressWarnings("unchecked")
    default <O> ParseResult<O> map(Function<E, O> mapper) {
        return (ParseResult<O>) this;
    }

    @SuppressWarnings("unchecked")
    default <O> ParseResult<O> flatMap(Function<E, ParseResult<O>> mapper) {
        return (ParseResult<O>) this;
    }

    default ParseResult<E> filter(Predicate<E> predicate) {
        return this;
    }

    default E value() {
        throw new NoSuchElementException();
    }

    static <E> ParseResult<E> of(E value) {
        return new Ok<>(value);
    }

    static <E> ParseResult<E> empty() {
        return new None<>();
    }

    static <E> ParseResult<E> err() {
        return new Err<>();
    }

    ParseResult<E> or(Supplier<ParseResult<E>> supplier);


    record Ok<E>(E value) implements ParseResult<E> {
        @Override
        public <O> ParseResult<O> map(Function<E, O> mapper) {
            return ParseResult.of(mapper.apply(value));
        }

        @Override
        public <O> ParseResult<O> flatMap(Function<E, ParseResult<O>> mapper) {
            return mapper.apply(value);
        }

        @Override
        public ParseResult<E> filter(Predicate<E> predicate) {
            return predicate.test(value) ? this : ParseResult.empty();
        }

        @Override
        public ParseResult<E> or(Supplier<ParseResult<E>> supplier) {
            return this;
        }
    }

    record None<E>() implements ParseResult<E> {
        @Override
        public ParseResult<E> or(Supplier<ParseResult<E>> supplier) {
            return supplier.get();
        }
    }

    record Err<E>() implements ParseResult<E> {
        @Override
        public ParseResult<E> or(Supplier<ParseResult<E>> supplier) {
            return this;
        }
    }
}
