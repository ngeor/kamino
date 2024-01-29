package com.github.ngeor.parser;

import java.util.Set;
import java.util.stream.Collectors;

public class ExpressionParser implements Parser<Expression> {

    private Parser<Expression> nonBinaryParser() {
        return integerLiteral().or(stringLiteral()).or(name()).or(unaryExpression());
    }

    @Override
    public ParseResult<Expression> parse(Tokenizer tokenizer) {
        ParseResult<Expression> leftResult = nonBinaryParser().parse(tokenizer);

        if (!(leftResult instanceof ParseResult.Ok<Expression>)) {
            return leftResult;
        }

        boolean goOn = true;
        while (goOn) {
            // try if there is a binary operator next
            ParseResult<String> opResult =
                    binaryOperator().surroundedByOptionalSpace().rollingBack().parse(tokenizer);

            switch (opResult) {
                case ParseResult.Ok<String> opOk:
                    ParseResult<Expression> rightResult = nonBinaryParser().parse(tokenizer);
                    switch (rightResult) {
                        case ParseResult.Ok<Expression> rightOk:
                            // combine
                            leftResult = leftResult.map(leftValue -> leftValue.toBinary(opOk.value(), rightOk.value()));
                            break;
                        case ParseResult.None<Expression> ignored:
                            return ParseResult.err();
                        case ParseResult.Err<Expression> rightErr:
                            return rightErr.cast();
                    }
                    break;
                case ParseResult.None<String> ignored:
                    goOn = false;
                    break;
                case ParseResult.Err<String> opErr:
                    return opErr.cast();
            }
        }

        return leftResult;
    }

    private Parser<Expression> integerLiteral() {
        return Parsers.kind(TokenKind.DIGIT)
                .map(Token::value)
                .map(Integer::parseInt)
                .map(Expression.IntegerLiteral::new);
    }

    private Parser<Expression> stringLiteral() {
        return quote().andKeepingRight(innerStringToken().many())
                .andKeepingLeft(quote().orThrow())
                .map(tokens -> tokens.stream().map(Token::value).collect(Collectors.joining()))
                .map(Expression.StringLiteral::new);
    }

    private Parser<Token> quote() {
        return Parsers.symbol('"');
    }

    private Parser<Token> innerStringToken() {
        return new TokenParser().filter(token -> token.kind() != TokenKind.NEW_LINE && !"\"".equals(token.value()));
    }

    private Parser<Expression> name() {
        return Parsers.kind(TokenKind.LETTER).map(Token::value).map(Expression.Name::new);
    }

    private Parser<Expression> unaryExpression() {
        return unaryOperator()
                .and(new ExpressionParser())
                .map(tuple -> new Expression.UnaryExpression(tuple.left(), tuple.right()));
    }

    private Parser<String> unaryOperator() {
        return Parsers.symbol('-').map(Token::value);
    }

    private Parser<String> binaryOperator() {
        return new TokenParser()
                .filter(token -> token.kind() == TokenKind.SYMBOL
                        && Set.of("+", "-", "*", "/").contains(token.value()))
                .map(Token::value);
    }
}
