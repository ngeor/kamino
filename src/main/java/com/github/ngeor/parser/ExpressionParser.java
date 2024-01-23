package com.github.ngeor.parser;

public class ExpressionParser implements Parser<Expression> {
    @Override
    public ParseResult<Expression> parse(Tokenizer tokenizer) {
        return integerLiteral().or(name()).or(unaryExpression()).parse(tokenizer);
    }

    private Parser<Expression> integerLiteral() {
        return new TokenParser()
                .filter(token -> token.kind() == TokenKind.DIGIT)
                .map(Token::value)
                .map(Integer::parseInt)
                .map(Expression.IntegerLiteral::new);
    }

    private Parser<Expression> name() {
        return new TokenParser()
                .filter(token -> token.kind() == TokenKind.LETTER)
                .map(Token::value)
                .map(Expression.Name::new);
    }

    private Parser<Expression> unaryExpression() {
        return unaryOperator()
                .and(new ExpressionParser())
                .map(tuple -> new Expression.UnaryExpression(tuple.left(), tuple.right()));
    }

    private Parser<String> unaryOperator() {
        return new TokenParser()
                .filter(token -> token.kind() == TokenKind.SYMBOL && "-".equals(token.value()))
                .map(Token::value);
    }
}
