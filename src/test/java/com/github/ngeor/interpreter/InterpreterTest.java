package com.github.ngeor.interpreter;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.parser.ParseResult;
import com.github.ngeor.parser.Parser;
import com.github.ngeor.parser.Statement;
import com.github.ngeor.parser.StatementParser;
import com.github.ngeor.parser.Tokenizer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@SuppressWarnings("MagicNumber")
class InterpreterTest {
    @Test
    @Disabled
    void test() {
        String input = """
        A = 1
        """;
        Tokenizer tokenizer = new Tokenizer(input);
        Parser<Statement> parser = new StatementParser();
        ParseResult<Statement> parseResult = parser.parse(tokenizer);
        Interpreter interpreter = new Interpreter();
        interpreter.run(parseResult.value());
        assertThat(interpreter.getAsInt("A")).isEqualTo(1);
    }
}
