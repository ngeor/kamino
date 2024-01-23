package com.github.ngeor.parser;

public class TokenParser implements Parser<Token> {
    @Override
    public ParseResult<Token> parse(Tokenizer tokenizer) {
        return tokenizer.next().map(ParseResult::of).orElseGet(ParseResult::empty);
    }
}
