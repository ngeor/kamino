package com.github.ngeor.parser;

public record OrThrowParser<E>(Parser<E> parser) implements Parser<E> {
    @Override
    public ParseResult<E> parse(Tokenizer tokenizer) {
        ParseResult<E> result = parser.parse(tokenizer);
        if (result instanceof ParseResult.None<E>) {
            return ParseResult.err();
        }

        return result;
    }
}
