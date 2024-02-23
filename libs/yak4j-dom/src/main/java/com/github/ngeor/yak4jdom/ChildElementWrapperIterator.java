package com.github.ngeor.yak4jdom;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ChildElementWrapperIterator extends BaseNodesIterator<ElementWrapper> {
    ChildElementWrapperIterator(Node parentNode) {
        super(parentNode);
    }

    @Override
    protected ElementWrapper map(Node node) {
        if (node != null && node.getNodeType() == Node.ELEMENT_NODE && node instanceof Element element) {
            return new ElementWrapper(element);
        }
        return null;
    }
}
