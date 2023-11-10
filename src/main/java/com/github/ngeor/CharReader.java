package com.github.ngeor;

public interface CharReader {
    boolean hasNext();

    char next();

    void undo(char ch);
}
