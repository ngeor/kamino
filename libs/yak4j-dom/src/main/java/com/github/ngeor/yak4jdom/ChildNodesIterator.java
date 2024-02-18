package com.github.ngeor.yak4jdom;

import org.w3c.dom.Node;

/**
 * An iterator over child nodes.
 */
public class ChildNodesIterator extends BaseNodesIterator<Node> {
    ChildNodesIterator(Node parentNode) {
        super(parentNode);
    }

    @Override
    protected Node map(Node node) {
        return node;
    }
}
