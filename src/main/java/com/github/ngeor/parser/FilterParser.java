package com.github.ngeor.parser;

import java.util.function.Predicate;

public class FilterParser<E> implements Parser<E> {
    private final Parser<E> decorated;
    private final Predicate<E> predicate;

    public FilterParser(Parser<E> decorated, Predicate<E> predicate) {
        this.decorated = decorated;
        this.predicate = predicate;
    }

    @Override
    public ParseResult<E> parse(Tokenizer tokenizer) {
        tokenizer.mark();
        ParseResult<E> original = decorated.parse(tokenizer);
        ParseResult<E> filtered = original.filter(predicate);
        tokenizer.undo(filtered instanceof ParseResult.None<E>);
        return filtered;
    }
}
