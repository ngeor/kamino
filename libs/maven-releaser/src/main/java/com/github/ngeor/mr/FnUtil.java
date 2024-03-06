package com.github.ngeor.mr;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

final class FnUtil {
    private FnUtil() {}

    public static <E> UnaryOperator<E> toUnaryOperator(Consumer<E> consumer) {
        return input -> {
            consumer.accept(input);
            return input;
        };
    }
}
