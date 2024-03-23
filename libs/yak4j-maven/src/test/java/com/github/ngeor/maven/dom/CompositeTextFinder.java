package com.github.ngeor.maven.dom;

import com.github.ngeor.yak4jdom.ElementWrapper;
import org.apache.commons.lang3.tuple.Pair;

public class CompositeTextFinder implements Finder<ElementWrapper, Pair<String, String>> {
    private final TextFinder left;
    private final TextFinder right;

    public CompositeTextFinder(TextFinder left, TextFinder right) {
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
    public Pair<String, String> toResult() {
        return Pair.of(left.toResult().orElse(null), right.toResult().orElse(null));
    }
}
