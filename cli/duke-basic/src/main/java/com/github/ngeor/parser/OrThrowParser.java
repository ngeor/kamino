package com.github.ngeor.parser;

public record OrThrowParser<E>(Parser<E> parser) implements Parser<E> {
    @Override
    public ParseResult<E> parse(Tokenizer tokenizer) {
        return parser.parse(tokenizer).orThrow();
    }
}
