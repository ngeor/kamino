package com.github.ngeor.maven.find;

import java.util.Iterator;
import java.util.function.BiFunction;

public interface State<E, T> {
    boolean isFound();

    boolean hasValue();

    T getValue();

    State<E, T> visit(E element);

    default <R, O> State<E, O> compose(State<E, R> right, BiFunction<T, R, O> valueMapper) {
        return CompositeState.compose(this, right, valueMapper);
    }

    default T consume(Iterator<E> it) {
        State<E, T> state = this;
        while (!state.isFound() && it.hasNext()) {
            state = state.visit(it.next());
        }
        return state.getValue();
    }
}
