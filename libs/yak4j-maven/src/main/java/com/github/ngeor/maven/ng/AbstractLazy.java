package com.github.ngeor.maven.ng;

abstract class AbstractLazy<E> {
    private E value;
    private boolean loaded;

    protected E get1() {
        if (!loaded) {
            loaded = true;
            value = doGet();
        }
        return value;
    }

    protected abstract E doGet();
}
