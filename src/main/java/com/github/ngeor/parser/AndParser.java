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
        tokenizer.mark();
        ParseResult<L> leftResult = left.parse(tokenizer);
        ParseResult<Tuple<L, R>> result = leftResult.flatMap(leftValue -> {
            ParseResult<R> rightResult = right.parse(tokenizer);
            return rightResult.flatMap(rightValue -> ParseResult.of(new Tuple<>(leftValue, rightValue)));
        });
        tokenizer.undo(result instanceof ParseResult.None<Tuple<L,R>>);
        return result;
    }

    public record Tuple<L, R>(L left, R right) {}
}
