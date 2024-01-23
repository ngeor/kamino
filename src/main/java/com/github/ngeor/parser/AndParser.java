package com.github.ngeor.parser;

public record AndParser<L, R>(Parser<L> left, Parser<R> right) implements Parser<AndParser.Tuple<L, R>> {
    @Override
    public ParseResult<Tuple<L, R>> parse(Tokenizer tokenizer) {
        ParseResult<L> leftResult = left.parse(tokenizer);
        return leftResult.flatMap(leftValue -> {
            ParseResult<R> rightResult = right.parse(tokenizer);
            return rightResult.flatMap(rightValue -> ParseResult.of(new Tuple<>(leftValue, rightValue)));
        });
    }

    public record Tuple<L, R>(L left, R right) {}
}
