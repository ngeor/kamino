package com.github.ngeor.parser;

public record SurroundedByParser<E, A>(Parser<E> mainParser, Parser<A> aroundParser) implements Parser<E> {

    @Override
    public ParseResult<E> parse(Tokenizer tokenizer) {
        return aroundParser.parse(tokenizer)
            .switchTo(() -> mainParser.parse(tokenizer))
            .flatMap(mainResult -> {
                ParseResult<A> secondAround = aroundParser.parse(tokenizer);
                return secondAround.switchTo(() -> ParseResult.of(mainResult));
            });
    }
}
