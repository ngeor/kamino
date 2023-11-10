package com.github.ngeor.parser;

public interface CharReader {
    boolean hasNext();

    char next();

    void undo(char ch);
}
