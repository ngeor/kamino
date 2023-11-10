package com.github.ngeor.parser;

public interface TokenizerListener {
    void tokenReturned(Token token);

    void tokenReverted(Token token);
}
