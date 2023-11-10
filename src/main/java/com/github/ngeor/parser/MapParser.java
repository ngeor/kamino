package com.github.ngeor.parser;

import java.util.function.Function;

public class MapParser<I, O> implements Parser<O> {
    private final Parser<I> decorated;
    private final Function<I, O> mapper;

    public MapParser(Parser<I> decorated, Function<I, O> mapper) {
        this.decorated = decorated;
        this.mapper = mapper;
    }

    @Override
    public ParseResult<O> parse(Tokenizer tokenizer) {
        return decorated.parse(tokenizer).map(mapper);
    }
}
