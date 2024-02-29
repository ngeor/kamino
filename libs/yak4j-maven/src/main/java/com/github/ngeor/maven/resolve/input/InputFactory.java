package com.github.ngeor.maven.resolve.input;

import java.io.File;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface InputFactory {
    Input load(File pomFile);

    default InputFactory decorate(UnaryOperator<InputFactory> decorator) {
        return decorator.apply(this);
    }
}
