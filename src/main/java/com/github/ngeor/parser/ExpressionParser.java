package com.github.ngeor.parser;

import java.util.Set;
import java.util.stream.Collectors;

public class ExpressionParser implements Parser<Expression> {
    @Override
    public ParseResult<Expression> parse(Tokenizer tokenizer) {
        ParseResult<Expression> singleResult = integerLiteral()
            .or(stringLiteral())
            .or(name())
            .or(unaryExpression())
            .parse(tokenizer);
        return singleResult.flatMap(leftValue -> {
            // try if there is a binary operator next
            ParseResult<String> opResult = binaryOperator()
                .surroundedByOptionalSpace()
                .rollingBack()
                .parse(tokenizer);

            return switch (opResult) {
                case ParseResult.Ok<String> ok -> {
                    // there is an operator, so it's recursion time
                    ParseResult<Expression> rightResult = parse(tokenizer).orThrow();
                    yield rightResult.map(right -> new Expression.BinaryExpression(leftValue, ok.value(), right));
                }
                // no operator, return the already parsed expression
                case ParseResult.None<String> ignored -> singleResult;
                // errors take precedence
                case ParseResult.Err<String> err -> err.cast();
            };
        });
    }

    private Parser<Expression> integerLiteral() {
        return Parsers.kind(TokenKind.DIGIT)
                .map(Token::value)
                .map(Integer::parseInt)
                .map(Expression.IntegerLiteral::new);
    }

    private Parser<Expression> stringLiteral() {
        return quote()
            .andKeepingRight(innerStringToken().many())
            .andKeepingLeft(quote().orThrow())
            .map(tokens -> tokens.stream().map(Token::value).collect(Collectors.joining()))
            .map(Expression.StringLiteral::new);
    }

    private Parser<Token> quote() {
        return Parsers.symbol('"');
    }

    private Parser<Token> innerStringToken() {
        return new TokenParser()
            .filter(token -> token.kind() != TokenKind.NEW_LINE && !"\"".equals(token.value()));
    }

    private Parser<Expression> name() {
        return Parsers.kind(TokenKind.LETTER)
                .map(Token::value)
                .map(Expression.Name::new);
    }

    private Parser<Expression> unaryExpression() {
        return unaryOperator()
                .and(new ExpressionParser())
                .map(tuple -> new Expression.UnaryExpression(tuple.left(), tuple.right()));
    }

    private Parser<String> unaryOperator() {
        return Parsers.symbol('-')
                .map(Token::value);
    }

    private Parser<String> binaryOperator() {
        return new TokenParser()
            .filter(token -> token.kind() == TokenKind.SYMBOL && Set.of("+", "-", "*", "/").contains(token.value()))
            .map(Token::value);
    }
}
