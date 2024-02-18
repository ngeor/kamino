package com.github.ngeor.yak4jdom;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;
import java.util.Objects;

public abstract class BaseNodesIterator<E> implements Iterator<E> {
    private final Node parentNode;
    private int index;
    private NodeList nodeList;
    private final Box<E> nextToReturn = new Box<>();

    protected BaseNodesIterator(Node parentNode) {
        this.parentNode = Objects.requireNonNull(parentNode);
    }

    @Override
    public boolean hasNext() {
        doNext();
        return nextToReturn.isPresent();
    }

    @Override
    public E next() {
        doNext();
        return nextToReturn.take();
    }

    private void doNext() {
        if (nodeList == null) {
            nodeList = parentNode.getChildNodes();
        }

        while (!nextToReturn.isPresent()) {

            if (index >= nodeList.getLength()) {
                return;
            }

            nextToReturn.set(map(nodeList.item(index)));
            index++;
        }
    }

    protected abstract E map(Node node);
}
