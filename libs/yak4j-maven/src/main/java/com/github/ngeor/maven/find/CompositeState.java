package com.github.ngeor.maven.find;

import java.util.function.BiFunction;

public record CompositeState<E, L, R, T>(State<E, L> left, State<E, R> right, BiFunction<L, R, T> valueBuilder)
        implements State<E, T> {

    public static <E, L, R, T> State<E, T> compose(
            State<E, L> left, State<E, R> right, BiFunction<L, R, T> valueBuilder) {
        if (left.isFound() && right.isFound()) {
            return new FoundState<>(valueBuilder.apply(left.getValue(), right.getValue()));
        }

        return new CompositeState<>(left, right, valueBuilder);
    }

    @Override
    public boolean isFound() {
        return left.isFound() && right.isFound();
    }

    @Override
    public boolean hasValue() {
        return left.hasValue() && right.hasValue();
    }

    @Override
    public T getValue() {
        return valueBuilder.apply(left.getValue(), right.getValue());
    }

    @Override
    public State<E, T> visit(E element) {
        State<E, L> l = left.visit(element);
        if (l != left) {
            return compose(l, right, valueBuilder);
        }

        State<E, R> r = right.visit(element);
        if (r != right) {
            return compose(left, r, valueBuilder);
        }

        return this;
    }
}
