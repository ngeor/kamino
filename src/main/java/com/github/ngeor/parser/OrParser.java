package com.github.ngeor.parser;

import java.util.LinkedList;

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

    private static final class OptionalParser<E> implements Parser<E>, TokenizerListener {
        private final Parser<E> decorated;
        private final LinkedList<Token> tokens = new LinkedList<>();

        private OptionalParser(Parser<E> decorated) {
            this.decorated = decorated;
        }

        @Override
        public ParseResult<E> parse(Tokenizer tokenizer) {
            tokenizer.addListener(this);
            final ParseResult<E> result;
            try {
                result = decorated.parse(tokenizer);
            } finally {
                tokenizer.removeListener(this);
            }
            if (result.isEmpty()) {
                undo(tokenizer);
            }
            return result;
        }

        @Override
        public void tokenReturned(Token token) {
            tokens.addLast(token);
        }

        @Override
        public void tokenReverted(Token token) {
            tokens.removeLast();
        }

        private void undo(Tokenizer tokenizer) {
            while (!tokens.isEmpty()) {
                tokenizer.undo(tokens.removeLast());
            }
        }
    }
}
