package com.github.ngeor.parser;

public final class Parsers {
    private Parsers() {}

    public static Parser<Token> anyToken() {
        return new TokenParser();
    }

    public static Parser<Token> symbol(char ch) {
        return anyToken()
                .filter(token ->
                        token.kind() == TokenKind.SYMBOL && token.value().equals(String.valueOf(ch)));
    }

    public static Parser<Token> kind(TokenKind kind) {
        return anyToken().filter(token -> token.kind() == kind);
    }
}
