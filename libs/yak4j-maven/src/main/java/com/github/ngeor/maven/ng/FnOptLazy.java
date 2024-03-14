package com.github.ngeor.maven.ng;

import java.util.Optional;
import java.util.function.Supplier;

class FnOptLazy<E> extends AbstractLazy<E> implements Supplier<Optional<E>> {
    private final Supplier<Optional<E>> decorated;

    FnOptLazy(Supplier<Optional<E>> decorated) {
        this.decorated = decorated;
    }

    @Override
    public Optional<E> get() {
        return Optional.ofNullable(get1());
    }

    @Override
    protected E doGet() {
        return decorated.get().orElse(null);
    }
}
