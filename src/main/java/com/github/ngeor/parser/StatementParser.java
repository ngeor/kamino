package com.github.ngeor.parser;

public class StatementParser implements Parser<Statement> {
    @Override
    public ParseResult<Statement> parse(Tokenizer tokenizer) {
        return assignment().parse(tokenizer);
    }

    private Parser<Statement> assignment() {
        return name().and(equals())
                .and(new ExpressionParser())
                .map(tuple -> new Statement.Assignment(tuple.left().left(), tuple.right()));
    }

    private Parser<String> name() {
        return Parsers.kind(TokenKind.LETTER)
                .map(Token::value);
    }

    private Parser<Token> equals() {
        return Parsers.symbol('=');
    }
}
