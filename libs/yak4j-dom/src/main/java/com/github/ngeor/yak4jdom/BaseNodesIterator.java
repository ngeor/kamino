package com.github.ngeor.yak4jdom;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
        E result = nextToReturn.take();
        if (result == null) {
            throw new NoSuchElementException();
        }
        return result;
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
