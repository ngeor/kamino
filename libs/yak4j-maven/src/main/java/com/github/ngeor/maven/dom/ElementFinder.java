package com.github.ngeor.maven.dom;

import com.github.ngeor.yak4jdom.ElementWrapper;
import org.apache.commons.lang3.Validate;

public class ElementFinder implements Finder<ElementWrapper, ElementWrapper> {
    private final String elementName;
    private ElementWrapper value;

    public ElementFinder(String elementName) {
        this.elementName = Validate.notBlank(elementName);
    }

    @Override
    public boolean keepSearching() {
        return value == null;
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public boolean accept(ElementWrapper element) {
        if (elementName.equals(element.getNodeName())) {
            value = element;
            return true;
        }
        return false;
    }

    @Override
    public ElementWrapper toResult() {
        return value;
    }
}
