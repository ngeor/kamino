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
    private Node nextToReturn;

    ChildNodesIterator(Node parentNode) {
        this.parentNode = parentNode;
    }

    @Override
    public boolean hasNext() {
        doNext();
        return nextToReturn != null;
    }

    @Override
    public Node next() {
        doNext();
        Node result = nextToReturn;
        nextToReturn = null;
        return result;
    }

    private void doNext() {
        if (nextToReturn != null || seenEof()) {
            return;
        }
        if (index == 0) {
            nodeList = parentNode.getChildNodes();
            if (nodeList == null) {
                throw new IllegalStateException("Got null nodeList");
            }
        }
        if (index < nodeList.getLength()) {
            nextToReturn = nodeList.item(index);
            index++;
        }
    }

    private boolean seenEof() {
        return nodeList != null && index >= nodeList.getLength();
    }
}
