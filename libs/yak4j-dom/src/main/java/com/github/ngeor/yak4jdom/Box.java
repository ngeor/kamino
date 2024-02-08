package com.github.ngeor.yak4jdom;

final class Box<E> {
    private E value;

    public boolean isPresent() {
        return value != null;
    }

    public E take() {
        E result = value;
        value = null;
        return result;
    }

    public void set(E newValue) {
        this.value = newValue;
    }
}
