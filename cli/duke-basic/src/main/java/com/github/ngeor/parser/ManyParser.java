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
        boolean goOn = true;
        ParseResult<E> err = ParseResult.empty();
        while (goOn) {
            ParseResult<E> parseResult = parser.parse(tokenizer);
            if (parseResult instanceof ParseResult.Ok<E> ok) {
                result.add(ok.value());
            } else {
                goOn = false;
                if (parseResult instanceof ParseResult.Err<E> e) {
                    err = e;
                }
            }
        }

        return err.switchTo(() -> ParseResult.of(result));
    }
}
