package com.github.ngeor.parser;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed interface ParseResult<E> {
    <O> ParseResult<O> map(Function<E, O> mapper);

    <O> ParseResult<O> flatMap(Function<E, ParseResult<O>> mapper);

    /**
     * Abandons the current value for the value provided by the given supplier.
     * Can be used in order to discard an unimportant value.
     * Errors are not discarded.
     */
    <O> ParseResult<O> switchTo(Supplier<ParseResult<O>> supplier);

    ParseResult<E> filter(Predicate<E> predicate);

    ParseResult<E> or(Supplier<ParseResult<E>> supplier);

    ParseResult<E> orThrow();

    static <E> ParseResult<E> of(E value) {
        return new Ok<>(value);
    }

    static <E> ParseResult<E> empty() {
        return new None<>();
    }

    static <E> ParseResult<E> err() {
        return new Err<>();
    }

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
        public <O> ParseResult<O> switchTo(Supplier<ParseResult<O>> supplier) {
            return supplier.get();
        }

        @Override
        public ParseResult<E> filter(Predicate<E> predicate) {
            return predicate.test(value) ? this : ParseResult.empty();
        }

        @Override
        public ParseResult<E> or(Supplier<ParseResult<E>> supplier) {
            return this;
        }

        @Override
        public ParseResult<E> orThrow() {
            return this;
        }
    }

    record None<E>() implements ParseResult<E> {
        @Override
        public <O> ParseResult<O> map(Function<E, O> mapper) {
            return cast();
        }

        @Override
        public <O> ParseResult<O> flatMap(Function<E, ParseResult<O>> mapper) {
            return cast();
        }

        @Override
        public <O> ParseResult<O> switchTo(Supplier<ParseResult<O>> supplier) {
            return supplier.get();
        }

        @Override
        public ParseResult<E> filter(Predicate<E> predicate) {
            return cast();
        }

        @Override
        public ParseResult<E> or(Supplier<ParseResult<E>> supplier) {
            return supplier.get();
        }

        @Override
        public ParseResult<E> orThrow() {
            return ParseResult.err();
        }

        @SuppressWarnings("unchecked")
        public <O> ParseResult<O> cast() {
            return (ParseResult<O>) this;
        }
    }

    record Err<E>() implements ParseResult<E> {
        @Override
        public <O> ParseResult<O> map(Function<E, O> mapper) {
            return cast();
        }

        @Override
        public <O> ParseResult<O> flatMap(Function<E, ParseResult<O>> mapper) {
            return cast();
        }

        @Override
        public <O> ParseResult<O> switchTo(Supplier<ParseResult<O>> supplier) {
            return cast();
        }

        @Override
        public ParseResult<E> filter(Predicate<E> predicate) {
            return cast();
        }

        @Override
        public ParseResult<E> or(Supplier<ParseResult<E>> supplier) {
            return this;
        }

        @Override
        public ParseResult<E> orThrow() {
            return cast();
        }

        @SuppressWarnings("unchecked")
        public <O> ParseResult<O> cast() {
            return (ParseResult<O>) this;
        }
    }
}
