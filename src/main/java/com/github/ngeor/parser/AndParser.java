package com.github.ngeor.parser;

public class AndParser<L, R> implements Parser<AndParser.Tuple<L, R>> {
    private final Parser<L> left;
    private final Parser<R> right;

    public AndParser(Parser<L> left, Parser<R> right) {
        this.left = left;
        this.right = right;
    }

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
