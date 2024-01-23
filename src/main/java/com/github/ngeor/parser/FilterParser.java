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
        // TODO simplify mark/undo/accept
        tokenizer.mark();
        ParseResult<E> original = decorated.parse(tokenizer);
        ParseResult<E> filtered = original.filter(predicate);
        if (original instanceof ParseResult.Ok<E> && filtered instanceof ParseResult.None<E>) {
            tokenizer.undo();
        } else {
            tokenizer.accept();
        }
        return filtered;
    }
}
