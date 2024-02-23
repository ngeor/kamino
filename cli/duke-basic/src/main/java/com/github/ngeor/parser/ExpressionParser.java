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
            if (opResult instanceof ParseResult.Ok<String> opOk) {
                ParseResult<Expression> rightResult = nonBinaryParser().parse(tokenizer);
                if (rightResult instanceof ParseResult.Ok<Expression> rightOk) {
                    // combine
                    leftResult = leftResult.map(leftValue -> leftValue.toBinary(opOk.value(), rightOk.value()));
                } else if (rightResult instanceof ParseResult.Err<Expression> rightErr) {
                    return rightErr.cast();
                } else {
                    return ParseResult.err();
                }
            } else if (opResult instanceof ParseResult.Err<String> opErr) {
                return opErr.cast();
            } else {
                goOn = false;
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
