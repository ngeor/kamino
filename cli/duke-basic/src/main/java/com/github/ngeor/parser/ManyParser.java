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
            switch (parser.parse(tokenizer)) {
                case ParseResult.Ok<E> ok:
                    result.add(ok.value());
                    break;
                case ParseResult.None<E> ignored:
                    goOn = false;
                    break;
                case ParseResult.Err<E> e:
                    goOn = false;
                    err = e;
                    break;
            }
        }

        return err.switchTo(() -> ParseResult.of(result));
    }
}
