package com.github.ngeor.parser;

import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface Parser<E> {
    ParseResult<E> parse(Tokenizer tokenizer);

    default Parser<E> filter(Predicate<E> predicate) {
        return new FilterParser<>(this, predicate);
    }

    default <O> Parser<O> map(Function<E, O> mapper) {
        return new MapParser<>(this, mapper);
    }

    default Parser<E> or(Parser<E> other) {
        return new OrParser<>(this, other);
    }

    default <O> Parser<AndParser.Tuple<E, O>> and(Parser<O> other) {
        return new AndParser<>(this, other);
    }
}
