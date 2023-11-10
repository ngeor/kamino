package com.github.ngeor;

public interface TokenizerListener {
    void tokenReturned(Token token);

    void tokenReverted(Token token);
}
