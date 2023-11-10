package com.github.ngeor;

public class TokenParser implements Parser<Token> {
    @Override
    public ParseResult<Token> parse(Tokenizer tokenizer) {
        return new ParseResult<>(tokenizer.next());
    }
}
