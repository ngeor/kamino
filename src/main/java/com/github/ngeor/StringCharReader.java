package com.github.ngeor;

public class StringCharReader implements CharReader {
    private final String input;
    private int pos;

    public StringCharReader(String input) {
        this.input = input;
        this.pos = 0;
    }

    @Override
    public boolean hasNext() {
        return pos < input.length();
    }

    @Override
    public char next() {
        char result = input.charAt(pos);
        pos++;
        return result;
    }

    @Override
    public void undo(char ch) {
        if (pos <= 0) {
            throw new IndexOutOfBoundsException();
        }

        pos--;
        if (input.charAt(pos) != ch) {
            throw new IllegalArgumentException();
        }
    }
}
