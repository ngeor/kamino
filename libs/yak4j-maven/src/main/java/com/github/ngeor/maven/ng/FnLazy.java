package com.github.ngeor.maven.ng;

import java.util.function.Supplier;

class FnLazy<E> extends AbstractLazy<E> implements Supplier<E> {
    private final Supplier<E> decorated;

    FnLazy(Supplier<E> decorated) {
        this.decorated = decorated;
    }

    @Override
    public E get() {
        return get1();
    }

    @Override
    protected E doGet() {
        return decorated.get();
    }
}
