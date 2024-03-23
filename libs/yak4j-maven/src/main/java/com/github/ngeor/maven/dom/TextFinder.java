package com.github.ngeor.maven.dom;

import com.github.ngeor.yak4jdom.ElementWrapper;
import org.apache.commons.lang3.Validate;

import java.util.Optional;

public class TextFinder implements Finder<ElementWrapper, String> {
    private final String elementName;
    private boolean found;
    private String value;

    public TextFinder(String elementName) {
        this.elementName = Validate.notBlank(elementName);
    }

    @Override
    public boolean keepSearching() {
        return !found;
    }

    @Override
    public boolean isEmpty() {
        return !found;
    }

    @Override
    public boolean accept(ElementWrapper element) {
        if (elementName.equals(element.getNodeName())) {
            found = true;
            value = element.getTextContentTrimmed().orElse(null);
            return true;
        }
        return false;
    }

    @Override
    public String toResult() {
        return value;
    }
}
