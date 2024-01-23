package com.github.ngeor.parser;

public class OrParser<E> implements Parser<E> {
    private final Parser<E> left;
    private final Parser<E> right;

    public OrParser(Parser<E> left, Parser<E> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public ParseResult<E> parse(Tokenizer tokenizer) {
        tokenizer.mark();
        ParseResult<E> result = left.parse(tokenizer);
        if (result instanceof ParseResult.None<E>) {
            tokenizer.undo();
            return right.parse(tokenizer);
        }
        // Ok or Err win
        tokenizer.accept();
        return result;
    }
}
