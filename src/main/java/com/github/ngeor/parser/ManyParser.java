package com.github.ngeor.parser;

import java.util.ArrayList;
import java.util.List;

public record ManyParser<E>(Parser<E> parser) implements Parser<List<E>> {
    public ManyParser(Parser<E> parser) {
        this.parser = parser.rollingBack();
    }

    @Override
    public ParseResult<List<E>> parse(Tokenizer tokenizer) {
        List<E> result = new ArrayList<>();
        while (true) {
            // TODO upgrade to Java 21 for switch patters
            ParseResult<E> parseResult = parser.parse(tokenizer);
            if (parseResult instanceof ParseResult.Ok<E> ok) {
                result.add(ok.value());
            } else if (parseResult instanceof ParseResult.None<E>) {
                break;
            } else if (parseResult instanceof ParseResult.Err<E>) {
                return (ParseResult<List<E>>) parseResult;
            }
        }

        return ParseResult.of(result);
    }
}
