package com.github.ngeor.maven.document;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class Lazy<E> implements Supplier<E> {
    private E value;
    private boolean loaded;

    private final Supplier<E> supplier;
    private final Consumer<E> listener;

    public Lazy(Supplier<E> supplier, Consumer<E> listener) {
        this.supplier = Objects.requireNonNull(supplier);
        this.listener = Objects.requireNonNull(listener);
    }

    public Lazy(Supplier<E> supplier) {
        this(supplier, Function.identity()::apply);
    }

    @Override
    public E get() {
        if (loaded) {
            return value;
        }

        value = supplier.get();
        loaded = true;
        listener.accept(value);
        return value;
    }
}
