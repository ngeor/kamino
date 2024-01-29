package com.github.ngeor.parser;

public record OrParser<E>(Parser<E> left, Parser<E> right) implements Parser<E> {
    public OrParser(Parser<E> left, Parser<E> right) {
        this.left = left.rollingBack();
        this.right = right.rollingBack();
    }

    @Override
    public ParseResult<E> parse(Tokenizer tokenizer) {
        ParseResult<E> result = left.parse(tokenizer);
        if (result instanceof ParseResult.None<E>) {
            return right.parse(tokenizer);
        }
        return result;
    }
}
