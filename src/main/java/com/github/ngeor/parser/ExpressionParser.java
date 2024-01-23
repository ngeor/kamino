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
        if (!(singleResult instanceof ParseResult.Ok<Expression>)) {
            return singleResult;
        }

        // try if there is a binary operator next
        ParseResult<String> opResult = binaryOperator()
            .surroundedByOptionalSpace()
            .rollingBack()
            .parse(tokenizer);
        if (opResult instanceof ParseResult.Err<String>) {
            return ParseResult.err(); // TODO (ParseResult<Expression>) opResult;
        }
        if (opResult instanceof ParseResult.None<String>) {
            return singleResult;
        }

        // there is an operator, so it's recursion time
        ParseResult<Expression> rightResult = parse(tokenizer).orThrow();
        return rightResult.map(right -> new Expression.BinaryExpression(singleResult.value(), opResult.value(), right));
    }

    private Parser<Expression> integerLiteral() {
        return new TokenParser()
                .filter(token -> token.kind() == TokenKind.DIGIT)
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
        return new TokenParser().filter(token -> "\"".equals(token.value()));
    }

    private Parser<Token> innerStringToken() {
        return new TokenParser()
            .filter(token -> token.kind() != TokenKind.NEW_LINE && !"\"".equals(token.value()));
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

    private Parser<String> binaryOperator() {
        return new TokenParser()
            .filter(token -> token.kind() == TokenKind.SYMBOL && Set.of("-", "+").contains(token.value()))
            .map(Token::value);
    }
}
