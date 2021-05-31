/*
 * NodeTypeRelation.java
 *
 * Created on July 6, 2004, 2:49 PM
 */

package org.jcms.dbfactory;

import org.jcms.model.NodeType;

/**
 * @author ngeor
 */
public class NodeTypeRelation {
    /**
     * Zero or more cardinality.
     */
    public static final int ZERO_OR_MORE = 0;

    private NodeType parent;
    private NodeType child;
    private int cardinality;

    /**
     * Creates a new instance of NodeTypeRelation.
     */
    public NodeTypeRelation(NodeType parent, NodeType child, int cardinality) {
        this.parent = parent;
        this.child = child;
        this.cardinality = cardinality;
    }

    public NodeType getParent() {
        return this.parent;
    }

    public void setParent(NodeType parent) {
        this.parent = parent;
    }

    public NodeType getChild() {
        return this.child;
    }

    public void setChild(NodeType child) {
        this.child = child;
    }

    public int getCardinality() {
        return this.cardinality;
    }

    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }

}
