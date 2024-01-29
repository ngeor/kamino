package com.github.ngeor.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Tokenizer {
    private final String input;
    private int offset;
    private List<Integer> offsets;

    public Tokenizer(String input) {
        this.input = input;
        this.offset = 0;
        this.offsets = new ArrayList<>();
    }

    public Optional<Token> next() {
        if (offset >= input.length()) {
            return Optional.empty();
        }

        List<TokenKind> eligible = new ArrayList<>(Arrays.asList(TokenKind.values()));

        boolean isFirst = true;
        int maxOffset = offset;
        int maxMatch = offset;
        for (int i = offset; i < input.length(); i++) {
            char ch = input.charAt(i);

            List<TokenKind> pass = new ArrayList<>();
            List<TokenKind> fail = new ArrayList<>();

            while (!eligible.isEmpty()) {
                TokenKind kind = eligible.remove(0);

                boolean matches =
                        switch (kind) {
                            case LETTER -> isAlpha(ch) || (!isFirst && isDigit(ch));
                            case DIGIT -> isDigit(ch);
                            case SPACE -> isSpace(ch);
                            case NEW_LINE -> isNewLine(ch);
                            case SYMBOL -> isFirst;
                            default -> false;
                        };

                if (matches) {
                    pass.add(kind);
                    maxMatch = i;
                } else {
                    fail.add(kind);
                }
            }

            isFirst = false;
            maxOffset = i;

            if (pass.isEmpty()) {
                eligible.addAll(fail);
                break;
            } else {
                eligible.addAll(pass);
            }
        }

        String value = maxOffset == maxMatch ? input.substring(offset) : input.substring(offset, maxOffset);
        TokenKind kind = eligible.remove(0);
        Token token = new Token(kind, value);
        offset += value.length();
        return Optional.of(token);
    }

    public void mark() {
        offsets.add(0, offset);
    }

    public void undo() {
        offset = offsets.remove(0);
    }

    public void accept() {
        offsets.remove(0);
    }

    private boolean isAlpha(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private boolean isSpace(char ch) {
        return ch == ' ' || ch == '\t';
    }

    private boolean isNewLine(char ch) {
        return ch == '\r' || ch == '\n';
    }
}
