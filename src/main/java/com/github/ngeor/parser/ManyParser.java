package com.github.ngeor.parser;

import java.util.ArrayList;
import java.util.List;

public record ManyParser<E>(Parser<E> parser) implements Parser<List<E>> {
    @Override
    public ParseResult<List<E>> parse(Tokenizer tokenizer) {
        List<E> result = new ArrayList<>();
        while (true) {
            tokenizer.mark();

            // TODO upgrade to Java 21 for switch patters
            ParseResult<E> parseResult = parser.parse(tokenizer);
            if (parseResult instanceof ParseResult.Ok<E> ok) {
                tokenizer.accept();
                result.add(ok.value());
            } else if (parseResult instanceof ParseResult.None<E>) {
                tokenizer.undo();
                break;
            } else if (parseResult instanceof ParseResult.Err<E>) {
                tokenizer.accept();
                return (ParseResult<List<E>>) parseResult;
            }
        }

        return ParseResult.of(result);
    }
}
