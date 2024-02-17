package com.github.ngeor.yak4jdom;

import java.util.Iterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An iterator over child nodes.
 */
class ChildNodesIterator implements Iterator<Node> {
    private final Node parentNode;
    private int index;
    private NodeList nodeList;
    private final Box<Node> nextToReturn = new Box<>();

    ChildNodesIterator(Node parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public boolean hasNext() {
        doNext();
        return nextToReturn.isPresent();
    }

    @Override
    public Node next() {
        doNext();
        return nextToReturn.take();
    }

    private void doNext() {
        if (nextToReturn.isPresent() || seenEof()) {
            return;
        }
        if (index == 0) {
            nodeList = parentNode.getChildNodes();
        }
        if (index < nodeList.getLength()) {
            nextToReturn.set(nodeList.item(index));
            index++;
        }
    }

    private boolean seenEof() {
        return nodeList != null && index >= nodeList.getLength();
    }
}
