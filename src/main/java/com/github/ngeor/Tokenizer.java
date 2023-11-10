package com.github.ngeor;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntPredicate;

public class Tokenizer {
    private final CharReader reader;
    private final List<TokenizerListener> listeners = new ArrayList<>();

    public Tokenizer(CharReader reader) {
        this.reader = reader;
    }

    public Tokenizer(String input) {
        this(new StringCharReader(input));
    }

    public void addListener(TokenizerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TokenizerListener listener) {
        listeners.remove(listener);
    }

    public Token next() {
        StringBuilder buffer = new StringBuilder();
        EnumMap<TokenKind, IntPredicate> recognizers = new EnumMap<>(TokenKind.class);
        recognizers.put(TokenKind.SPACE, i -> i == ' ' || i == '\t');
        recognizers.put(TokenKind.NEW_LINE, i -> i == '\r' || i == '\n');
        recognizers.put(TokenKind.DIGIT, i -> i >= '0' && i <= '9');
        recognizers.put(TokenKind.LETTER, i -> (i >= 'A' && i <= 'Z') || (i >= 'a' && i <= 'z'));
        Set<TokenKind> eligible = EnumSet.copyOf(recognizers.keySet());
        TokenKind lastMatch = TokenKind.EOF;
        while (reader.hasNext() && !eligible.isEmpty()) {
            buffer.append(reader.next());
            for (Map.Entry<TokenKind, IntPredicate> entry : recognizers.entrySet()) {
                if (buffer.chars().allMatch(entry.getValue())) {
                    lastMatch = entry.getKey();
                } else {
                    eligible.remove(entry.getKey());
                }
            }
        }

        if (lastMatch == TokenKind.EOF) {
            // None of the recognizers matched anything ever.
            // Either it was EOF from the start, or it was a symbol.
            if (!buffer.isEmpty()) {
                lastMatch = TokenKind.SYMBOL;
            }
        } else if (eligible.isEmpty()) {
            // All recognizers were disqualified on the last round.
            // Need to unread the last character.
            reader.undo(buffer.charAt(buffer.length() - 1));
            buffer.deleteCharAt(buffer.length() - 1);
        }

        Token result = new Token(lastMatch, buffer.toString());
        for (TokenizerListener listener : listeners) {
            listener.tokenReturned(result);
        }
        return result;
    }

    public void undo(Token token) {
        undo(token.value());
        for (TokenizerListener listener : listeners) {
            listener.tokenReverted(token);
        }
    }

    private void undo(String value) {
        int i = value.length();
        while (i > 0) {
            i--;
            reader.undo(value.charAt(i));
        }
    }
}
