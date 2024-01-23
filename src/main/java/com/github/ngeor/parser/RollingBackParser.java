package com.github.ngeor.parser;

public record RollingBackParser<E>(Parser<E> parser) implements Parser<E> {
    @Override
    public ParseResult<E> parse(Tokenizer tokenizer) {
        tokenizer.mark();
        ParseResult<E> result = parser.parse(tokenizer);
        if (result instanceof ParseResult.None<E>) {
            tokenizer.undo();
        } else {
            tokenizer.accept();
        }
        return result;
    }
}
