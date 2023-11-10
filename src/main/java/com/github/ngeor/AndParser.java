package com.github.ngeor;

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
        if (leftResult.isPresent()) {
            ParseResult<R> rightResult = right.parse(tokenizer);
            if (rightResult.isPresent()) {
                return new ParseResult<>(new Tuple<>(leftResult.value(), rightResult.value()));
            }
        }
        return ParseResult.empty();
    }

    public record Tuple<L, R>(L left, R right) {}
}
