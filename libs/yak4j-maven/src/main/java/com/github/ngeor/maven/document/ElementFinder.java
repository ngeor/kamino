package com.github.ngeor.maven.document;

import com.github.ngeor.yak4jdom.ElementWrapper;
import java.util.function.Function;
import java.util.function.Predicate;

class ElementFinder<E> implements Predicate<ElementWrapper> {
    private final String elementName;
    private final Function<ElementWrapper, E> valueExtractor;
    private E value;

    public static ElementFinder<String> textFinder(String elementName) {
        return new ElementFinder<>(elementName, e -> e.getTextContentTrimmed().orElse(null));
    }

    public static ElementFinder<ElementWrapper> elementFinder(String elementName) {
        return new ElementFinder<>(elementName, Function.identity());
    }

    private ElementFinder(String elementName, Function<ElementWrapper, E> valueExtractor) {
        this.elementName = elementName;
        this.valueExtractor = valueExtractor;
    }

    @Override
    public boolean test(ElementWrapper element) {
        if (value == null && elementName.equals(element.getNodeName())) {
            value = valueExtractor.apply(element);
            return true;
        }

        return false;
    }

    public E getValue() {
        return value;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public boolean isPresent() {
        return value != null;
    }
}
