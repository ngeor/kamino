package com.github.ngeor.interpreter;

import com.github.ngeor.parser.Expression;
import com.github.ngeor.parser.Statement;
import java.util.HashMap;
import java.util.Map;

public class Interpreter {
    private final Map<String, Variant> memory = new HashMap<>();

    public void run(Statement statement) {
        if (statement instanceof Statement.Assignment a) {
            memory.put(a.name(), evaluateExpression(a.expression()));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public Variant get(String variableName) {
        return memory.get(variableName);
    }

    private Variant evaluateExpression(Expression expression) {
        if (expression instanceof Expression.LiteralDigit l) {
            return new Variant.VInt(Integer.parseInt(l.value()));
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
