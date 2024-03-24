package com.github.ngeor.maven.find;

public record FoundState<E, V>(V value) implements State<E, V> {
    @Override
    public boolean isFound() {
        return true;
    }

    @Override
    public boolean hasValue() {
        return value != null;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public State<E, V> visit(E element) {
        return this;
    }
}
