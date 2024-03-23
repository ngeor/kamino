package com.github.ngeor.maven.dom;

import com.github.ngeor.yak4jdom.ElementWrapper;
import org.apache.commons.lang3.tuple.Pair;

public class CompositeFinder<L, R> implements Finder<ElementWrapper, Pair<L, R>> {
    private final Finder<ElementWrapper, L> left;
    private final Finder<ElementWrapper, R> right;

    public CompositeFinder(Finder<ElementWrapper, L> left, Finder<ElementWrapper, R> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isEmpty() {
        return left.isEmpty() || right.isEmpty();
    }

    @Override
    public boolean accept(ElementWrapper element) {
        return left.acceptIfEmpty(element) || right.acceptIfEmpty(element);
    }

    @Override
    public Pair<L, R> toResult() {
        return Pair.of(left.toResult(), right.toResult());
    }
}
