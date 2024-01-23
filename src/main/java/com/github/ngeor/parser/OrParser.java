package com.github.ngeor.parser;

public class OrParser<E> implements Parser<E> {
    private final Parser<E> left;
    private final Parser<E> right;

    public OrParser(Parser<E> left, Parser<E> right) {
        this.left = new OptionalParser<>(left);
        this.right = right;
    }

    @Override
    public ParseResult<E> parse(Tokenizer tokenizer) {
        ParseResult<E> result = left.parse(tokenizer);
        if (result.isEmpty()) {
            return right.parse(tokenizer);
        } else {
            return result;
        }
    }

    private static final class OptionalParser<E> implements Parser<E> {
        private final Parser<E> decorated;

        private OptionalParser(Parser<E> decorated) {
            this.decorated = decorated;
        }

        @Override
        public ParseResult<E> parse(Tokenizer tokenizer) {
            tokenizer.mark();
            final ParseResult<E> result = decorated.parse(tokenizer);
            if (result.isEmpty()) {
                tokenizer.undo();
            } else {
                tokenizer.accept();
            }
            return result;
        }
    }
}
