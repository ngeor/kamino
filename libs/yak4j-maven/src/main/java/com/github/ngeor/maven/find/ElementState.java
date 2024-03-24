package com.github.ngeor.maven.find;

import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.function.Function;

public record ElementState<V>(String elementName, boolean found, V value, Function<ElementWrapper, V> valueExtractor)
        implements State<ElementWrapper, V> {
    public ElementState(String elementName, Function<ElementWrapper, V> valueExtractor) {
        this(elementName, false, null, valueExtractor);
    }

    @Override
    public boolean isFound() {
        return found;
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
    public State<ElementWrapper, V> visit(ElementWrapper element) {
        if (found || !elementName.equals(element.getNodeName())) {
            return this;
        }
        return new FoundState<>(valueExtractor.apply(element));
    }
}
